package it.tn.rivadelgarda.comune.gda.docer.api.rest.data.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="RG")
public class RegistroGiornaliero {

	@XmlElement(name="documenti")
	private Collection<Map<String, String>> collection;

	public RegistroGiornaliero() {
		// mapProperty = new HashMap<String, MetadatoDocer>();
		collection = new ArrayList<Map<String, String>>();
	}

	@XmlJavaTypeAdapter(MapAdapter.class)
	public Collection<Map<String, String>> getCollection() {
		return collection;
	}

	public void setCollection(Collection<Map<String, String>> collection) {
		this.collection = collection;
	}

}