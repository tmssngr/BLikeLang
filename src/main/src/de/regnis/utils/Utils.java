package de.regnis.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Thomas Singer
 */
public final class Utils {

	// Static =================================================================

	@NotNull
	public static <O> O notNull(@Nullable O value) {
		Objects.requireNonNull(value);
		return value;
	}

	@NotNull
	public static <O> O notNull(@Nullable O value, @NotNull O defaultValue) {
		return value != null ? value : defaultValue;
	}

	@Nullable
	public static <O> O getLastOrNull(List<O> list) {
		if (list.isEmpty()) {
			return null;
		}
		return getLast(list);
	}

	@NotNull
	public static <O> O getLast(List<O> list) {
		return list.get(list.size() - 1);
	}

	@Nullable
	public static String parseString(@NotNull String text, char surroundingChar) {
		final int length = text.length();
		if (length < 2 || text.charAt(0) != surroundingChar || text.charAt(length - 1) != surroundingChar) {
			return null;
		}

		final StringBuilder buffer = new StringBuilder();
		boolean wasBackslash = false;
		for (int i = 1; i < length - 1; i++) {
			char chr = text.charAt(i);
			if (wasBackslash) {
				chr = switch (chr) {
					case '\'' -> chr;
					case '"' -> chr;
					case '\\' -> chr;
					case 't' -> '\t';
					case 'n' -> '\n';
					case 'r' -> '\r';
					default -> 0;
				};
				if (chr == 0) {
					break;
				}

				wasBackslash = false;
			}
			else if (chr == '\\') {
				wasBackslash = true;
				continue;
			}

			buffer.append(chr);
		}
		return wasBackslash ? null : buffer.toString();
	}

	public static StringBuilder appendCommaSeparated(Iterable<String> collection, StringBuilder buffer) {
		return appendCommaSeparated(collection, s -> s, buffer);
	}

	public static <O> StringBuilder appendCommaSeparated(Iterable<? extends O> collection, Function<O, String> function, StringBuilder buffer) {
		return appendCommaSeparated(collection,
		                            (BiConsumer<O, StringBuilder>)
				                            (obj, stringBuilder) -> buffer.append(function.apply(obj)),
		                            buffer);
	}

	public static <O> StringBuilder appendCommaSeparated(Iterable<? extends O> collection, BiConsumer<O, StringBuilder> biConsumer, StringBuilder buffer) {
		boolean isFirst = true;
		for (O obj : collection) {
			if (isFirst) {
				isFirst = false;
			}
			else {
				buffer.append(", ");
			}
			biConsumer.accept(obj, buffer);
		}

		return buffer;
	}

	@NotNull
	public static <O extends Comparable<O>> List<O> toSortedList(Collection<O> strings) {
		final List<O> sorted = new ArrayList<>(strings);
		sorted.sort(Comparator.naturalOrder());
		return sorted;
	}

	public static <K extends Comparable<K>, V> void print(Map<K, V> map, String prefix, String padding, String suffix) {
		for (K key : toSortedList(map.keySet())) {
			System.out.print(prefix);
			System.out.print(key);
			System.out.print(padding);
			System.out.print(map.get(key));
			System.out.print(suffix);
		}
	}

	public static void assertTrue(boolean value) {
		if (!value) {
			throw new IllegalStateException();
		}
	}

	public static <S, T, C extends Collection<T>> C convert(Collection<S> source, C target, Function<S, T> function) {
		for (S sourceItem : source) {
			final T targetItem = function.apply(sourceItem);
			target.add(targetItem);
		}
		return target;
	}

	public static <S, T> Iterable<T> convert(Iterable<S> source, Function<S, T> function) {
		return new Iterable<>() {
			@NotNull
			@Override
			public Iterator<T> iterator() {
				final Iterator<S> iterator = source.iterator();
				return new Iterator<>() {
					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public T next() {
						final S sourceItem = iterator.next();
						return function.apply(sourceItem);
					}
				};
			}
		};
	}

	public static String toHex4(int v) {
		return toHex4(v, new StringBuilder()).toString();
	}

	public static StringBuilder toHex4(int v, StringBuilder buffer) {
		toHex2(v >> 8, buffer);
		return toHex2(v, buffer);
	}

	public static String toHex2(int v) {
		return toHex2(v, new StringBuilder()).toString();
	}

	public static StringBuilder toHex2(int v, StringBuilder buffer) {
		toHex(v >> 4, buffer);
		return toHex(v, buffer);
	}

	public static StringBuilder toHex(int v, StringBuilder buffer) {
		buffer.append(toHex(v));
		return buffer;
	}

	public static char toHex(int v) {
		v = v & 0x0F;
		if (v >= 10) {
			v += 'A' - '9' - 1;
		}
		return (char) (v + '0');
	}

	@Deprecated
	public static void todo() {
	}

	public static int highByte(int value) {
		return value >> 8;
	}

	public static int lowByte(int value) {
		return value & 0xFF;
	}
}
