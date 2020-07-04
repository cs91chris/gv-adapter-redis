
package it.greenvulcano.gvesb.adapter.redis;

import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class RedisAPI {
	private Jedis client = null;
	
	public RedisAPI(String endpoint) {
		this.client = new Jedis(endpoint);
	}
	
	public void close() {
		client.close();
	}
	
	public String get(String key) {
		return client.get(key);
	}
	
	public String decrement(String key) {
		return Long.toString(client.decr(key));
	}
	
	public String delete(String keys, String separator) {
		return client.del(keys.split(separator)).toString();
	}
	
	public String increment(String key) {
		return Long.toString(client.incr(key));
	}
	
	public String keys(String pattern, String separator) {
		StringBuilder resp = new StringBuilder();
		Set<String> res = client.keys(pattern);
		
		for (String r : res) {
			resp.append(r);
			resp.append(separator);
		}
		
		String data = resp.toString();
		return data.substring(0, data.length() - separator.length());
	}
	
	public String set(String key, String value, boolean append, Long expire, String onlyIf) {
		if (append == true) {
			return Long.toString(client.append(key, value));
		}
		
		SetParams params = new SetParams();
		
		if (expire != null) {
			params = params.px(expire);
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
	
	public String sum(String key, int value) {
		if (value > 0) {
			return Long.toString(client.incrBy(key, value));
		}
		return Long.toString(client.decrBy(key, Math.abs(value)));
	}
}
