package main

import (
	"testing"
)

func TestReadConfig(t *testing.T) {
	config, err := ReadConfig()

	if err != nil {
		t.Error(err)
		return
	}

	if config == nil {
		t.Error("config is nil")
	}
}
