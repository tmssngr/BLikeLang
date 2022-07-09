package de.regnis.b.ir;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public abstract class AbstractBlock {

	// Fields =================================================================

	private final List<AbstractBlock> prev = new ArrayList<>();
	private final List<AbstractBlock> next = new ArrayList<>();

	// Setup ==================================================================

	protected AbstractBlock(@Nullable AbstractBlock prev) {
		if (prev != null) {
			addPrev(prev);
		}
	}

	// Accessing ==============================================================

	protected final void addPrev(@NotNull AbstractBlock prev) {
		this.prev.add(prev);
		prev.next.add(this);
	}

	public final List<AbstractBlock> getPrev() {
		return Collections.unmodifiableList(prev);
	}

	public final List<AbstractBlock> getNext() {
		return Collections.unmodifiableList(next);
	}
}
