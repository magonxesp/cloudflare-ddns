use crate::config::RecordType;
use reqwest::blocking::Client;
use std::net::IpAddr;
use std::time::Duration;

const IPV4_URL: &str = "https://api.ipify.org";
const IPV6_URL: &str = "https://api6.ipify.org";
const REQUEST_TIMEOUT: Duration = Duration::from_secs(10);

pub fn fetch(record_type: RecordType) -> Result<IpAddr, String> {
    let client = Client::builder()
        .timeout(REQUEST_TIMEOUT)
        .build()
        .map_err(|error| format!("failed to build public IP HTTP client: {error}"))?;
    let response = client
        .get(url_for(record_type))
        .send()
        .map_err(|error| format!("public IP request failed: {error}"))?
        .error_for_status()
        .map_err(|error| format!("public IP service returned an error: {error}"))?;
    let body = response
        .text()
        .map_err(|error| format!("failed to read public IP response: {error}"))?;

    parse(record_type, &body)
}

fn url_for(record_type: RecordType) -> &'static str {
    match record_type {
        RecordType::A => IPV4_URL,
        RecordType::Aaaa => IPV6_URL,
    }
}

fn parse(record_type: RecordType, body: &str) -> Result<IpAddr, String> {
    let address: IpAddr = body
        .trim()
        .parse()
        .map_err(|_| format!("public IP service returned an invalid address: {body:?}"))?;

    if !record_type.accepts(address) {
        return Err(format!(
            "public IP service returned {address}, which is not a {record_type} address"
        ));
    }

    Ok(address)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn parses_ipv4_with_whitespace() {
        assert_eq!(
            parse(RecordType::A, " 192.0.2.10\n").unwrap(),
            "192.0.2.10".parse::<IpAddr>().unwrap()
        );
    }

    #[test]
    fn rejects_the_wrong_address_family() {
        let error = parse(RecordType::A, "2001:db8::1").unwrap_err();
        assert!(error.contains("not a A address"));
    }

    #[test]
    fn rejects_non_addresses() {
        assert!(parse(RecordType::A, "<html>error</html>").is_err());
    }
}
