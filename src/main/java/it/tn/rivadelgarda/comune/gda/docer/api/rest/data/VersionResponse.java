package it.tn.rivadelgarda.comune.gda.docer.api.rest.data;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class VersionResponse {
	private String versione;
	private String note;

	public String getVersione() {
		return versione;
	}

	public void setVersione(String versione) {
		this.versione = versione;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
