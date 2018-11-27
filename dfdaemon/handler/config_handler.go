package handler

import (
	"net/http"
	"github.com/sirupsen/logrus"
	"encoding/json"
	"os"
)

// DebugInfo update config file: /etc/dragonfly.conf
func UpdateConfig(w http.ResponseWriter, req *http.Request) {
	logrus.Debugf("access:%s", req.URL.String())

	// write config to file: /etc/dragonfly.conf
	var config Config
	json.NewDecoder(req.Body).Decode(&config)

	// write to config file
	f, err := os.Create("/etc/dragonfly.conf")

	if err != nil {
		return
	}

	defer f.Close()

	f.WriteString("[node]")
	f.WriteString("addresses: " + config.Addresses)

	f.Sync()

	json.NewEncoder(w).Encode("{'status': 'success'}")

	logrus.Info("update config finished.")
}

type Config struct {
	Addresses string
}