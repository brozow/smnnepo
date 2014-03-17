package org.opennms.netmgt.api.sample;

import org.apache.camel.Produce;

public class DefaultSampler implements Sampler {

	@Produce(property="endpointUri")
	Sampler proxy;

	private String m_endpointUri;

	public String getEndpointUri() {
		return m_endpointUri;
	}

	public void setEndpointUri(String endpointUri) {
		this.m_endpointUri = endpointUri;
	}

	/**
	 * Send the incoming {@link Agent} message into the Camel route
	 * specified by the {@link #m_endpointUri} property.
	 */
	@Override
	public void collect(Agent agent) {
		proxy.collect(agent);
	}
}
