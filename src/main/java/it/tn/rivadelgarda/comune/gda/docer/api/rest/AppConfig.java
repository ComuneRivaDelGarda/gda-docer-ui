package it.tn.rivadelgarda.comune.gda.docer.api.rest;


import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class AppConfig extends ResourceConfig {
	
	public AppConfig() {
		packages("it.tn.rivadelgarda.comune.gda.docer.api.rest");
		register(MultiPartFeature.class);
	}
	
}
