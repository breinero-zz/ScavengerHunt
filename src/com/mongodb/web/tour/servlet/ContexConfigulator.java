package com.mongodb.web.tour.servlet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bryanreinero.firehose.dao.DAOException;
import com.bryanreinero.firehose.dao.DAOServiceFactory;
import com.bryanreinero.firehose.dao.DataAccessHub;
import com.bryanreinero.firehose.metrics.SampleSet;
import com.bryanreinero.firehose.metrics.Statistics;
import com.bryanreinero.firehose.dao.DataAccessObject;

public class ContexConfigulator implements ServletContextListener {
	
    Logger logger = LogManager.getLogger( ContexConfigulator.class.getName() );
    private DataAccessHub datahub;
    private SampleSet set;
   
    @Override
    public void contextInitialized(ServletContextEvent event) {
    	final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
	    
    	ServletContext servletContext = event.getServletContext();
    			
	    String DataAccessConfig = servletContext.getInitParameter("DataAccessConfig");
        
        set = new SampleSet();
        
        // Read the Data Access Configuration
        String daoConfigJSON = null;
		try {
			FileReader fr = new FileReader( servletContext.getRealPath ( DataAccessConfig ) );
			BufferedReader br =  new BufferedReader( fr );
			StringBuffer sb = new StringBuffer();
			String line = null;
			while ( (line = br.readLine()) != null ) {
				sb.append(line);
			}
			br.close();
			daoConfigJSON = sb.toString();
		} catch (IOException e) {
			logger.error("Data Access configuration not loaded. "+e.getMessage() );
		}
		
        try {
        	datahub = DAOServiceFactory.getDataAccessHub( daoConfigJSON, set);
			servletContext.setAttribute("daoService", datahub);
			DataAccessObject configDao = new configDAO( servletContext.getInitParameter("server.config.rootDir") );
			datahub.setDataAccessObject("configs", configDao );
        }
		 catch (DAOException e) {
			logger.error("Error initializing DataAccessHub. "+e.getMessage());
		}

		// Registering Statistics With JMX
		try {
			mbs.registerMBean(new Statistics( set ), new ObjectName("com.bryanreinero:type=Metrics"));
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException | MalformedObjectNameException e) {
			logger.error("Failed to register Statistics to JXM. "+e.getMessage());
		}
			
    }

	@Override
    public void contextDestroyed(ServletContextEvent event) {
        // NOOP.
    }
}