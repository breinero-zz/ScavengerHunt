package com.mongodb.web.tour.servlet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.bryanreinero.hum.element.Specification;
import com.bryanreinero.hum.parser.XMLParser;
import com.bryanreinero.hum.parser.XMLParserFactory;
import com.bryanreinero.hum.persistence.ConfigDAO;
import com.bryanreinero.hum.server.DAO;

public class configDAO implements DAO {
	
	public static Logger logger = LogManager.getLogger( Poi.class.getName() ); 
	
	private Map<String, Specification> configs = new ConcurrentHashMap<String, Specification>();
	private XMLParser parser;
	private String path;
	
	public configDAO ( String path )  {
		this.path = path;
		parser = XMLParserFactory.getParser();
	}
	
	private Specification getConfig( String key ) {
		Specification config = null;
		try {
			config = parser.parse(new FileInputStream( path+key+".xml") );
		} catch (FileNotFoundException e) {
			logger.warn( e.getMessage() );
		} catch (IOException e) {
			logger.warn( e.getMessage() );
		} catch (SAXException e) {
			logger.warn( e.getMessage() );
		}
		finally {
			if ( config == null )
				config = ConfigDAO.defaultTree;
		}
		
		configs.put(key, config);
		
		return config;
	}
	
	@Override
	public Object execute(Map<String, Object> request) {
		String name =  (String)request.get("name");
//		if ( ! configs.containsKey( name ) )
//			return getConfig( name );
//		else
//			return configs.get(name);
		return getConfig( name );
	}

}
