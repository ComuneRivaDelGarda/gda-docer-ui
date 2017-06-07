package it.tn.rivadelgarda.comune.gda.docer.api.rest.data;

import java.util.List;

import com.google.gson.Gson;

public class StampData {
	
	// data ora corrente
//	Date dataCorrente;
//	
//	Date dataProtocollo;
//	
//	String numeroProtocollo;
	
	float offsetx;
	float offsety;
	
//	int nRighe;
	
	float rotation;
	
	List<String> rows;
	
//	boolean riservato;
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
