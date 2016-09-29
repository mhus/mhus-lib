package de.mhus.lib.core.jmx;

import java.lang.ref.WeakReference;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import de.mhus.lib.core.lang.MObject;

final class MBeanProxy extends MObject implements DynamicMBean {

    /**
     * The real MBean object.
     */
    private final ReferenceImpl weak;
    private MRemoteManager server;
    private ObjectName name;
//	private Object real;
	private JmxDescription desc;

    /**
     * <p>Constructor for MBeanProxy.</p>
     *
     * @param realObject a {@link java.lang.Object} object.
     * @param desc a {@link de.mhus.lib.core.jmx.JmxDescription} object.
     * @param server a {@link de.mhus.lib.core.jmx.MRemoteManager} object.
     * @param name a {@link javax.management.ObjectName} object.
     * @param weak a boolean.
     * @throws java.lang.Exception if any.
     */
    public MBeanProxy(Object realObject,JmxDescription desc,MRemoteManager server,ObjectName name,boolean weak) throws Exception {
    	// have tow references, the real only to hold the project for a while, depends on the strategy
        this.weak = new ReferenceImpl(realObject);
//        if (!weak) this.real = realObject;
        this.desc = desc;
        if (desc == null) this.desc = JmxDescription.create(realObject);
        this.server = server;
        this.name = name;
        if (realObject instanceof JmxObject) {
        	((JmxObject)realObject).setJmxProxy(this);
        }
    }

    private class ReferenceImpl extends WeakReference<Object> {
        public ReferenceImpl(Object referent) {
            super(referent);
        }
    }

	/** {@inheritDoc} */
	@Override
	public Object getAttribute(String attribute)
			throws AttributeNotFoundException, MBeanException,
			ReflectionException {
		
		return desc.getAttribute(getObject(), attribute);
	}

	private Object getObject() {
		Object out = weak.get();
		if (out == null) {
			unregsiter();
			throw new IllegalStateException(name + " no longer exists");
		}
		return out;
	}

	private synchronized void unregsiter() {
		if (server == null) return;
		server.unregister(name);
		server = null;
	}

	/** {@inheritDoc} */
	@Override
	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException,
			MBeanException, ReflectionException {
		
		desc.setAttribute(getObject(), attribute);
		
	}

	/** {@inheritDoc} */
	@Override
	public AttributeList getAttributes(String[] attributes) {
        return desc.getAttributes(getObject(), attributes);
	}

	/** {@inheritDoc} */
	@Override
	public AttributeList setAttributes(AttributeList attributes) {
		return desc.setAttributes(getObject(),attributes);
	}

	/** {@inheritDoc} */
	@Override
	public Object invoke(String actionName, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {
		return desc.invoke(getObject(), actionName, params, signature);
	}

	/** {@inheritDoc} */
	@Override
	public MBeanInfo getMBeanInfo() {
		return desc.getMBeanInfo();
	}

	/**
	 * <p>Setter for the field <code>name</code>.</p>
	 *
	 * @param objectName a {@link javax.management.ObjectName} object.
	 */
	public void setName(ObjectName objectName) {
		name = objectName;
	}

	/**
	 * <p>check.</p>
	 */
	public void check() {
		try {
			getObject();
		} catch (Throwable t) {
			log().t(name,"closed");
		}
	}

}
