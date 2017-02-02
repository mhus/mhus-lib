package de.mhus.lib.vaadin.desktop;

public interface Navigable {

	/**
	 * Navigate to a selected resource.
	 * 
	 * @param selection
	 * @param filter
	 * @return null if not possible or the title of the resource to display
	 */
	String navigateTo(String selection, String filter);
	
}