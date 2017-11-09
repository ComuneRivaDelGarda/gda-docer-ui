package it.tn.rivadelgarda.comune.gda.docer.api.rest.data.xml;

import javax.xml.bind.annotation.XmlElement;

public class Document {

	@XmlElement
	public Metadato[] metadati;

	private Document() {
	} // Required by JAXB

	public Document(Metadato[] metadati) {
		this.metadati = metadati;
	}
}
