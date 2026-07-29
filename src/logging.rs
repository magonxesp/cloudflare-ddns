use crate::config::LoggingConfig;
use fern::Dispatch;
use std::fs;
use std::io;
use std::path::{Path, PathBuf};

pub fn configure(config: &LoggingConfig) -> Result<(), String> {
    let dispatch = Dispatch::new()
        .format(|out, message, record| {
            out.finish(format_args!(
                "[{}][{}] {}",
                record.level(),
                record.target(),
                message
            ))
        })
        .level(config.level.into())
        .chain(io::stdout());

    let dispatch = if let Some(path) = &config.file {
        let path = expand_home(path);
        if let Some(parent) = path
            .parent()
            .filter(|parent| !parent.as_os_str().is_empty())
        {
            fs::create_dir_all(parent).map_err(|error| {
                format!(
                    "failed to create log directory {}: {error}",
                    parent.display()
                )
            })?;
        }
        dispatch.chain(
            fern::log_file(&path)
                .map_err(|error| format!("failed to open log file {}: {error}", path.display()))?,
        )
    } else {
        dispatch
    };

    dispatch
        .apply()
        .map_err(|error| format!("failed to configure logging: {error}"))
}

fn expand_home(path: &Path) -> PathBuf {
    let mut components = path.components();
    if components
        .next()
        .is_some_and(|component| component.as_os_str() == "~")
        && let Some(home) = std::env::var_os("HOME")
    {
        return PathBuf::from(home).join(components.as_path());
    }

    path.to_path_buf()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn leaves_regular_paths_unchanged() {
        assert_eq!(
            expand_home(Path::new("/var/log/cloudflare-ddns.log")),
            PathBuf::from("/var/log/cloudflare-ddns.log")
        );
    }
}
