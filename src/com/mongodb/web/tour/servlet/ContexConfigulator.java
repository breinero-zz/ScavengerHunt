package com.mongodb.web.tour.servlet;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.bryanreinero.hum.server.DAO;
import com.bryanreinero.hum.server.DAOService;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;


public class ContexConfigulator implements ServletContextListener, DAOService {
	
    Logger logger = LogManager.getLogger( ContexConfigulator.class.getName() );
    
    private Map<String, DAO> daos = new HashMap<String, DAO>();
   
    @Override
    public void contextInitialized(ServletContextEvent event) {
    	
        ServletContext servletContext = event.getServletContext();
        String l4jConfigPath = servletContext.getInitParameter("log4jConfigPath");
        String prefix = "/Users/breinero/Documents/workspace/Tour/WebContent/";
        PropertyConfigurator.configure( prefix + l4jConfigPath );
        
        daos.put("configs", 
        		new configDAO( servletContext.getInitParameter("server.config.rootDir") ) );
        
        MongoClient client = null;
        try {
			client = new MongoClient();
		} catch (UnknownHostException e) {
			logger.fatal(e.getMessage());
		}
        
        if( client != null ) {
        	DBCollection  coll = client.getDB("walkingtour").getCollection("hotspots");
        	daos.put("Poi", new POIAccessObject( coll ) );
        }
  
		servletContext.setAttribute("daoService", this);
    }

	@Override
    public void contextDestroyed(ServletContextEvent event) {
        // NOOP.
    }
 

	@Override
	public Object execute(String key, Map<String, Object> request) {
		if ( daos.containsKey(key) )
			return daos.get(key).execute(request);
		return null;
	}
}