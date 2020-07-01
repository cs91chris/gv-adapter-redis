
package it.greenvulcano.gvesb.adapter.redis;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.greenvulcano.configuration.XMLConfig;
import it.greenvulcano.gvesb.adapter.redis.operations.BaseOperation;
import it.greenvulcano.gvesb.buffer.GVBuffer;
import it.greenvulcano.gvesb.virtual.CallException;
import it.greenvulcano.gvesb.virtual.CallOperation;
import it.greenvulcano.gvesb.virtual.ConnectionException;
import it.greenvulcano.gvesb.virtual.InitializationException;
import it.greenvulcano.gvesb.virtual.InvalidDataException;
import it.greenvulcano.gvesb.virtual.OperationKey;
import it.greenvulcano.util.metadata.PropertiesHandler;
import redis.clients.jedis.Jedis;

public class RedisCall implements CallOperation {
	private OperationKey key = null;
	private static final String operationsPackage = "it.greenvulcano.gvesb.adapter.redis.operations";
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(RedisCall.class);
	
	private String name;
	private String endpoint;
	private String separator;
	private Jedis client = null;
	private boolean asJson = false;
	protected NodeList operations = null;
	protected List<BaseOperation> operationInstances = new ArrayList<>();
	
	@Override
	public void init(Node node) throws InitializationException {
		try {
			name = XMLConfig.get(node, "@name");
			endpoint = XMLConfig.get(node, "@endpoint");
			separator = XMLConfig.get(node, "@separator");
			asJson = XMLConfig.getBoolean(node, "@as-json", false);
			operations = XMLConfig.getNodeList(node, "*[@type='redisOperation']");
			
			logger.debug("Initializing redis-call " + name);
			
			if (endpoint == null) {
				throw new InitializationException("endopoint argument can not be null");
			}
			
			if (operations == null) {
				logger.warn("no operations found on redis-call: " + name);
				return;
			}
			
			client = new Jedis(endpoint);
			
			for (int i = 0; i < operations.getLength(); i++) {
				BaseOperation op = null;
				Node opNode = operations.item(i);
				String className = XMLConfig.get(opNode, "@class");
				
				if (className == null || className.equals("")) {
					className = operationsPackage + "." + opNode.getNodeName();
				}
				
				logger.debug("creating object from class: " + className);
				op = (BaseOperation) Class.forName(className).newInstance();
				op.setClient(client);
				
				op.init(opNode);
				operationInstances.add(op);
			}
			
			logger.debug("Configured Redis Call: " + name);
		}
		catch (Exception e) {
			String[][] messages = new String[][] { { "message", e.getMessage() } };
			throw new InitializationException("GV_INIT_SERVICE_ERROR", messages, e);
		}
	}
	
	@Override
	public GVBuffer perform(GVBuffer gvBuffer) throws ConnectionException, CallException, InvalidDataException {
		try {
			logger.debug("executing Redis call: " + name);
			separator = (separator == null) ? BaseOperation.defaultSeparator
					: PropertiesHandler.expand(separator, gvBuffer);
			
			if (asJson == true) {
				JSONArray json = new JSONArray();
				for (BaseOperation op : operationInstances) {
					json.put(op.perform(gvBuffer));
				}
				gvBuffer.setObject(json.toString());
			}
			else {
				StringBuilder resp = new StringBuilder();
				for (BaseOperation op : operationInstances) {
					resp.append(op.perform(gvBuffer));
					resp.append(separator);
				}
				String data = resp.toString();
				gvBuffer.setObject(data.substring(0, data.length() - 1));
			}
		}
		catch (Exception exc) {
			throw new CallException("GV_CALL_SERVICE_ERROR",
					new String[][] { { "service", gvBuffer.getService() }, { "system", gvBuffer.getSystem() },
							{ "tid", gvBuffer.getId().toString() }, { "message", exc.getMessage() } },
					exc);
		}
		return gvBuffer;
	}
	
	@Override
	public void cleanUp() {
		for (BaseOperation op : operationInstances) {
			op.cleanUp();
		}
		client.close();
	}
	
	@Override
	public void destroy() {
		
	}
	
	@Override
	public void setKey(OperationKey operationKey) {
		this.key = operationKey;
	}
	
	@Override
	public OperationKey getKey() {
		return key;
	}
	
	@Override
	public String getServiceAlias(GVBuffer gvBuffer) {
		return gvBuffer.getService();
	}
}
