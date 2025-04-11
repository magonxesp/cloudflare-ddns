package main

import (
	"testing"
)

func TestFetchCurrentIp(t *testing.T) {
	ip, err := FetchCurrentIp()

	t.Logf("returned ip address: %s", ip)

	if err != nil {
		t.Errorf("ip address fetching failed with error: %s", err)
	}

	if ip == "" {
		t.Errorf("returned ip address is empty")
	}
}
