package it.tn.rivadelgarda.comune.gda.docer.api.rest.data.xml;

import javax.xml.bind.annotation.XmlElement;

public class ListElements {

	@XmlElement
	public MapElements[] elements;

	private ListElements() {
	} // Required by JAXB

	public ListElements(MapElements[] elements) {
		this.elements = elements;
	}
}
