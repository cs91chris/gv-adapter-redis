
package it.greenvulcano.gvesb.adapter.redis.operations;

import org.w3c.dom.Node;

import it.greenvulcano.configuration.XMLConfig;
import it.greenvulcano.configuration.XMLConfigException;
import it.greenvulcano.gvesb.buffer.GVBuffer;
import it.greenvulcano.util.metadata.PropertiesHandler;
import it.greenvulcano.util.metadata.PropertiesHandlerException;

public class Set extends BaseOperation {
	private String key = null;
	private String value = null;
	private Long expire = null;
	private String onlyIf = null;
	private boolean append = false;
	
	@Override
	public void init(Node node) throws XMLConfigException {
		super.init(node);
		
		key = XMLConfig.get(node, "@key");
		value = XMLConfig.get(node, "@value");
		onlyIf = XMLConfig.get(node, "@only-if");
		append = XMLConfig.getBoolean(node, "@append", false);
		String millis = XMLConfig.get(node, "@expire-millis");
		expire = (millis != null) ? Long.parseLong(millis) : null;
	}
	
	@Override
	public String perform(GVBuffer gvBuffer) throws PropertiesHandlerException {
		super.perform(gvBuffer);
		
		key = PropertiesHandler.expand(key, gvBuffer);
		if(value != null) {
			value = PropertiesHandler.expand(value, gvBuffer);
		}
		else {
			value = (String) gvBuffer.getObject();
		}
		return client.set(key, value, append, expire, onlyIf);
	}
}
