package it.tn.rivadelgarda.comune.gda.docer.api.rest.data.xml;

import javax.xml.bind.annotation.XmlElement;

class Metadato {
	
	@XmlElement
	public String key;
	@XmlElement
	public String value;

	private Metadato() {
	} // Required by JAXB

	public Metadato(String key, String value) {
		this.key = key;
		this.value = value;
	}
}
