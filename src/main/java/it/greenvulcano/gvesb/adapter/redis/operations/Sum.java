
package it.greenvulcano.gvesb.adapter.redis.operations;

import org.w3c.dom.Node;

import it.greenvulcano.configuration.XMLConfig;
import it.greenvulcano.configuration.XMLConfigException;
import it.greenvulcano.gvesb.buffer.GVBuffer;
import it.greenvulcano.util.metadata.PropertiesHandler;
import it.greenvulcano.util.metadata.PropertiesHandlerException;

public class Sum extends BaseOperation {
	private String key = null;
	private String value = null;
	
	@Override
	public void init(Node node) throws XMLConfigException {
		super.init(node);
		
		key = XMLConfig.get(node, "@key");
		value = XMLConfig.get(node, "@value", "1");
	}
	
	@Override
	public String perform(GVBuffer gvBuffer) throws PropertiesHandlerException {
		super.perform(gvBuffer);
		
		key = PropertiesHandler.expand(key, gvBuffer);
		Long numValue = Long.parseLong(PropertiesHandler.expand(value, gvBuffer));
		
		if (numValue > 0) {
			return Long.toString(client.incrBy(key, numValue));
		}
		return Long.toString(client.decrBy(key, Math.abs(numValue)));
	}
}
