package de.regnis.b.ast;

import de.regnis.b.type.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Thomas Singer
 */
public final class VarDeclaration extends SimpleStatement {

	// Static =================================================================

	@NotNull
	public static VarDeclaration createTempVarDeclaration(@NotNull String name, @NotNull Expression expression) {
		return expression.hasType()
				? new VarDeclaration(name, expression.getType(), expression)
				: new VarDeclaration(name, expression);
	}

	// Fields =================================================================

	public final Type type;
	public final String typeName;
	public final String name;
	public final Expression expression;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public VarDeclaration(@NotNull String name, @NotNull Expression expression) {
		this(null, name, expression, -1, -1, null);
	}

	public VarDeclaration(@Nullable String typeName, @NotNull String name, @NotNull Expression expression, int line, int column) {
		this(typeName, name, expression, line, column, null);
	}

	public VarDeclaration(@NotNull String name, @NotNull Type type, @NotNull Expression expression) {
		this(type.toString(), name, expression, -1, -1, type);
	}

	private VarDeclaration(@Nullable String typeName, @NotNull String name, @NotNull Expression expression, int line, int column, @Nullable Type type) {
		this.typeName = typeName;
		this.name = name;
		this.expression = expression;
		this.line = line;
		this.column = column;
		this.type = type;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "declare(" + name + ": " + typeName + " = " + expression + ")";
	}

	@Override
	public <O> O visit(StatementVisitor<O> visitor) {
		return visitor.visitLocalVarDeclaration(this);
	}

	// Accessing ==============================================================

	public VarDeclaration derive(@NotNull Expression expression) {
		return new VarDeclaration(typeName, name, expression, line, column, type);
	}
}
