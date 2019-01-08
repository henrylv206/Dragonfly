package prometheus

import (
	"github.com/prometheus/client_golang/prometheus"
	"time"
)

const (
	MaxAge time.Duration = 10 * time.Minute

)

// metrics
var (
	// TODO test, delete this
	CpuTemp = prometheus.NewGauge(prometheus.GaugeOpts{
		Name: "cpu_temperature_celsius",
		Help: "Current temperature of the CPU.",
	})

	// current status
	CurrentStatus = prometheus.NewGauge(prometheus.GaugeOpts{
		Name: "current_status",
		Help: "Current status of the dfdaemon.",
	})

	// version
	DfdaemonVersion = prometheus.NewGauge(prometheus.GaugeOpts{
		Name: "dfdaemon_version_info",
		Help: "Current version info of the dfdaemon.",
	})

	// API request count
	ApiReqeusts = prometheus.NewGauge(prometheus.GaugeOpts{
		Name: "api_request_count",
		Help: "api request count.",
	})

	// total dfget number
	TotalDfgets = prometheus.NewGauge(prometheus.GaugeOpts{
		Name: "dfget_num_total",
		Help: "total dfget number",
	})

	// current dfget number
	CurrentDfgets = prometheus.NewGauge(prometheus.GaugeOpts{
		Name: "dfget_num_current",
		Help: "current dfget number.",
	})

	// dfget_download_success_count
	DownloadDfgetSuccessCount = prometheus.NewGauge(prometheus.GaugeOpts{
		Name: "dfget_download_success_count",
		Help: "dfget download success count.",
	})

	// dfget_download_failed_count
	DownloadDfgetFailedCount = prometheus.NewGauge(prometheus.GaugeOpts{
		Name: "dfget_download_failed_count",
		Help: "dfget download failed count.",
	})

	// disk_space_total
	TotalDisk = prometheus.NewGauge(prometheus.GaugeOpts{
		Name: "disk_space_total",
		Help: "total disk space.",
	})

	// disk_space_current
	CurrentDisk = prometheus.NewGauge(prometheus.GaugeOpts{
		Name: "disk_space_current",
		Help: "Current disk space.",
	})

)



func init() {
	// TODO only for test, delete this
	CpuTemp.Set(30)

	// register metrics
	prometheus.MustRegister(CpuTemp, CurrentStatus, DfdaemonVersion, ApiReqeusts, TotalDfgets, CurrentDfgets, DownloadDfgetSuccessCount, DownloadDfgetFailedCount, TotalDisk, CurrentDisk)


	// TODO test
	buildInfo := prometheus.NewGaugeVec(
		prometheus.GaugeOpts{
			Name: "dfdaemon_build_info",
			Help: "A metric with a constant '1' value labeled by major, minor.",
		},
		[]string{"major", "minor"},
	)

	buildInfo.WithLabelValues("2018-12", "v0.2.0").Set(1)

	prometheus.MustRegister(buildInfo)

}
