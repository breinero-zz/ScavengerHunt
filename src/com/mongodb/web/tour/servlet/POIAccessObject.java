package com.mongodb.web.tour.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bryanreinero.firehose.metrics.Interval;
import com.bryanreinero.firehose.metrics.SampleSet;
import com.bryanreinero.hum.server.DAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class POIAccessObject implements DAO {

	private final DBCollection collection;
	private final SampleSet samples;
	
	public class Result {
		private List<Object> objects = new ArrayList<Object>();
		
		public void addObject( Object o ) {
			objects.add(o);
		}
		
		@Override
		public String toString()  {
			StringBuffer buf = new StringBuffer();
			for( Object o : objects )
				buf.append(o.toString());
			return buf.toString();
		}
	}
	
	public POIAccessObject( DBCollection collection, SampleSet samples ) {
		this.collection = collection;
		this.samples = samples;
	}
	
	@Override
	public Object execute(Map<String, Object> request) {
		DBObject near = new BasicDBObject(request);
		DBObject query = new BasicDBObject( "Location.geometry", near );
		
		Result r = new Result();
		Interval i = samples.set("getPOI");
		DBCursor cursor = collection.find( query );
		i.mark();
		for ( Object o: cursor )
			r.addObject(o);
			
		return r;
	}

}
