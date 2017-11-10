package it.tn.rivadelgarda.comune.gda.docer.api.rest.data;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UploadAllegatoResponse {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
