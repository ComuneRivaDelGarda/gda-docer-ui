package it.tn.rivadelgarda.comune.gda.docer.api.rest;

import java.util.Date;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Test API")
@Path("/test")
public class ServiceTest {

	protected static final Logger logger = LoggerFactory.getLogger(ServiceTest.class);

	@Context
	protected ServletContext restServletContext;

	@Context
	protected UriInfo uriInfo;

	@ApiOperation(value = "/test", notes = "un web service di test per verificare che la webapp sia attiva", produces = MediaType.TEXT_PLAIN, response = String.class)	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt() {
		// logger.debug("{}", restServletContext.getContextPath());
		logger.debug("{}", uriInfo.getAbsolutePath());
		return new Date().toString();
	}
}
