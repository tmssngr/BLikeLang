package de.regnis.bril;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Singer
 */
public final class BrilNode {

	// Fields =================================================================

	private final Map<String, Object> children = new HashMap<>();

	// Setup ==================================================================

	public BrilNode() {
	}

	// Accessing ==============================================================

	public BrilNode set(String key, String value) {
		children.put(key, value);
		return this;
	}

	public BrilNode set(String key, int value) {
		children.put(key, value);
		return this;
	}

	public BrilNode set(String key, List<String> values) {
		children.put(key, values);
		return this;
	}

	public List<BrilNode> getOrCreateNodeList(String key) {
		//noinspection unchecked
		List<BrilNode> list = (List<BrilNode>) children.get(key);
		if (list == null) {
			list = new ArrayList<>();
			children.put(key, list);
		}

		return list;
	}

	public List<String> getOrCreateStringList(String key) {
		//noinspection unchecked
		List<String> list = (List<String>) children.get(key);
		if (list == null) {
			list = new ArrayList<>();
			children.put(key, list);
		}

		return list;
	}

	public String getString(String key) {
		return (String) children.get(key);
	}
}
