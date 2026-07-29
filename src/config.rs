use serde::Deserialize;
use std::env;
use std::fmt;
use std::fs;
use std::hash::Hash;
use std::net::IpAddr;
use std::path::{Path, PathBuf};
use std::time::Duration;

const CONFIG_FILE_NAME: &str = "config.yaml";

#[derive(Debug, Deserialize)]
#[serde(deny_unknown_fields)]
pub struct Config {
    #[serde(default)]
    pub logging: LoggingConfig,
    pub cloudflare: CloudflareConfig,
    pub zones: Vec<ZoneConfig>,
}

impl Config {
    pub fn load(explicit_path: Option<&Path>) -> Result<(Self, PathBuf), String> {
        let path = resolve_config_path(explicit_path)?;
        let content = fs::read_to_string(&path)
            .map_err(|error| format!("failed to read configuration {}: {error}", path.display()))?;
        let config: Self = serde_yaml::from_str(&content)
            .map_err(|error| format!("invalid configuration {}: {error}", path.display()))?;
        config.validate()?;
        Ok((config, path))
    }

    fn validate(&self) -> Result<(), String> {
        if self.zones.is_empty() {
            return Err("configuration must contain at least one zone".to_owned());
        }
        if self.cloudflare.timeout_seconds == 0 {
            return Err("cloudflare.timeout_seconds must be greater than zero".to_owned());
        }
        if self.cloudflare.api_base_url.trim().is_empty() {
            return Err("cloudflare.api_base_url cannot be empty".to_owned());
        }

        if self.cloudflare.api_token.trim().is_empty() {
            return Err("cloudflare.api_token cannot be empty".to_owned());
        }

        for zone in &self.zones {
            if zone.zone_id.trim().is_empty() {
                return Err("zone_id cannot be empty".to_owned());
            }
            if zone.records.is_empty() {
                return Err(format!(
                    "zone {} must contain at least one record",
                    zone.zone_id
                ));
            }
            for record in &zone.records {
                if record.name.trim().is_empty() {
                    return Err(format!(
                        "record name cannot be empty in zone {}",
                        zone.zone_id
                    ));
                }
            }
        }

        Ok(())
    }
}

#[derive(Debug, Deserialize)]
#[serde(default, deny_unknown_fields)]
pub struct LoggingConfig {
    pub level: LogLevel,
    pub file: Option<PathBuf>,
}

impl Default for LoggingConfig {
    fn default() -> Self {
        Self {
            level: LogLevel::Info,
            file: None,
        }
    }
}

#[derive(Debug, Clone, Copy, Deserialize)]
#[serde(rename_all = "lowercase")]
pub enum LogLevel {
    Trace,
    Debug,
    Info,
    Warn,
    Error,
}

impl From<LogLevel> for log::LevelFilter {
    fn from(level: LogLevel) -> Self {
        match level {
            LogLevel::Trace => Self::Trace,
            LogLevel::Debug => Self::Debug,
            LogLevel::Info => Self::Info,
            LogLevel::Warn => Self::Warn,
            LogLevel::Error => Self::Error,
        }
    }
}

#[derive(Debug, Deserialize)]
#[serde(default, deny_unknown_fields)]
pub struct CloudflareConfig {
    pub api_token: String,
    pub api_base_url: String,
    pub timeout_seconds: u64,
}

impl Default for CloudflareConfig {
    fn default() -> Self {
        Self {
            api_token: String::new(),
            api_base_url: "https://api.cloudflare.com/client/v4".to_owned(),
            timeout_seconds: 15,
        }
    }
}

impl CloudflareConfig {
    pub fn timeout(&self) -> Duration {
        Duration::from_secs(self.timeout_seconds)
    }
}

#[derive(Debug, Deserialize)]
#[serde(deny_unknown_fields)]
pub struct ZoneConfig {
    pub zone_id: String,
    pub records: Vec<RecordConfig>,
}

#[derive(Debug, Deserialize)]
#[serde(deny_unknown_fields)]
pub struct RecordConfig {
    pub name: String,
    #[serde(rename = "type", default)]
    pub record_type: RecordType,
}

#[derive(Debug, Clone, Copy, Default, Deserialize, Eq, Hash, PartialEq)]
pub enum RecordType {
    #[default]
    A,
    #[serde(rename = "AAAA")]
    Aaaa,
}

impl RecordType {
    pub fn accepts(self, address: IpAddr) -> bool {
        matches!(
            (self, address),
            (Self::A, IpAddr::V4(_)) | (Self::Aaaa, IpAddr::V6(_))
        )
    }
}

impl fmt::Display for RecordType {
    fn fmt(&self, formatter: &mut fmt::Formatter<'_>) -> fmt::Result {
        formatter.write_str(match self {
            Self::A => "A",
            Self::Aaaa => "AAAA",
        })
    }
}

pub fn resolve_config_path(explicit_path: Option<&Path>) -> Result<PathBuf, String> {
    if let Some(path) = explicit_path {
        return path
            .is_file()
            .then(|| path.to_path_buf())
            .ok_or_else(|| format!("configuration file {} does not exist", path.display()));
    }

    config_candidates()
        .into_iter()
        .find(|path| path.is_file())
        .ok_or_else(|| {
            "configuration file not found; use --config or create \
             ~/.config/cloudflare-ddns/config.yaml"
                .to_owned()
        })
}

fn config_candidates() -> Vec<PathBuf> {
    let mut paths = Vec::new();

    if let Some(home) = env::var_os("HOME").map(PathBuf::from) {
        paths.push(home.join(".cloudflare-ddns").join(CONFIG_FILE_NAME));
    }
    if let Some(xdg) = env::var_os("XDG_CONFIG_HOME").map(PathBuf::from) {
        paths.push(xdg.join("cloudflare-ddns").join(CONFIG_FILE_NAME));
    } else if let Some(home) = env::var_os("HOME").map(PathBuf::from) {
        paths.push(
            home.join(".config")
                .join("cloudflare-ddns")
                .join(CONFIG_FILE_NAME),
        );
    }
    paths.push(
        PathBuf::from("/etc")
            .join("cloudflare-ddns")
            .join(CONFIG_FILE_NAME),
    );

    paths
}

#[cfg(test)]
mod tests {
    use super::*;

    const CONFIG: &str = r#"
cloudflare:
  api_token: test-token
zones:
  - zone_id: zone-id
    records:
      - name: home.example.com
      - name: vpn.example.com
        type: AAAA
"#;

    #[test]
    fn parses_defaults_and_record_types() {
        let config: Config = serde_yaml::from_str(CONFIG).unwrap();

        assert_eq!(config.cloudflare.timeout_seconds, 15);
        assert_eq!(config.zones[0].records[0].record_type, RecordType::A);
        assert_eq!(config.zones[0].records[1].record_type, RecordType::Aaaa);
        config.validate().unwrap();
    }

    #[test]
    fn rejects_unknown_settings() {
        let invalid = CONFIG.replace("cloudflare:", "cloudflare:\n  imaginary_setting: true");

        assert!(serde_yaml::from_str::<Config>(&invalid).is_err());
    }

    #[test]
    fn record_types_only_accept_the_matching_address_family() {
        let ipv4: IpAddr = "192.0.2.1".parse().unwrap();
        let ipv6: IpAddr = "2001:db8::1".parse().unwrap();

        assert!(RecordType::A.accepts(ipv4));
        assert!(!RecordType::A.accepts(ipv6));
        assert!(RecordType::Aaaa.accepts(ipv6));
        assert!(!RecordType::Aaaa.accepts(ipv4));
    }
}
