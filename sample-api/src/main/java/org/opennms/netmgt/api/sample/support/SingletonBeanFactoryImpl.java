/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2014 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.api.sample.support;

import java.util.concurrent.atomic.AtomicReference;

public class SingletonBeanFactoryImpl<T> implements SingletonBeanFactory<T> {

    private AtomicReference<T> m_instance = new AtomicReference<T>();

    public SingletonBeanFactoryImpl() {
        m_instance.set(null);
    }

    public SingletonBeanFactoryImpl(T instance) {
        m_instance.set(instance);
    }

    public T getInstance() {
        return m_instance == null ? null : m_instance.get();
    }

    public void setInstance(T instance) {
        m_instance.set(instance);
    }

    @Override
    public String toString() {
        return "SingletonBeanFactory[ instance=" + (getInstance() == null? null : getInstance().getClass().getName()) + " ]";
    }
}
