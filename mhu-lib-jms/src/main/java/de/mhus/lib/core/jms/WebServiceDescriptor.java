package de.mhus.lib.core.jms;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebService;

import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.pojo.AnnotationFilter;
import de.mhus.lib.core.pojo.DefaultFilter;
import de.mhus.lib.core.pojo.FunctionsOnlyStrategy;
import de.mhus.lib.core.pojo.FunctionsStrategy;
import de.mhus.lib.core.pojo.PojoAction;
import de.mhus.lib.core.pojo.PojoModel;
import de.mhus.lib.core.pojo.PojoParser;

public class WebServiceDescriptor extends ServiceDescriptor {

	private Object service;
	private PojoModel model;

	public WebServiceDescriptor(Object service) {
		super(findIfc(service));
		this.service = service;
		model = new PojoParser().parse(service, new FunctionsOnlyStrategy(true, WebMethod.class) ).getModel();
		
		for (String name : model.getActionNames()) {
			PojoAction act = model.getAction(name);
			functions.put(name, new WebServiceFunction(act));
		}
		
	}
	
	private static Class<?> findIfc(Object service) {
		// TODO traverse thru all ifcs
		Class<?> c = service instanceof Class ? (Class)service : service.getClass();
		if (c.isAnnotationPresent(WebService.class)) return c;
		for (Class<?> i : c.getInterfaces()) {
			if (i.isAnnotationPresent(WebService.class)) return i;
		}
		return c;
	}

	private class WebServiceFunction extends FunctionDescriptor {

		private PojoAction act;

		public WebServiceFunction(PojoAction act) {
			this.act = act;
//			oneWay = act.getAnnotation(Oneway.class) != null || act.getReturnType() == null;
			oneWay = act.getAnnotation(Oneway.class) != null;
		}

		@Override
		public RequestResult<Object> doExecute(IProperties properties,
				Object[] obj) {
			
			// TODO check special case for direct handling
			
			MProperties p = new MProperties();
			Object res = null;
			Throwable t = null;
			try {
				res = act.doExecute(service, obj);
			} catch (IOException e) {
				t = e.getCause();
				if (t instanceof InvocationTargetException) {
					t = t.getCause();
				}
				if (t == null) t = e;
			} catch (Throwable e) {
				t = e;
			}
			if (t != null) {
				p.setString("exception", t.getClass().getCanonicalName());
				p.setString("exceptionMessage", t.getMessage());
				p.setString("exceptionClass", act.getManagedClass().getCanonicalName());
				p.setString("exceptionMethod", act.getName());
				
				log().t(act.getManagedClass().getCanonicalName(),act.getName(),t);
			}
			return new RequestResult<Object>(res, p);
		}
		
	}
	
}
