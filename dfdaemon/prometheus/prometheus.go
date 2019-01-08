package prometheus

import (
	"github.com/prometheus/client_golang/prometheus"
	"time"
)

const (
	MaxAge time.Duration = 10 * time.Minute

)

var (
	DefaultRegister prometheus.Registerer = prometheus.DefaultRegisterer

)



var cpuTemp = prometheus.NewGauge(prometheus.GaugeOpts{
	Name: "cpu_temperature_celsius",
	Help: "Current temperature of the CPU.",
})

func init() {

	cpuTemp.Set(30)

	prometheus.MustRegister(cpuTemp)

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
