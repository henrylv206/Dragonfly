package com.alibaba.dragonfly.supernode.service.impl;

import java.io.IOException;
import java.io.StringWriter;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dragonfly.supernode.common.Constants;
import com.alibaba.dragonfly.supernode.common.MetricConsts;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import io.prometheus.client.hotspot.DefaultExports;

/**
 * 
 * @author henrylv
 *
 */
@Service("metricsService")
public class MetricsService {

	private static final Logger logger = LoggerFactory.getLogger(MetricsService.class);
	 
	private static final CollectorRegistry registry = CollectorRegistry.defaultRegistry;
	
	private StringWriter writer;
	
	@PostConstruct
	public void init() {
		DefaultExports.initialize();
		

        // metrics
        MetricConsts.totalThreads.set(Constants.DOWNLOAD_MAX_POOL_SIZE);
        
        MetricConsts.currentStatus.set(1);
        
        
	}
	
	public String collect() {
		
		writer = new StringWriter();
		
	    try {
			TextFormat.write004(writer, registry.metricFamilySamples());
		} catch (IOException e) {
			logger.error("get metrics failed.", e);
		}

	    return writer.toString();
	  }
}
