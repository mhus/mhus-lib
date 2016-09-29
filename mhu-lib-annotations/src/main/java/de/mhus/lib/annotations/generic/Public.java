package de.mhus.lib.annotations.generic;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>Public class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Public {
	String[] hints() default {};
	String description() default "";
	boolean readable() default true;
	boolean writeable() default true;
	String name() default "";
}
