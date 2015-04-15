package de.mhus.lib.vaadin.form2;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.PasswordField;

public class UiPassword extends UiText {

	@SuppressWarnings("rawtypes")
	@Override
	protected AbstractField createTextField() {
		return new PasswordField();
	}

}
