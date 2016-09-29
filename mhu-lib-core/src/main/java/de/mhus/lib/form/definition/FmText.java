package de.mhus.lib.form.definition;

import de.mhus.lib.core.definition.IDefAttribute;

/**
 * <p>FmText class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 * @since 3.3.0
 */
public class FmText extends FmElement {

	/**
	 * <p>Constructor for FmText.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param title a {@link java.lang.String} object.
	 * @param description a {@link java.lang.String} object.
	 * @param definitions a {@link de.mhus.lib.core.definition.IDefAttribute} object.
	 */
	public FmText(String name, String title, String description, IDefAttribute ... definitions) {
		this(name, new FmNls(title, description));
		addDefinition(definitions);
	}
	
	/**
	 * <p>Constructor for FmText.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param definitions a {@link de.mhus.lib.core.definition.IDefAttribute} object.
	 */
	public FmText(String name, IDefAttribute ... definitions) {
		super(name, definitions);
		setString("type", "text");
	}


}
