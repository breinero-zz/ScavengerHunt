package com.mongodb.web.tour.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bryanreinero.hum.server.DAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class POIAccessObject implements DAO {

	private final DBCollection collection;
	
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
	
	public POIAccessObject( DBCollection collection ) {
		this.collection = collection;
	}
	
	@Override
	public Object execute(Map<String, Object> request) {
		DBObject near = new BasicDBObject(request);
		DBObject query = new BasicDBObject( "Location.geometry", near );
		
		Result r = new Result();
		for ( Object o: collection.find( query ) )
			r.addObject(o);
			
		return r;
	}

}
