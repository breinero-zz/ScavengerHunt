package com.mongodb.tour.circuitbreaker;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public interface Threshold {
	public boolean isExceeded(DescriptiveStatistics stats);
	public BreakerType getType();
	public double getValue();
}
