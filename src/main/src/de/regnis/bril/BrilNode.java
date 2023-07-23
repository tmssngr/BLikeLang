package de.regnis.bril;

import de.regnis.utils.Utils;

import java.util.*;

/**
 * @author Thomas Singer
 */
public final class BrilNode {

	// Fields =================================================================

	private final Map<String, Object> children = new HashMap<>();

	// Setup ==================================================================

	public BrilNode() {
	}

	// Implemented ============================================================

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final BrilNode brilNode = (BrilNode) o;
		return Objects.equals(children, brilNode.children);
	}

	@Override
	public int hashCode() {
		return Objects.hash(children);
	}

	@Override
	public String toString() {
		final List<String> keys = new ArrayList<>(children.keySet());
		keys.sort(Comparator.naturalOrder());
		final StringBuilder buffer = new StringBuilder();
		buffer.append("[");
		Utils.appendCommaSeparated(keys, key -> key + ": " + children.get(key), buffer);
		buffer.append("]");
		return buffer.toString();
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

	public BrilNode set(String key, boolean value) {
		children.put(key, value);
		return this;
	}

	public BrilNode set(String key, List<String> values) {
		children.put(key, values);
		return this;
	}

	public BrilNode setNodes(String key, List<BrilNode> nodes) {
		children.put(key, nodes);
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

	public List<BrilNode> getNodeList(String key) {
		//noinspection unchecked
		List<BrilNode> list = (List<BrilNode>) children.get(key);
		if (list == null) {
			list = new ArrayList<>();
		}

		return Collections.unmodifiableList(list);
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

	public int getInt(String key) {
		return (Integer)children.get(key);
	}

	public boolean getBoolean(String key) {
		return (Boolean)children.get(key);
	}

	public List<String> getStringList(String key) {
		//noinspection unchecked
		List<String> list = (List<String>) children.get(key);
		if (list == null) {
			list = new ArrayList<>();
		}

		return list;
	}

	public Map<String, Object> getMap(Collection<String> keys) {
		final Map<String, Object> map = new LinkedHashMap<>();
		for (String key : keys) {
			if (children.containsKey(key)) {
				map.put(key, children.get(key));
			}
		}
		return map;
	}
}
