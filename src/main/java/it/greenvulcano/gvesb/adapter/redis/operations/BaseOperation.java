
package it.greenvulcano.gvesb.adapter.redis.operations;

import org.slf4j.Logger;
import org.w3c.dom.Node;

import it.greenvulcano.configuration.XMLConfig;
import it.greenvulcano.configuration.XMLConfigException;
import it.greenvulcano.gvesb.buffer.GVBuffer;
import it.greenvulcano.util.metadata.PropertiesHandler;
import it.greenvulcano.util.metadata.PropertiesHandlerException;
import redis.clients.jedis.Jedis;

public abstract class BaseOperation {
	public static final String defaultSeparator = "\n";
	protected static final Logger logger = org.slf4j.LoggerFactory.getLogger(BaseOperation.class);
	protected Jedis client = null;
	protected String endpoint = null;
	
	public void setClient(Jedis client) {
		this.client = client;
	}
	
	public void init(Node node) throws XMLConfigException {
		endpoint = XMLConfig.get(node, "@endpoint");
	}
	
	public String perform(GVBuffer gvBuffer) throws PropertiesHandlerException {
		if (endpoint != null && endpoint != "") {
			client = new Jedis(PropertiesHandler.expand(endpoint, gvBuffer));
		}
		return null;
	}
	
	public void cleanUp() {
		this.client.close();
	}
}
