use crate::config::{CloudflareConfig, RecordConfig, RecordType, ZoneConfig};
use reqwest::blocking::Client;
use serde::{Deserialize, Serialize};
use std::net::IpAddr;

#[derive(Debug)]
pub struct CloudflareClient {
    client: Client,
    api_base_url: String,
}

#[derive(Debug, Clone, Copy, Eq, PartialEq)]
pub enum UpdateOutcome {
    Unchanged,
    Updated,
    WouldUpdate,
}

impl CloudflareClient {
    pub fn new(config: &CloudflareConfig) -> Result<Self, String> {
        let mut headers = reqwest::header::HeaderMap::new();
        let authorization = format!("Bearer {}", config.api_token);
        headers.insert(
            reqwest::header::AUTHORIZATION,
            authorization.parse().map_err(|error| {
                format!("Cloudflare API token contains invalid characters: {error}")
            })?,
        );

        let client = Client::builder()
            .default_headers(headers)
            .timeout(config.timeout())
            .build()
            .map_err(|error| format!("failed to build Cloudflare HTTP client: {error}"))?;

        Ok(Self {
            client,
            api_base_url: config.api_base_url.trim_end_matches('/').to_owned(),
        })
    }

    pub fn update_record(
        &self,
        zone: &ZoneConfig,
        configured_record: &RecordConfig,
        address: IpAddr,
        dry_run: bool,
    ) -> Result<UpdateOutcome, String> {
        if !configured_record.record_type.accepts(address) {
            return Err(format!(
                "{address} cannot be assigned to a {} record",
                configured_record.record_type
            ));
        }

        let record = self.find_record(zone, configured_record)?;
        if record.content == address.to_string() {
            return Ok(UpdateOutcome::Unchanged);
        }
        if dry_run {
            return Ok(UpdateOutcome::WouldUpdate);
        }

        let url = format!(
            "{}/zones/{}/dns_records/{}",
            self.api_base_url, zone.zone_id, record.id
        );
        let response = self
            .client
            .patch(url)
            .json(&UpdateRecordRequest {
                content: address.to_string(),
            })
            .send()
            .map_err(|error| format!("Cloudflare update request failed: {error}"))?
            .error_for_status()
            .map_err(|error| format!("Cloudflare rejected the DNS update: {error}"))?;
        let envelope: ApiEnvelope<DnsRecord> = response
            .json()
            .map_err(|error| format!("invalid Cloudflare update response: {error}"))?;
        let updated = envelope.into_result()?;

        if updated.content != address.to_string() {
            return Err(format!(
                "Cloudflare returned {} after updating the record to {address}",
                updated.content
            ));
        }

        Ok(UpdateOutcome::Updated)
    }

    fn find_record(
        &self,
        zone: &ZoneConfig,
        configured_record: &RecordConfig,
    ) -> Result<DnsRecord, String> {
        let url = format!("{}/zones/{}/dns_records", self.api_base_url, zone.zone_id);
        let response = self
            .client
            .get(url)
            .query(&[
                ("type", configured_record.record_type.to_string()),
                ("name.exact", configured_record.name.clone()),
            ])
            .send()
            .map_err(|error| format!("Cloudflare record lookup failed: {error}"))?
            .error_for_status()
            .map_err(|error| format!("Cloudflare rejected the record lookup: {error}"))?;
        let envelope: ApiEnvelope<Vec<DnsRecord>> = response
            .json()
            .map_err(|error| format!("invalid Cloudflare list response: {error}"))?;
        let mut records = envelope.into_result()?;

        match records.len() {
            0 => Err(format!(
                "{} record {} was not found in zone {}",
                configured_record.record_type, configured_record.name, zone.zone_id
            )),
            1 => Ok(records.remove(0)),
            count => Err(format!(
                "Cloudflare returned {count} {} records named {} in zone {}",
                configured_record.record_type, configured_record.name, zone.zone_id
            )),
        }
    }
}

#[derive(Debug, Deserialize)]
struct ApiEnvelope<T> {
    success: bool,
    result: Option<T>,
    #[serde(default)]
    errors: Vec<ApiError>,
}

