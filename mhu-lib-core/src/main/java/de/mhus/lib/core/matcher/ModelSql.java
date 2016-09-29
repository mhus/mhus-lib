package de.mhus.lib.core.matcher;

import de.mhus.lib.core.MString;

/**
 * <p>ModelSql class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class ModelSql extends ModelPattern {

	private String pattern;

	/** {@inheritDoc} */
	@Override
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/** {@inheritDoc} */
	@Override
	protected boolean matches(String str) {
		return MString.compareSQLLikePattern(str, pattern);
	}

	/** {@inheritDoc} */
	@Override
	public String getPattern() {
		return pattern;
	}

	/** {@inheritDoc} */
	@Override
	public String getPatternTypeName() {
		return "sql";
	}

}
