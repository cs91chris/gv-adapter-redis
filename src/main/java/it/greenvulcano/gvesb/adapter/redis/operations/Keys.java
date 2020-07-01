
package it.greenvulcano.gvesb.adapter.redis.operations;

import java.util.Set;

import org.w3c.dom.Node;

import it.greenvulcano.configuration.XMLConfig;
import it.greenvulcano.configuration.XMLConfigException;
import it.greenvulcano.gvesb.buffer.GVBuffer;
import it.greenvulcano.util.metadata.PropertiesHandler;
import it.greenvulcano.util.metadata.PropertiesHandlerException;

public class Keys extends BaseOperation {
	private String pattern;
	private String separator;
	
	@Override
	public void init(Node node) throws XMLConfigException {
		super.init(node);
		
		pattern = XMLConfig.get(node, "@pattern");
		separator = XMLConfig.get(node, "@separator");
	}
	
	@Override
	public String perform(GVBuffer gvBuffer) throws PropertiesHandlerException {
		super.perform(gvBuffer);
		
		StringBuilder resp = new StringBuilder();
		separator = (separator == null) ? defaultSeparator : PropertiesHandler.expand(separator, gvBuffer);
		Set<String> res = client.keys(PropertiesHandler.expand(pattern, gvBuffer));
		
		for (String r : res) {
			resp.append(r);
			resp.append(separator);
		}
		
		String data = resp.toString();
		return data.substring(0, data.length() - separator.length());
	}
}
