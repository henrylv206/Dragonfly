package handler

import (
	"net/http"
	"encoding/json"
	"os"
	"fmt"
)

// DebugInfo update config file: /etc/dragonfly.conf
func UpdateConfig(w http.ResponseWriter, req *http.Request) {
	fmt.Printf("access:%s", req.URL.String())

	// write config to file: /etc/dragonfly.conf
	var config Config
	json.NewDecoder(req.Body).Decode(&config)

	// write to config file
	fmt.Printf("config addresses: %s", config.Addresses)

	f, err := os.Create("/etc/dragonfly.conf")

	if err != nil {
		return
	}

	defer f.Close()

	f.WriteString("[node]\n")
	f.WriteString("addresses: " + config.Addresses)

	f.Sync()

	json.NewEncoder(w).Encode("{'status': 'success'}")

	fmt.Printf("update config finished.")
}

type Config struct {
	Addresses string
}