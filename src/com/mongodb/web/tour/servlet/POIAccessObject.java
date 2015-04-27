package com.mongodb.web.tour.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bryanreinero.firehose.dao.MongoDAO;
import com.bryanreinero.firehose.metrics.Interval;
import com.bryanreinero.firehose.metrics.SampleSet;
import com.bryanreinero.hum.server.DataAccessObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class POIAccessObject extends MongoDAO implements DataAccessObject {

	public POIAccessObject(MongoClient client, String namespace) {
		super(client, namespace);
	}

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
	
	@Override
	public Object execute(Map<String, Object> request) {
		DBObject near = new BasicDBObject(request);
		DBObject query = new BasicDBObject( "Location.geometry", near );
		
		Result r = new Result();		
		DBCursor cursor = collection.find( query );

		for ( Object o: cursor )
			r.addObject(o);
			
		return r;
	}

}
