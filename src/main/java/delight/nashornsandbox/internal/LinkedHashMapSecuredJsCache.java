package delight.nashornsandbox.internal;

import java.util.LinkedHashMap;
import java.util.function.Supplier;

import delight.nashornsandbox.SecuredJsCache;

/**
 * Default implementation of {@link SecuredJsCache}, uses a {@link LinkedHashMap}
 * as its cache and is not thread-safe. Also, mixing scripts with missing braces
 * allowed and not allowed is forbidden.
 */
class LinkedHashMapSecuredJsCache implements SecuredJsCache {

	private final LinkedHashMap<String, String> map;

	public LinkedHashMapSecuredJsCache(LinkedHashMap<String, String> linkedHashMap) {
		this.map = linkedHashMap;
	}

	@Override
	public String getOrCreate(String js, Supplier<String> producer) {
		String result = map.get(js);
		if (result == null) {
			result = producer.get();
			map.put(js, result);
		}
		return result;
	}

}
