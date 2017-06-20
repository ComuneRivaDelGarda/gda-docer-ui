package it.tn.rivadelgarda.comune.gda.docer.api.rest.data;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SetAclToExternalIdRequest {

	private String externalId;
	private String acl;
	private String utente;

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getAcl() {
		return acl;
	}

	public void setAcl(String acl) {
		this.acl = acl;
	}

	public String getUtente() {
		return utente;
	}

	public void setUtente(String utente) {
		this.utente = utente;
	}

}
