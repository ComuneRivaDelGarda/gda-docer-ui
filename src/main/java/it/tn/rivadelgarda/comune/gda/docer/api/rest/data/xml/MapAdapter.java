package it.tn.rivadelgarda.comune.gda.docer.api.rest.data.xml;

import java.util.Collection;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MapAdapter extends XmlAdapter<Document[], Collection<Map<String, String>>> {
//public class MapAdapter {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public Document[] marshal(Collection<Map<String, String>> arg0) throws Exception {
		Document[] listElements = new Document[arg0.size()];

		int k = 0;
		int i = 0;
		for (Map<String, String> item : arg0) {
			logger.info("row {}", k);
			Metadato[] mapElements = new Metadato[item.size()];
			for (Map.Entry<String, String> entry : item.entrySet()) {
				final String key = entry.getKey();
				final String value = entry.getValue();
				logger.info("item {} convert {}-{}", i, key, value);
				mapElements[i++] = new Metadato(key, value);
			}
			i = 0;
			listElements[k++] = new Document(mapElements);
		}
		return listElements;
	}

	public Collection<Map<String, String>> unmarshal(Document[] arg0) throws Exception {
		//// Map<String, MetadatoDocer> r = new HashMap<String, String>();
		// Collection<Map<String, String>> r = new ArrayList<Map<String, String>>();
		// // for (Map<String, String> listitem : r)
		// for (MapElements mapelement : arg0)
		// r.put(mapelement.key, mapelement.value);
		// return r;
		return null;
	}
}