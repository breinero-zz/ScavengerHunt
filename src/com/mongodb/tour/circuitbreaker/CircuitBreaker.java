package com.mongodb.tour.circuitbreaker;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.bryanreinero.firehose.metrics.SampleSet;

public class CircuitBreaker {
	private final AtomicBoolean isTripped = new AtomicBoolean(false);
	
	private final Map<String, Threshold> tresholds = new HashMap<String, Threshold>();
	private final SampleSet samples;
	
	// How often the CircuitBreaker should check thresholds
	private Integer interval = new Integer(1000);
	
	public CircuitBreaker( SampleSet set ){
		samples = set;
	}
	
	private void check() {
		for( Entry<String, Threshold> e : tresholds.entrySet() ) {
			DescriptiveStatistics stats = samples.report( e.getKey() ); 
			Threshold t = e.getValue();
			if( t.isExceeded( stats ) )
				this.isTripped.set(true);
		}
	}
	
	public boolean reset () {
		return isTripped.compareAndSet(false, true);
	}
	
	public boolean isTripped() {
		return isTripped.get();
	}
}
