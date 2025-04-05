package main

import (
	"encoding/json"
	"os"
)

type Zone struct {
	Id    string   `json:"zone_id"`
	Names []string `json:"names"`
}

type Account struct {
	Id       string `json:"account_id"`
	ApiToken string `json:"api_token"`
	Zones    []Zone `json:"zones"`
}

type Config struct {
	Accounts []Account `json:"accounts"`
}

func ReadConfig() (*Config, error) {
	var config Config

	content, err := os.ReadFile(getConfigPath())
	if err != nil {
		return nil, err
	}

	err = json.Unmarshal(content, &config)
	if err != nil {
		return nil, err
	}

	return &config, nil
}

func getConfigPath() string {
	if ConfigPath != "" {
		return ConfigPath
	}

	return "config.json"
}
