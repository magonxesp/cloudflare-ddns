package main

import (
	"flag"
	"io"
	"log"
	"log/slog"
	"net/http"
	"os"
)

var logFile string
var logJson bool
var ConfigPath string
var Logger *slog.Logger

func main() {
	flag.Parse()
	configureLogger()

	// config := ReadConfig()
	// TODO: iterar config y hacer peticiones a cloudflare
}

func FetchCurrentIp() (string, error) {
	resp, err := http.Get("https://api.ipify.org")
	if err != nil {
		return "", err
	}
	defer resp.Body.Close()

	// Leer el cuerpo de la respuesta
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return "", err
	}

	return string(body), nil
}

func configureLogger() {
	writter := os.Stdout
	if logFile != "" {
		file, err := os.OpenFile(logFile, os.O_APPEND|os.O_WRONLY|os.O_CREATE, 0600)
		if err != nil {
			log.Panicln("Unable to open log file", err)
		}

		writter = file
	}

	var handler slog.Handler
	handler = slog.NewTextHandler(writter, nil)

	if logJson {
		handler = slog.NewJSONHandler(writter, nil)
	}

	Logger = slog.New(handler)
}

func init() {
	flag.StringVar(&logFile, "logfile", "", "Logs file path, if no specified uses stdout")
	flag.BoolVar(&logJson, "logjson", false, "Show logs in json format")
	flag.StringVar(&ConfigPath, "config", "config.json", "The config file path, if not specified try to find config.json on the working directory")
}
