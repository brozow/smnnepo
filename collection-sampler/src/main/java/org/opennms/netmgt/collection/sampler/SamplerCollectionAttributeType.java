/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2010-2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.collection.sampler;

import org.opennms.netmgt.api.sample.Metric;
import org.opennms.netmgt.collection.api.AttributeGroupType;
import org.opennms.netmgt.collection.api.CollectionAttribute;
import org.opennms.netmgt.collection.api.Persister;
import org.opennms.netmgt.collection.support.AbstractCollectionAttributeType;

public class SamplerCollectionAttributeType extends AbstractCollectionAttributeType {

	private final Metric m_metric;

	public SamplerCollectionAttributeType(AttributeGroupType groupType, Metric sample) {
		super(groupType);
		if (sample == null) {
			throw new IllegalArgumentException("Sample cannot be null");
		}
		m_metric = sample;
	}

	@Override
	public void storeAttribute(CollectionAttribute attribute, Persister persister) {
		// TODO Figure out what to do here. Does the sampler API handle string attributes?
		//persister.persistStringAttribute(attribute);
		persister.persistNumericAttribute(attribute);
	}

	@Override
	public String getName() {
		return m_metric.getName();
	}

	@Override
	public String getType() {
		return m_metric.getType().toString();
	}

}
