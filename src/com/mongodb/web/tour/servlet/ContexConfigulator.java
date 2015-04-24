package com.mongodb.web.tour.servlet;

import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.bryanreinero.firehose.circuitbreaker.CircuitBreaker;
import com.bryanreinero.firehose.metrics.Interval;
import com.bryanreinero.firehose.metrics.SampleSet;
import com.bryanreinero.firehose.metrics.Statistics;
import com.bryanreinero.hum.server.DAO;
import com.bryanreinero.hum.server.DAOService;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;


public class ContexConfigulator implements ServletContextListener, DAOService {
	
    Logger logger = LogManager.getLogger( ContexConfigulator.class.getName() );
    private Map<String, CircuitBreaker> breakers = new HashMap<String, CircuitBreaker>();
    private Map<String, DAO> daos = new HashMap<String, DAO>();
    private SampleSet set;
   
    @Override
    public void contextInitialized(ServletContextEvent event) {
    	final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ServletContext servletContext = event.getServletContext();
        String l4jConfigPath = servletContext.getInitParameter("log4jConfigPath");
        String prefix = "/Users/breinero/Documents/workspace/Tour/WebContent/";
        PropertyConfigurator.configure( prefix + l4jConfigPath );
        
        set = new SampleSet();
        try {
			mbs.registerMBean(new Statistics( set ), new ObjectName("com.bryanreinero:type=Metrics"));
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException | MalformedObjectNameException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
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
        	daos.put("Poi", new POIAccessObject( coll, set, null ) );
        }
  
		servletContext.setAttribute("daoService", this);
    }

	@Override
    public void contextDestroyed(ServletContextEvent event) {
        // NOOP.
    }
 

	@Override
	public Object execute(String key, Map<String, Object> request) throws Exception {
		
		if( breakers.get(key).isTripped() ) 
			throw new Exception("Breaker tripped");
		
		if ( daos.containsKey(key) ) {
			Interval i = set.set("configload");
			Object o = daos.get(key).execute(request);
			i.mark();
			return o;
		}
		return null;
	}
}