package org.opennms.netmgt.api.sample.support;

import java.lang.reflect.Method;

import org.apache.camel.Consume;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will redirect messages to the {@link DispatcherWhiteboard#m_endpointUri} URI
 * to any OSGi services that are registered at the interface that is defined by the
 * {@link #setMessageClass(String)} method call.
 */
public class DispatcherWhiteboard {
	
	private static final Logger LOG = LoggerFactory.getLogger(DispatcherWhiteboard.class);

	private BundleContext m_context;
	private Class<?> m_messageClass;
	private Class<?> m_serviceClass;
	private String m_methodName = "dispatch";
	private final String m_endpointUri;

	@SuppressWarnings("rawtypes")
        private ServiceTracker m_tracker = null;
	private Method m_method = null;

	public DispatcherWhiteboard(String endpointUri) {
		m_endpointUri = endpointUri;
	}

	public String getEndpointUri() {
		return m_endpointUri;
	}

	public void setContext(BundleContext context) {
		this.m_context = context;
	}

	public Class<?> getMessageClass() {
		return m_messageClass;
	}

	public void setMessageClass(Class<?> messageClass) {
		this.m_messageClass = messageClass;
	}

	public void setMessageClass(String messageClass) throws ClassNotFoundException {
		this.m_messageClass = Class.forName(messageClass);
	}

	public Class<?> getServiceClass() {
		return m_serviceClass;
	}

	public void setServiceClass(Class<?> serviceClass) {
		this.m_serviceClass = serviceClass;
	}

	public void setServiceClass(String serviceClass) throws ClassNotFoundException {
		this.m_serviceClass = Class.forName(serviceClass);
	}

	public String getMethodName() {
		return m_methodName;
	}

	public void setMethodName(String methodName) {
		this.m_methodName = methodName;
	}

	public void destroy() {
		if (m_tracker != null) {
			m_tracker.close();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
        @Consume(property="endpointUri")
	public void dispatch(Object message) throws NoSuchMethodException, SecurityException {
		if (m_tracker == null) {
			m_tracker = new ServiceTracker(m_context, m_serviceClass, null);
			m_tracker.open();
		}

		if (m_method == null) {
			m_method = m_serviceClass.getMethod(m_methodName, m_messageClass);
		}

		try {
			Object[] services = m_tracker.getServices();
			if (services != null && services.length > 0) {
				for (Object service : m_tracker.getServices()) {
					m_method.invoke(service, message);
				}
			}
		} catch (Throwable e) {
			// If anything goes wrong, log an error message
			// TODO: Use a dead-letter channel?
			LOG.warn("Message dispatch failed: " + e.getMessage(), e);
		}
	}
}
