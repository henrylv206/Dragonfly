package com.alibaba.dragonfly.supernode.common;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;

public final class MetricConsts {

	// task
    public static final Gauge currentTasks = Gauge.build().name("task_num_current").help("current task number.").register();
    public static final Counter totalTasks = Counter.build().name("task_num_total").help("total task number.").register();

    // disk
    public static final Gauge currentDisks = Gauge.build().name("disk_space_current").help("current disk space.").register();
    public static final Gauge totalDisks = Gauge.build().name("disk_space_total").help("total disk space.").register();


    // download thread metrics
    public static final Gauge totalThreads = Gauge.build().name("download_thread_num_total").help("total download thread number.").register();
    public static final Gauge currentThreads = Gauge.build().name("download_thread_num_current").help("current download thread number.").register();
    
    // download failed count
    public static final Counter failCount = Counter.build().name("download_fail_count").help("total task number.").register();
    
    // peer
    public static final Gauge totalPeers = Gauge.build().name("peer_num_total").help("total peer number.").register();
    
    // download rate
    public static final Gauge downloadRates = Gauge.build().name("download_rate_current").help("current download rates.").register();
    		
    		
    // API request count
    public static final Counter apiReqeusts = Counter.build().name("api_request_count").help("api request count.").register();
    
    // current status
    public static final Gauge currentStatus = Gauge.build().name("current_status").help("supernode current status.").register();;
}
