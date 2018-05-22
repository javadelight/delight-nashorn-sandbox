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
	private final boolean allowNoBraces;

	public LinkedHashMapSecuredJsCache(LinkedHashMap<String, String> linkedHashMap, boolean allowNoBraces) {
		this.map = linkedHashMap;
		this.allowNoBraces = allowNoBraces;
	}

	@Override
	public String getOrCreate(String js, boolean allowNoBraces, Supplier<String> producer) {
		assertConfiguration(allowNoBraces);
		String result = map.get(js);
		if (result == null) {
			result = producer.get();
			map.put(js, result);
		}
		return result;
	}

	private void assertConfiguration(boolean allowNoBraces) {
		if (allowNoBraces != this.allowNoBraces) {
			throw new IllegalArgumentException("Non-matching cache configuration");
		}
	}

}
