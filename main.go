package main

import (
	"flag"
	"log"
	"log/slog"
	"os"
)

var logFile string
var logJson bool
var ConfigPath string
var Logger = slog.Default()

func main() {
	flag.Parse()
	configureLogger()

	config, err := ReadConfig()
	if err != nil {
		Logger.Error("failed to read config", "error", err)
		os.Exit(1)
	}

	ip, err := FetchCurrentIp()
	if err != nil {
		Logger.Error("failed to fetch current ip", "error", err)
		os.Exit(1)
	}

	hasErrors := false

	for _, account := range config.Accounts {
		updater := NewDnsUpdater(&account)
		err = updater.UpdateRecords(ip)

		if err != nil {
			Logger.Error(
				"failed to update account records",
				"account_id", account.Id,
				"error", err,
			)

			hasErrors = true
		}
	}

	if hasErrors {
		os.Exit(1)
	}
}

func configureLogger() {
	writer := os.Stdout
	if logFile != "" {
		file, err := os.OpenFile(logFile, os.O_APPEND|os.O_WRONLY|os.O_CREATE, 0600)
		if err != nil {
			log.Panicln("Unable to open log file", err)
		}

		writer = file
	}

	var handler slog.Handler
	handler = slog.NewTextHandler(writer, nil)

	if logJson {
		handler = slog.NewJSONHandler(writer, nil)
	}

	Logger = slog.New(handler)
}

func init() {
	flag.StringVar(&logFile, "logfile", "", "Logs file path, if no specified uses stdout")
	flag.BoolVar(&logJson, "logjson", false, "Show logs in json format")
	flag.StringVar(&ConfigPath, "config", "config.json", "The config file path, if not specified try to find config.json on the working directory")
}
