package prometheus

import (
	"github.com/prometheus/client_golang/prometheus"
)

func init() {
	buildInfo := prometheus.NewGaugeVec(
		prometheus.GaugeOpts{
			Name: "kubernetes_build_info",
			Help: "A metric with a constant '1' value labeled by major, minor, git version, git commit, git tree state, build date, Go version, and compiler from which Kubernetes was built, and platform on which it is running.",
		},
		[]string{"major", "minor"},
	)

	buildInfo.WithLabelValues("2018-10-29", "0.0.1").Set(1)

	prometheus.MustRegister(buildInfo)
}
