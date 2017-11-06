package it.tn.rivadelgarda.comune.gda.docer.api.rest;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.tn.rivadelgarda.comune.gda.docer.DocerHelper;

public abstract class ServiceBase {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Context
	protected ServletContext restServletContext;

	@Context
	protected UriInfo uriInfo;
	
	protected DocerHelper docer = null;
	protected String token = null;

	protected DocerHelper getDocerHelper(String utente) throws Exception {
		if (docer == null) {
			Properties p = new Properties();
			// String path =
			// restServletContext.getRealPath("WEB-INF/config.properties");
			// p.load(getClass().getResourceAsStream("config.properties"));
			// p.load(fileProperties);
			InputStream fileProperties = restServletContext.getResourceAsStream("/WEB-INF/config.properties");
			p.load(fileProperties);
			fileProperties.close();
			logger.debug("caricato configurazione docer da /WEB-INF/config.properties");

			String docerUsername = p.getProperty("username");
			String docerPassword = p.getProperty("password");
			final String docerUserPassword = p.getProperty("userpassword");
			if (StringUtils.isNotBlank(utente)) {
				docerUsername = utente;
				docerPassword = docerUserPassword;
			}
			final String docerSerivcesUrl = p.getProperty("url");

			docer = new DocerHelper(docerSerivcesUrl, docerUsername, docerPassword);

			token = docer.login();
			logger.debug("connesso a docer con tocken {}", token);
		}
		return docer;
	}
}
