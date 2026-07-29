use clap::Parser;
use cloudflare_ddns::cloudflare::{CloudflareClient, UpdateOutcome};
use cloudflare_ddns::config::Config;
use cloudflare_ddns::{logging, public_ip};
use log::{error, info};
use std::collections::HashMap;
use std::net::IpAddr;
use std::path::PathBuf;
use std::process::ExitCode;

#[derive(Debug, Parser)]
#[command(about = "Update Cloudflare DNS records with the current public IP")]
struct Cli {
    /// Use a specific configuration file
    #[arg(short, long, value_name = "FILE")]
    config: Option<PathBuf>,

    /// Show the changes without updating Cloudflare
    #[arg(long)]
    dry_run: bool,
}

fn main() -> ExitCode {
    match run() {
        Ok(()) => ExitCode::SUCCESS,
        Err(error) => {
            eprintln!("error: {error}");
            ExitCode::FAILURE
        }
    }
}

fn run() -> Result<(), String> {
    let cli = Cli::parse();
    let (config, path) = Config::load(cli.config.as_deref())?;

    logging::configure(&config.logging)?;
    info!("using configuration from {}", path.display());

    let client = CloudflareClient::new(&config.cloudflare)?;
    let mut addresses = HashMap::<_, IpAddr>::new();
    let mut had_errors = false;

    for zone in &config.zones {
        for record in &zone.records {
            let address = match addresses.get(&record.record_type) {
                Some(address) => *address,
                None => {
                    let address = public_ip::fetch(record.record_type).map_err(|error| {
                        format!(
                            "failed to resolve public {} address: {error}",
                            record.record_type
                        )
                    })?;
                    info!(
                        "resolved public {} address: {}",
                        record.record_type, address
                    );
                    addresses.insert(record.record_type, address);
                    address
                }
            };

            match client.update_record(zone, record, address, cli.dry_run) {
                Ok(UpdateOutcome::Unchanged) => {
                    info!(
                        "{} {} is already {}",
                        record.record_type, record.name, address
                    );
                }
                Ok(UpdateOutcome::Updated) => {
                    info!(
                        "updated {} {} to {}",
                        record.record_type, record.name, address
                    );
                }
                Ok(UpdateOutcome::WouldUpdate) => {
                    info!(
                        "would update {} {} to {}",
                        record.record_type, record.name, address
                    );
                }
                Err(error) => {
                    error!(
                        "failed to update {} {}: {error}",
                        record.record_type, record.name
                    );
                    had_errors = true;
                }
            }
        }
    }

    if had_errors {
        return Err("one or more DNS records could not be updated".to_owned());
    }

    Ok(())
}
