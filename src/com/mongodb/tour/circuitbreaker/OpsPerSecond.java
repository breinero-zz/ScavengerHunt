package com.mongodb.tour.circuitbreaker;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class OpsPerSecond implements Threshold {

	private final Integer max;
	
	public OpsPerSecond(Integer max ) {
		this.max = max;
	}
	
	@Override
	public boolean isExceeded(DescriptiveStatistics stats) {
		return (stats.getN() >= max );
	}

}
