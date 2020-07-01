
package it.greenvulcano.gvesb.adapter.redis.operations;

import org.w3c.dom.Node;

import it.greenvulcano.configuration.XMLConfig;
import it.greenvulcano.configuration.XMLConfigException;
import it.greenvulcano.gvesb.buffer.GVBuffer;
import it.greenvulcano.util.metadata.PropertiesHandler;
import it.greenvulcano.util.metadata.PropertiesHandlerException;
import redis.clients.jedis.params.SetParams;

public class Set extends BaseOperation {
	private String key = null;
	private String value = null;
	private String expire = null;
	private String onlyIf = null;
	private boolean append = false;
	
	@Override
	public void init(Node node) throws XMLConfigException {
		super.init(node);
		
		key = XMLConfig.get(node, "@key");
		value = XMLConfig.get(node, "@value");
		append = XMLConfig.getBoolean(node, "@append", false);
		expire = XMLConfig.get(node, "@expire-millis");
		onlyIf = XMLConfig.get(node, "@only-if");
	}
	
	@Override
	public String perform(GVBuffer gvBuffer) throws PropertiesHandlerException {
		super.perform(gvBuffer);
		
		key = PropertiesHandler.expand(key, gvBuffer);
		value = PropertiesHandler.expand(value, gvBuffer);
		
		if (append == true) {
			return Long.toString(client.append(key, value));
		}
		
		SetParams params = new SetParams();
		
		if (expire != null) {
			params = params.px(Long.parseLong(PropertiesHandler.expand(expire, gvBuffer)));
		}
		
		if (onlyIf != null) {
			switch (onlyIf.toLowerCase()) {
				case "exists":
					params = params.xx();
					break;
				case "not-exists":
					params = params.nx();
					break;
			}
		}
		
		return client.set(key, value, params);
	}
}
