package com.mongodb.web.tour.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.bryanreinero.firehose.dao.DAOException;
import com.bryanreinero.firehose.dao.MongoDAO;
import com.bryanreinero.firehose.dao.DataAccessObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public class POIAccessObject extends MongoDAO implements DataAccessObject {
	
	public static Logger logger = LogManager.getLogger( POIAccessObject.class.getName() );

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
	public Object execute(Map<String, Object> request) throws DAOException {
		DBObject near = new BasicDBObject(request);
		DBObject query = new BasicDBObject( "Location.geometry", near );
		
		Result r = new Result();		
		try {
			DBCursor cursor = collection.find( query );
			for ( Object o: cursor )
				r.addObject(o);
		} catch ( MongoException e ) {
			logger.warn( "couldn't execute read to mongo. "+e.getMessage()); 
			throw new DAOException( "Trouble executing request.", e );
		}
		return r;
	}
}
