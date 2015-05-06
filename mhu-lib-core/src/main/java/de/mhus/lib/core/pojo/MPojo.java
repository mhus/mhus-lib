package de.mhus.lib.core.pojo;

import java.io.IOException;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.w3c.dom.Element;

import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.util.Base64;

public class MPojo {

	private static final String MAGIC = "\n\t\t\r[[";

	private static PojoModel createModel(Object from) {
		PojoModel model = new PojoParser().parse(from,"_",null).filter(new DefaultFilter(true, false, false, false, true) ).getModel();
		return model;
	}
	
	public static void pojoToJson(Object from, ObjectNode to) throws IOException {
		pojoToJson(from, to, createModel(from));
	}
	
	public static void pojoToJson(Object from, ObjectNode to, PojoModel model) throws IOException {
		for (PojoAttribute<?> attr : model) {
			Object value = attr.get(from);
			String name = attr.getName();
			if (value == null)
				to.put(name, (String)null);
			else
			if (value instanceof Boolean)
				to.put(name, (boolean)value);
			else
			if (value instanceof Integer)
				to.put(name, (int)value);
			else
			if (value instanceof String)
				to.put(name, (String)value);
			else
			if (value.getClass().isEnum()) {
				to.put(name, ((Enum<?>)value).ordinal());
				to.put(name + "_", ((Enum<?>)value).name());
			}
			else
				to.put(attr.getName(), String.valueOf(value));
		}
	}

	public static void jsonToPojo(JsonNode from, Object to) throws IOException {
		jsonToPojo(from, to, createModel(to));
	}
	
	@SuppressWarnings("unchecked")
	public static void jsonToPojo(JsonNode from, Object to, PojoModel  model) throws IOException {
		for (PojoAttribute<Object> attr : model) {
			String name = attr.getName();
			Class<?> type = attr.getType();
			JsonNode json = from.get(name);
			
			try {
				if (json == null || !attr.canWrite() ) {
					
				} else
				if (type == Boolean.class || type == boolean.class)
					attr.set(to, json.getValueAsBoolean(false));
				else
				if (type == Integer.class || type == int.class)
					attr.set(to, json.getValueAsInt(0));
				else
				if (type == String.class)
					attr.set(to, json.getValueAsText());
				else
				if (type == UUID.class)
					try {
						attr.set(to, UUID.fromString(json.getValueAsText()));
					} catch (IllegalArgumentException e) {
						attr.set(to, null);
					}
				else
				if (type.isEnum()) {
					Object[] cons=type.getEnumConstants();
					int ord = json.getValueAsInt(0);
					Object c = cons.length > 0 ? cons[0] : null;
					if (ord >=0 && ord < cons.length) c = cons[ord];
					attr.set(to, c );
				}
				else
					attr.set(to, json.getValueAsText());
			} catch (Throwable t) {
				System.out.println("ERROR " + name);
				t.printStackTrace();
			}
		}
	}

	public static void pojoToXml(Object from, Element to) throws IOException {
		pojoToXml(from, to, createModel(from));
	}

	public static void pojoToXml(Object from, Element to, PojoModel model) throws IOException {
		for (PojoAttribute<?> attr : model) {
			Object value = attr.get(from);
			String name = attr.getName();
			if (value == null) {
				//to.setAttribute(name, (String)null);
			} else
			if (value instanceof Boolean)
				to.setAttribute(name, MCast.toString((boolean)value));
			else
			if (value instanceof Integer)
				to.setAttribute(name, MCast.toString((int)value));
			else
			if (value instanceof String)
				to.setAttribute(name, toAttribute( (String)value) );
			else
			if (value.getClass().isEnum()) {
				to.setAttribute(name, MCast.toString( ((Enum<?>)value).ordinal() ) );
				//to.setAttribute(name + "_", ((Enum<?>)value).name());
			}
			else
				to.setAttribute(attr.getName(), toAttribute( MCast.toString(value)) );
		}
	}

	private static String fromAttribute(String value) {
		if (value == null) return null;
		if (value.equals(MAGIC + "null")) return null;
		if (value.startsWith(MAGIC)) {
			new String( Base64.decode( value.substring(MAGIC.length()) ) );
		}
		return value;
	}
	private static String toAttribute(String value) {
		if (value == null) return MAGIC + "null";
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == '\n' || c == '\r' || c == '\t' || c >= 32 && c <= 55295 ) {
			} else {
				return MAGIC + Base64.encode(value);
			}
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	public static void xmlToPojo(Element from, Object to) throws IOException {
		xmlToPojo(from, to, createModel(to));
	}
	
	@SuppressWarnings("unchecked")
	public static void xmlToPojo(Element from, Object to, PojoModel model) throws IOException {
		for (PojoAttribute<Object> attr : model) {
			String name = attr.getName();
			Class<?> type = attr.getType();
			String value = from.getAttribute(name);
			
			try {
				if (value == null || !attr.canWrite() ) {
					
				} else
				if (type == Boolean.class || type == boolean.class)
					attr.set(to, value);
				else
				if (type == Integer.class || type == int.class)
					attr.set(to, value);
				else
				if (type == String.class)
					attr.set(to, fromAttribute( value ) );
				else
				if (type == UUID.class)
					try {
						attr.set(to, UUID.fromString(value));
					} catch (IllegalArgumentException e) {
						attr.set(to, null);
					}
				else
				if (type.isEnum()) {
					Object[] cons=type.getEnumConstants();
					int ord = MCast.toint(value, 0);
					Object c = cons.length > 0 ? cons[0] : null;
					if (ord >=0 && ord < cons.length) c = cons[ord];
					attr.set(to, c );
				}
				else
					attr.set(to, fromAttribute( value ) );
			} catch (Throwable t) {
				System.out.println("ERROR " + name);
				t.printStackTrace();
			}
		}
	}
	
	/**
	 * Functionize a String. Remove bad names and set first characters to upper. Return def if the name
	 * can't be created, e.g. only numbers.
	 * 
	 * @param in
	 * @param firstUpper 
	 * @param def
	 * @return
	 */
	public static String toFunctionName(String in, boolean firstUpper,String def) {
		if (MString.isEmpty(in)) return def;
		boolean first = firstUpper;
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < in.length(); i++) {
			char c = in.charAt(i);
			if (c >= 'a' && c <= 'z' || c >='A' && c <='Z' || c == '_') {
				if (first)
					c = Character.toUpperCase(c);
				first = false;
				out.append(c);
			} else
			if (!first && c >='0' && c <= '9') {
				out.append(c);
			} else {
				first = true;
			}
		}
		
		if (out.length() == 0) return def;
		return out.toString();
	}
	
	
	
}
