package de.mhus.lib.core.pojo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.Set;

import de.mhus.lib.core.lang.MObject;

/**
 * <p>FunctionsOnlyStrategy class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class FunctionsOnlyStrategy extends MObject implements PojoStrategy {

	private boolean toLower = true;
	private Class<? extends Annotation>[] annotationMarker;
	
	/**
	 * <p>Constructor for FunctionsOnlyStrategy.</p>
	 */
	public FunctionsOnlyStrategy() {
		this(true);
	}
		
	/**
	 * <p>Constructor for FunctionsOnlyStrategy.</p>
	 *
	 * @param toLower a boolean.
	 * @param annotationMarker a {@link java.lang.Class} object.
	 */
	@SafeVarargs
	public FunctionsOnlyStrategy(boolean toLower, Class<? extends Annotation> ... annotationMarker) {
		this.toLower = toLower;
		this.annotationMarker = annotationMarker;
	}
	
	/** {@inheritDoc} */
	@Override
	public void parse(PojoParser parser, Class<?> clazz, PojoModelImpl model) {
		parse("", null, parser, clazz, model, 0);
	}
	
	/**
	 * <p>parse.</p>
	 *
	 * @param prefix a {@link java.lang.String} object.
	 * @param parent a {@link de.mhus.lib.core.pojo.FunctionAttribute} object.
	 * @param parser a {@link de.mhus.lib.core.pojo.PojoParser} object.
	 * @param clazz a {@link java.lang.Class} object.
	 * @param model a {@link de.mhus.lib.core.pojo.PojoModelImpl} object.
	 * @param level a int.
	 */
	protected void parse(String prefix, FunctionAttribute<Object> parent, PojoParser parser, Class<?> clazz, PojoModelImpl model, int level) {

		if (level > 10 ) return; // logging ?
		
		for (Method m : getMethods(clazz)) {

			// ignore static methods
			if (Modifier.isStatic(m.getModifiers()))
					continue;

			try {
				String mName = m.getName();
				String s = (toLower ? mName.toLowerCase() : mName);
				String name = prefix + s;

				if (isMarker(clazz, m)) {
					// everything else is an action
					FunctionAction action = new FunctionAction(clazz,m,name,parent);
					model.addAction(action);
				}
				
			} catch (Exception e) {
				log().d(e);
			}
		}
	}
	
	private boolean isMarker(Class<?> clazz, Method m) {
		if (annotationMarker == null || annotationMarker.length == 0) return true;
		if (m != null) {
			for (Class<? extends Annotation> a :annotationMarker)
				if (m.isAnnotationPresent(a)) return true;
			Set<Method> res = MethodAnalyser.getMethodsForMethod(clazz, m.getName());
			for (Method m2 : res) {
				for (Class<? extends Annotation> a :annotationMarker)
					if (m2.isAnnotationPresent(a)) return true;
			}
		}
		return false;
	}

	/**
	 * <p>getMethods.</p>
	 *
	 * @param clazz a {@link java.lang.Class} object.
	 * @return a {@link java.util.LinkedList} object.
	 */
	protected LinkedList<Method> getMethods(Class<?> clazz) {
		LinkedList<Method> out = new LinkedList<Method>();
//		HashSet<String> names = new HashSet<String>();
		do {
			for (Method m : clazz.getMethods()) {
//				if (!names.contains(m.getName())) {
					out.add(m);
//					names.add(m.getName());
//				}
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		
		return out;
	}

	/** {@inheritDoc} */
	@Override
	public void parseObject(PojoParser parser, Object pojo, PojoModelImpl model) {
		Class<?> clazz = pojo.getClass();
		parse(parser, clazz, model);
	}
	
}
