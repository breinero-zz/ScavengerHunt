package com.mongodb.tour.circuitbreaker;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Latency implements Threshold {

	private final Integer max;
	
	public Latency ( Integer max ) {
		this.max = max;
	}
	
	@Override
	public boolean isExceeded(DescriptiveStatistics stats) {
		return ( stats.getMean() >= max );
	}
}
