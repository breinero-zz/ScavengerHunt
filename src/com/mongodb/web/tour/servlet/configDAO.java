package com.mongodb.web.tour.servlet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.bryanreinero.hum.element.DecisionTree;
import com.bryanreinero.hum.parser.XMLParser;
import com.bryanreinero.hum.parser.XMLParserFactory;
import com.bryanreinero.hum.persistence.ConfigDAO;
import com.bryanreinero.hum.server.DAO;

public class configDAO implements DAO {
	
	public static Logger logger = LogManager.getLogger( Poi.class.getName() ); 
	
	private Map<String, DecisionTree> configs = new ConcurrentHashMap<String, DecisionTree>();
	private XMLParser parser;
	private String path;
	
	public configDAO ( String path )  {
		this.path = path;
		parser = XMLParserFactory.getParser();
	}
	
	private DecisionTree getConfig( String key ) {
		DecisionTree config = null;
		try {
			config = parser.parse(new FileInputStream( path+key+".xml") );
		} catch (FileNotFoundException e) {
			logger.warn( e.getCause() );
			e.printStackTrace();
		} catch (IOException e) {
			logger.warn( e.getCause() );
			e.printStackTrace();
		} catch (SAXException e) {
			logger.warn( e.getCause() );
			e.printStackTrace();
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
		if ( ! configs.containsKey( name ) )
			return getConfig( name );
		else
			return configs.get(name);
	}

}
