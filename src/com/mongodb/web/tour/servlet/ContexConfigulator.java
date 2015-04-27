package com.mongodb.web.tour.servlet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

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
        String l4jConfigPath = servletContext.getInitParameter("log4jConfigPath");
        String prefix = "/Users/breinero/Documents/workspace/Tour/WebContent/";
        PropertyConfigurator.configure( prefix + l4jConfigPath );
        
        set = new SampleSet();
        try {
			mbs.registerMBean(new Statistics( set ), new ObjectName("com.bryanreinero:type=Metrics"));

			FileReader fr = new FileReader	( prefix + "DataAccessConfig.json" );
			BufferedReader br =  new BufferedReader( fr );

			StringBuffer sb = new StringBuffer();
			String line = null;
			while ( (line = br.readLine()) != null ) {
				sb.append(line);
			}
			br.close();
			
			datahub = DAOServiceFactory.getDataAccessHub( sb.toString() , set);
			
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException | MalformedObjectNameException e1) {
			logger.warn(e1.getLocalizedMessage());
		} catch (DAOException e) {
			logger.fatal("Error initializing DataAccessHub. "+e.getMessage());
		} catch (FileNotFoundException e) {
			logger.fatal("Error initializing DataAccessHub. "+e.getMessage());
		} catch (IOException e) {
			logger.fatal("Error initializing DataAccessHub. "+e.getMessage());
		}
        
        DataAccessObject configDao = new configDAO( servletContext.getInitParameter("server.config.rootDir") );
        datahub.setDataAccessObject("configs", configDao );
        
		servletContext.setAttribute("daoService", datahub);
    }

	@Override
    public void contextDestroyed(ServletContextEvent event) {
        // NOOP.
    }
}