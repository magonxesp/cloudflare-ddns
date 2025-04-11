package main

import (
	"context"
	"fmt"
	"github.com/cloudflare/cloudflare-go/v4"
	"github.com/cloudflare/cloudflare-go/v4/dns"
	"github.com/cloudflare/cloudflare-go/v4/option"
)

type DnsUpdater struct {
	account *Account
	client  *cloudflare.Client
}

func NewDnsUpdater(account *Account) *DnsUpdater {
	return &DnsUpdater{
		account: account,
		client:  cloudflareClient(account),
	}
}

func cloudflareClient(account *Account) *cloudflare.Client {
	return cloudflare.NewClient(
		option.WithAPIToken(account.ApiToken),
		option.WithAPIEmail(account.Email),
	)
}

func (updater *DnsUpdater) UpdateRecords(ip string) error {
	for _, zone := range updater.account.Zones {
		err := updater.UpdateZoneRecords(&zone, ip)
		if err != nil {
			return err
		}
	}

	return nil
}

func filterRecordsToUpdate(records []dns.RecordResponse, names []string) []dns.RecordResponse {
	var toUpdate []dns.RecordResponse

	for _, record := range records {
		for _, name := range names {
			if record.Name == name {
				toUpdate = append(toUpdate, record)
			}
		}
	}

	return toUpdate
}

func (updater *DnsUpdater) updateRecord(zone *Zone, record dns.RecordResponse, ip string) error {
	response, err := updater.client.DNS.Records.Edit(
		context.TODO(),
		record.ID,
		dns.RecordEditParams{
			ZoneID: cloudflare.F(zone.Id),
			Record: dns.ARecordParam{
				Content: cloudflare.F(ip),
			},
		},
	)

	if err != nil {
		return err
	}

	if response.Content != ip {
		return fmt.Errorf("record updated with ip %s but still have ip %s", response.Content, ip)
	}

	Logger.Info(
		"dns record ip updated",
		"zone", zone.Id,
		"record", record.Name,
		"ip", ip,
	)

	return nil
}

func (updater *DnsUpdater) UpdateZoneRecords(zone *Zone, ip string) error {
	page, err := updater.client.DNS.Records.List(context.TODO(), dns.RecordListParams{
		ZoneID: cloudflare.F(zone.Id),
	})

	if err != nil {
		return err
	}

	records := filterRecordsToUpdate(page.Result, zone.Names)

	for _, record := range records {
		err = updater.updateRecord(zone, record, ip)
		if err != nil {
			return err
		}
	}

	return nil
}
