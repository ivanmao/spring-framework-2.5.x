/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;

/**
 * Base implementation of {@link ComponentDefinition} that provides a basic implementation of
 * {@link #getDescription} which delegates to {@link #getName}. Also provides a base implementation
 * of {@link #toString} which delegates to {@link #getDescription} in keeping with the recommended
 * implementation strategy. Also provides default implementations of {@link #getInnerBeanDefinitions}
 * and {@link #getBeanReferences} that return an empty array.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public abstract class AbstractComponentDefinition implements ComponentDefinition {

	/**
	 * Delegates to {@link #getName}.
	 */
	public String getDescription() {
		return getName();
	}

	/**
	 * Returns an empty array.
	 */
	public BeanDefinitionHolder[] getInnerBeanDefinitions() {
		return new BeanDefinitionHolder[0];
	}

	/**
	 * Returns an empty array.
	 */
	public RuntimeBeanReference[] getBeanReferences() {
		return new RuntimeBeanReference[0];
	}

	/**
	 * Delegates to {@link #getDescription}.
	 */
	public String toString() {
		return getDescription();
	}

}
