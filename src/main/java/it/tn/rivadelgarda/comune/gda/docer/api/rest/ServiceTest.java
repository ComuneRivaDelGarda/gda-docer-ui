package it.tn.rivadelgarda.comune.gda.docer.api.rest;

import java.util.Date;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/test")
public class ServiceTest {

	protected static final Logger logger = LoggerFactory.getLogger(ServiceTest.class);

	@Context
	protected ServletContext restServletContext;

	@Context
	protected UriInfo uriInfo;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt() {
		// logger.debug("{}", restServletContext.getContextPath());
		logger.debug("{}", uriInfo.getAbsolutePath());
		return new Date().toString();
	}
}
