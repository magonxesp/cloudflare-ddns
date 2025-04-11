package main

import (
	"io"
	"log/slog"
	"net/http"
)

func FetchCurrentIp() (string, error) {
	resp, err := http.Get("https://api.ipify.org")
	if err != nil {
		return "", err
	}

	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return "", err
	}

	ip := string(body)
	Logger.Debug("Resolved IP address", slog.String("ip", ip))

	return ip, nil
}
