package com.mongodb.web.tour.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bryanreinero.hum.element.Specification;
import com.bryanreinero.firehose.dao.DAOService;
import com.bryanreinero.hum.server.Executor;
import com.bryanreinero.hum.server.HumException;
import com.bryanreinero.hum.server.Responder;
import com.bryanreinero.hum.server.Response;
import com.mongodb.BasicDBObject;

/**
 * Servlet implementation class Poi
 */
@WebServlet("/Tour/*")
public class Poi extends HttpServlet {


	private static final long serialVersionUID = -6170830231570604200L;
	private DAOService doaServices;

	public static Logger logger = LogManager.getLogger( Poi.class.getName() ); 
	//public Specification tree = null;
	
	private static Response stdErrResponse = new Response();
	
	static {
		stdErrResponse.setResponseHeader("Status-Code", "503");
		stdErrResponse.setResponseBody("Service unvailable");
	}
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public void init(ServletConfig config) {
		
		logger.info("loading DoaService");
		doaServices = (DAOService) config.getServletContext().getAttribute("daoService");
		logger.info("DoaService loaded");
		
	}

	public void service(HttpServletRequest req, HttpServletResponse resp) {

		try {
			Executor executor = new Executor(req, doaServices);

			try {
				Specification tree = (Specification) doaServices.execute(
						"configs", new BasicDBObject("name", "root"));
				tree.accept( executor );
				Responder.respond(resp, executor.getResponse());
			}catch( Exception e ) {
				logger.error("Servlet Poi failed execution. "+e.getCause() );
				Responder.respond(resp, stdErrResponse );
			}
		}
		catch (IOException ioe) {
			logger.error("Servlet Poi failed to repsond to request "+ioe.getMessage() );
		} 
		catch (HumException e1) {
			logger.error("Servlet Poi failed to repsond to request "+e1.getMessage() );
		}	
	}
}
