package it.tn.rivadelgarda.comune.gda.docer.api.rest.data;

import java.util.List;

import com.google.gson.Gson;

/**
 * { 
 *   "profilo1":
 *   {"offsetX":10,"offsetY":10,"rotation":0,"rows":["riga1","riga2","riga3"]},
 *   "profilo1":
 *   {"offsetX":15,"offsetY":15,"rotation":45,"rows":["rigaA","rigaB","rigaC"]}
 * }
 * @author mirco
 *
 */
public class StampData {
	
	// data ora corrente
//	Date dataCorrente;
//	
//	Date dataProtocollo;
//	
//	String numeroProtocollo;
	
	String description;
	
	public float offsetx;
	public float offsety;
	
//	int nRighe;
	
	public float rotation;
	
	public List<String> rows;
	
//	boolean riservato;
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