impl<T> ApiEnvelope<T> {
    fn into_result(self) -> Result<T, String> {
        if self.success {
            return self
                .result
                .ok_or_else(|| "Cloudflare response succeeded without a result".to_owned());
        }

        let message = self
            .errors
            .iter()
            .map(|error| format!("{}: {}", error.code, error.message))
            .collect::<Vec<_>>()
            .join("; ");
        Err(format!("Cloudflare API error: {message}"))
    }
}

#[derive(Debug, Deserialize)]
struct ApiError {
    code: u64,
    message: String,
}

#[derive(Debug, Deserialize)]
struct DnsRecord {
    id: String,
    content: String,
    #[allow(dead_code)]
    name: String,
    #[serde(rename = "type")]
    #[allow(dead_code)]
    record_type: RecordType,
}

#[derive(Debug, Serialize)]
struct UpdateRecordRequest {
    content: String,
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::io::{Read, Write};
    use std::net::TcpListener;
    use std::sync::mpsc;
    use std::thread;
    use std::time::Duration;

    #[test]
    fn extracts_a_successful_api_result() {
        let envelope: ApiEnvelope<DnsRecord> = serde_json::from_str(
            r#"{
                "success": true,
                "errors": [],
                "result": {
                    "id": "record-id",
                    "name": "home.example.com",
                    "type": "A",
                    "content": "192.0.2.1"
                }
            }"#,
        )
        .unwrap();

        assert_eq!(envelope.into_result().unwrap().content, "192.0.2.1");
    }

    #[test]
    fn reports_cloudflare_api_errors() {
        let envelope: ApiEnvelope<DnsRecord> = serde_json::from_str(
            r#"{
                "success": false,
                "errors": [{"code": 10000, "message": "Authentication error"}],
                "result": null
            }"#,
        )
        .unwrap();

        let error = envelope.into_result().unwrap_err();
        assert!(error.contains("10000: Authentication error"));
    }

    #[test]
    fn looks_up_and_patches_a_record_synchronously() {
        let listener = TcpListener::bind("127.0.0.1:0").unwrap();
        let address = listener.local_addr().unwrap();
        let (sender, receiver) = mpsc::channel();
        let server = thread::spawn(move || {
            let responses = [
                r#"{"success":true,"errors":[],"result":[{"id":"record-id","name":"home.example.com","type":"A","content":"192.0.2.1"}]}"#,
                r#"{"success":true,"errors":[],"result":{"id":"record-id","name":"home.example.com","type":"A","content":"192.0.2.2"}}"#,
            ];

            for body in responses {
                let (mut stream, _) = listener.accept().unwrap();
                stream
                    .set_read_timeout(Some(Duration::from_secs(2)))
                    .unwrap();
                let mut request = Vec::new();
                let mut buffer = [0; 4096];
                let read = stream.read(&mut buffer).unwrap();
                request.extend_from_slice(&buffer[..read]);
                sender.send(String::from_utf8(request).unwrap()).unwrap();

                write!(
                    stream,
                    "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: {}\r\nConnection: close\r\n\r\n{}",
                    body.len(),
                    body
                )
                .unwrap();
            }
        });

        let client = CloudflareClient::new(&CloudflareConfig {
            api_token: "test-token".to_owned(),
            api_base_url: format!("http://{address}"),
            timeout_seconds: 2,
        })
        .unwrap();
        let zone = ZoneConfig {
            zone_id: "zone-id".to_owned(),
            records: Vec::new(),
        };
        let record = RecordConfig {
            name: "home.example.com".to_owned(),
            record_type: RecordType::A,
        };

        let outcome = client
            .update_record(&zone, &record, "192.0.2.2".parse().unwrap(), false)
            .unwrap();

        assert_eq!(outcome, UpdateOutcome::Updated);
        let lookup = receiver.recv().unwrap();
        assert!(lookup.starts_with("GET /zones/zone-id/dns_records?"));
        assert!(lookup.contains("type=A"));
        assert!(lookup.contains("name.exact=home.example.com"));
        assert!(
            lookup
                .to_ascii_lowercase()
                .contains("authorization: bearer test-token")
        );
        let update = receiver.recv().unwrap();
        assert!(update.starts_with("PATCH /zones/zone-id/dns_records/record-id "));
        assert!(update.contains(r#"{"content":"192.0.2.2"}"#));
        server.join().unwrap();
    }
}
