package com.mongodb.web.tour.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.bryanreinero.hum.element.DecisionTree;
import com.bryanreinero.hum.server.ConfigurationDAO;
import com.bryanreinero.hum.server.DAOService;
import com.bryanreinero.hum.server.Executor;
import com.bryanreinero.hum.server.Responder;
import com.mongodb.BasicDBObject;

/**
 * Servlet implementation class Poi
 */
@WebServlet("/Poi")
public class Poi extends HttpServlet {


	private static final long serialVersionUID = -6170830231570604200L;
	public static ConfigurationDAO configurations;
	private DAOService doaServices;

	public static Logger logger = LogManager.getLogger( Poi.class.getName() ); 
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public void init(ServletConfig config) {
		
		logger.info("loading DoaService");
		doaServices = (DAOService)config.getServletContext().getAttribute("daoService");
		logger.info("DoaService loaded");
	}

	public void service(HttpServletRequest req, HttpServletResponse resp) {
		Executor executor = null;
		try {
			executor = new Executor(req, null);
			DecisionTree tree = (DecisionTree)doaServices.execute("configs", new BasicDBObject("name", "root") );
			tree.accept(executor);
		}
		catch( Exception e ) {
			logger.warn( e );
			
			// TODO: set executor to static 404 error
		}
		
		try {
			Responder.respond(resp, executor.getResponse());
		} catch (IOException ioe) {
			logger.error(ioe);
		}
	}

}
