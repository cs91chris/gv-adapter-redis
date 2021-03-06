
package it.greenvulcano.gvesb.adapter.redis.operations;

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
		
		separator = (separator == null) ? defaultSeparator : PropertiesHandler.expand(separator, gvBuffer);
		return client.keys(PropertiesHandler.expand(pattern, gvBuffer), separator);
	}
}
