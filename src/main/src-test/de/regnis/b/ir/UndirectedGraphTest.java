package de.regnis.b.ir;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Thomas Singer
 */
public class UndirectedGraphTest {

	// Accessing ==============================================================

	@Test
	public void testGraph() {
		final UndirectedGraph<String> graph = new UndirectedGraph<>();
		graph.addEdgesBetween("v0");
		graph.addEdgesBetween("v0", "v1");
		graph.addEdgesBetween("v0", "v1", "v2");
		graph.addEdgesBetween("v0", "v1");
		graph.addEdgesBetween("$1", "v1");
		graph.addEdgesBetween("$2");

		assertEquals(Set.of("v0", "v1", "v2", "$1", "$2"), graph.getObjects());

		assertTrue(hasEdgeBetween(graph, "v0", "v1"));
		assertTrue(hasEdgeBetween(graph, "v0", "v2"));
		assertTrue(hasEdgeBetween(graph, "v1", "v2"));
		assertTrue(hasEdgeBetween(graph, "$1", "v1"));
		assertFalse(hasEdgeBetween(graph, "v0", "$1"));
		assertFalse(hasEdgeBetween(graph, "$2", "v0"));
		assertFalse(hasEdgeBetween(graph, "$2", "v1"));
		assertFalse(hasEdgeBetween(graph, "$2", "v2"));
		assertFalse(hasEdgeBetween(graph, "$2", "$1"));

		assertEquals(Set.of("v1", "v2"), graph.getEdges("v0"));
		assertEquals(Set.of("v0", "v2", "$1"), graph.getEdges("v1"));
		assertEquals(Set.of("v0", "v1"), graph.getEdges("v2"));
		assertEquals(Set.of("v1"), graph.getEdges("$1"));
		assertEquals(Set.of(), graph.getEdges("$2"));

		try {
			graph.getEdges("notContained");
			fail();
		}
		catch (IllegalArgumentException expected) {
		}
	}

	// Utils ==================================================================

	private boolean hasEdgeBetween(UndirectedGraph<String> graph, String o1, String o2) {
		final boolean v1 = graph.hasEdgeBetween(o1, o2);
		assertEquals(v1, graph.hasEdgeBetween(o2, o1));
		return v1;
	}
}
