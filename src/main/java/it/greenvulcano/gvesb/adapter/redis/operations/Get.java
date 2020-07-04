
package it.greenvulcano.gvesb.adapter.redis.operations;

import org.w3c.dom.Node;

import it.greenvulcano.configuration.XMLConfig;
import it.greenvulcano.configuration.XMLConfigException;
import it.greenvulcano.gvesb.buffer.GVBuffer;
import it.greenvulcano.util.metadata.PropertiesHandler;
import it.greenvulcano.util.metadata.PropertiesHandlerException;

public class Get extends BaseOperation {
	private String key;
	
	@Override
	public void init(Node node) throws XMLConfigException {
		super.init(node);
		
		key = XMLConfig.get(node, "@key");
	}
	
	@Override
	public String perform(GVBuffer gvBuffer) throws PropertiesHandlerException {
		super.perform(gvBuffer);
		
		key = PropertiesHandler.expand(key, gvBuffer);
		return client.get(key);
	}
}
