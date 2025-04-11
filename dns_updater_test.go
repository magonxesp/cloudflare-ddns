package main

import "testing"

func createUpdaterFromConfig(t *testing.T) *DnsUpdater {
	config, _ := ReadConfig()
	if config == nil {
		t.Error("test need config.json")
		return nil
	}

	if len(config.Accounts) == 0 {
		t.Error("test need at least one account configured")
		return nil
	}

	account := config.Accounts[0]
	return NewDnsUpdater(&account)
}

func TestDnsUpdater_UpdateRecords(t *testing.T) {
	ip, _ := FetchCurrentIp()
	updater := createUpdaterFromConfig(t)
	if updater == nil {
		return
	}

	err := updater.UpdateRecords(ip)

	if err != nil {
		t.Error(err)
	}
}
