
package it.greenvulcano.gvesb.adapter.redis;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.greenvulcano.util.metadata.PropertiesHandler;
import it.greenvulcano.util.metadata.PropertiesHandlerException;
import it.greenvulcano.util.metadata.PropertyHandler;
import it.greenvulcano.util.txt.TextUtils;
import redis.clients.jedis.Jedis;

public class RedisPlaceholders implements PropertyHandler {
	private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RedisPlaceholders.class);
	private final static List<String> managedTypes = new LinkedList<>();
	private final static String separator = "::";
	
	private String endpoint = null;
	private String key = null;
	private Jedis client = null;
	
	static {
		managedTypes.add("redisGET");
		managedTypes.add("redisINCR");
		managedTypes.add("redisDECR");
		Collections.unmodifiableList(managedTypes);
	}
	
	@Override
	public List<String> getManagedTypes() {
		return managedTypes;
	}
	
	@Override
	public String expand(String type, String str, Map<String, Object> inProperties, Object object, Object extra)
			throws PropertiesHandlerException {
		try {
			if (!PropertiesHandler.isExpanded(str)) {
				str = PropertiesHandler.expand(str, inProperties, object, extra);
			}
			
			resolve(str);
			String resp = perform(type);
			return (resp != null) ? resp : str;
		}
		catch (Exception exc) {
			if (PropertiesHandler.isExceptionOnErrors()) {
				if (exc instanceof PropertiesHandlerException) {
					throw (PropertiesHandlerException) exc;
				}
				String mess = "Error handling '" + type + "' placeholder with value [" + str + "]";
				throw new PropertiesHandlerException(mess, exc);
			}
			return type + PROP_START + str + PROP_END;
		}
		finally {
			if (client != null) {
				client.close();
			}
		}
	}
	
	protected void resolve(String str) throws PropertiesHandlerException {
		try {
			List<String> tokens = TextUtils.splitByStringSeparator(str, separator);
			endpoint = tokens.get(0);
			key = tokens.get(1);
		}
		catch (IndexOutOfBoundsException e) {
			throw new PropertiesHandlerException(this.getClass().getName() + "invalid value: [" + str + "]");
		}
		logger.debug(this.getClass().getName() + "resolved: endpoint=" + endpoint + " key=" + key);
	}
	
	protected String perform(String type) {
		client = new Jedis(endpoint);
		logger.debug(this.getClass().getName() + " perform: type=" + type);
		
		if (type.equals("redisGET")) {
			return client.get(key);
		}
		else if (type.equals("redisINCR")) {
			return Long.toString(client.incr(key));
		}
		else if (type.equals("redisDECR")) {
			return Long.toString(client.decr(key));
		}
		return null;
	}
}
