package de.regnis.b.ir;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Thomas Singer
 */
public final class UndirectedGraph<O> {

	// Fields =================================================================

	private final Map<O, Vertex<O>> vertices = new LinkedHashMap<>();

	// Setup ==================================================================

	public UndirectedGraph() {
	}

	// Accessing ==============================================================

	public void addEdgesBetween(O... objects) {
		addEdgesBetween(Set.of(objects));
	}

	public void addEdgesBetween(@NotNull Set<O> objects) {
		for (O object : objects) {
			final Vertex<O> vertex = getOrCreateVertex(object);

			for (O otherObject : objects) {
				if (otherObject == object) {
					continue;
				}

				vertex.edgesTo.add(otherObject);
			}
		}
	}

	public Set<O> getObjects() {
		return Collections.unmodifiableSet(vertices.keySet());
	}

	public Set<O> getEdges(@NotNull O object) {
		return Collections.unmodifiableSet(getVertexNotNull(object).edgesTo);
	}

	public boolean hasEdgeBetween(@NotNull O o1, @NotNull O o2) {
		return getVertexNotNull(o1).edgesTo.contains(o2);
	}

	// Utils ==================================================================

	@NotNull
	private Vertex<O> getVertexNotNull(@NotNull O object) {
		final Vertex<O> vertex = vertices.get(object);
		if (vertex == null) {
			throw new IllegalArgumentException();
		}
		return vertex;
	}

	@NotNull
	private Vertex<O> getOrCreateVertex(@NotNull O object) {
		Vertex<O> vertex = vertices.get(object);
		if (vertex == null) {
			vertex = new Vertex<>();
			vertices.put(object, vertex);
		}
		return vertex;
	}

	// Inner Classes ==========================================================

	private static final class Vertex<O> {
		private final Set<O> edgesTo = new HashSet<>();

		@Override
		public String toString() {
			return edgesTo.toString();
		}
	}
}
