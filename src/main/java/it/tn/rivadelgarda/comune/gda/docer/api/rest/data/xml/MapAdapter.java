package it.tn.rivadelgarda.comune.gda.docer.api.rest.data.xml;

import java.util.Collection;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MapAdapter extends XmlAdapter<ListElements[], Collection<Map<String, String>>> {
//public class MapAdapter {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public ListElements[] marshal(Collection<Map<String, String>> arg0) throws Exception {
		ListElements[] listElements = new ListElements[arg0.size()];

		int k = 0;
		int i = 0;
		for (Map<String, String> item : arg0) {
			logger.info("row {}", k);
			MapElements[] mapElements = new MapElements[item.size()];
			for (Map.Entry<String, String> entry : item.entrySet()) {
				final String key = entry.getKey();
				final String value = entry.getValue();
				logger.info("item {} convert {}-{}", i, key, value);
				mapElements[i++] = new MapElements(key, value);
			}
			i = 0;
			listElements[k++] = new ListElements(mapElements);
		}
		return listElements;
	}

	public Collection<Map<String, String>> unmarshal(ListElements[] arg0) throws Exception {
		//// Map<String, MetadatoDocer> r = new HashMap<String, String>();
		// Collection<Map<String, String>> r = new ArrayList<Map<String, String>>();
		// // for (Map<String, String> listitem : r)
		// for (MapElements mapelement : arg0)
		// r.put(mapelement.key, mapelement.value);
		// return r;
		return null;
	}
}