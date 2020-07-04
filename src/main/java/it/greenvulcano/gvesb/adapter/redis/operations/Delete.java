
package it.greenvulcano.gvesb.adapter.redis.operations;

import org.w3c.dom.Node;

import it.greenvulcano.configuration.XMLConfig;
import it.greenvulcano.configuration.XMLConfigException;
import it.greenvulcano.gvesb.buffer.GVBuffer;
import it.greenvulcano.util.metadata.PropertiesHandler;
import it.greenvulcano.util.metadata.PropertiesHandlerException;

public class Delete extends BaseOperation {
	private String keys = null;
	private String separator = null;
	
	@Override
	public void init(Node node) throws XMLConfigException {
		super.init(node);
		
		keys = XMLConfig.get(node, "@keys");
		separator = XMLConfig.get(node, "@separator");
	}
	
	@Override
	public String perform(GVBuffer gvBuffer) throws PropertiesHandlerException {
		super.perform(gvBuffer);
		
		keys = PropertiesHandler.expand(keys, gvBuffer);
		separator = (separator == null) ? defaultSeparator : PropertiesHandler.expand(separator, gvBuffer);
		
		return client.delete(keys, separator);
	}
}
