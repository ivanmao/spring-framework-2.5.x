/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.beans.factory.config;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.springframework.beans.TestBean;
import org.springframework.beans.factory.parsing.CollectingReaderEventListener;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Rob Harrop
 * @author Juergen Hoeller
 */
public class UtilNamespaceHandlerTests extends TestCase {

	private DefaultListableBeanFactory beanFactory;

	private CollectingReaderEventListener listener = new CollectingReaderEventListener();

	public void setUp() {
		this.beanFactory = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this.beanFactory);
		reader.setEventListener(this.listener);
		reader.loadBeanDefinitions(new ClassPathResource("testUtilNamespace.xml", getClass()));
		assertEquals(18, this.beanFactory.getBeanDefinitionCount());
	}

	public void testLoadProperties() throws Exception {
		Properties props = (Properties) this.beanFactory.getBean("myProperties");
		assertEquals("Incorrect property value", "bar", props.get("foo"));
	}

	public void testConstant() throws Exception {
		Integer min = (Integer) this.beanFactory.getBean("min");
		assertEquals(Integer.MIN_VALUE, min.intValue());
	}

	public void testConstantWithDefaultName() throws Exception {
		Integer max = (Integer) this.beanFactory.getBean("java.lang.Integer.MAX_VALUE");
		assertEquals(Integer.MAX_VALUE, max.intValue());
	}

	public void testEvents() throws Exception {
		ComponentDefinition propertiesComponent = this.listener.getComponentDefinition("myProperties");
		assertNotNull("Event for 'myProperties' not sent", propertiesComponent);
		AbstractBeanDefinition propertiesBean = (AbstractBeanDefinition) propertiesComponent.getBeanDefinitions()[0];
		assertEquals("Incorrect BeanDefinition", PropertiesFactoryBean.class, propertiesBean.getBeanClass());

		ComponentDefinition constantComponent = this.listener.getComponentDefinition("min");
		assertNotNull("Event for 'min' not sent", propertiesComponent);
		AbstractBeanDefinition constantBean = (AbstractBeanDefinition) constantComponent.getBeanDefinitions()[0];
		assertEquals("Incorrect BeanDefinition", FieldRetrievingFactoryBean.class, constantBean.getBeanClass());
	}

	public void testNestedProperties() throws Exception {
		TestBean bean = (TestBean) this.beanFactory.getBean("testBean");
		Properties props = bean.getSomeProperties();
		assertEquals("Incorrect property value", "bar", props.get("foo"));
	}

	public void testPropertyPath() throws Exception {
		String name = (String) this.beanFactory.getBean("name");
		assertEquals("Rob Harrop", name);
	}

	public void testNestedPropertyPath() throws Exception {
		TestBean bean = (TestBean) this.beanFactory.getBean("testBean");
		assertEquals("Rob Harrop", bean.getName());
	}

	public void testSimpleMap() throws Exception {
		Map map = (Map) this.beanFactory.getBean("simpleMap");
		assertEquals("bar", map.get("foo"));
	}

	public void testSimpleList() throws Exception {
		List list = (List) this.beanFactory.getBean("simpleList");
		assertEquals("Rob Harrop", list.get(0));
	}

	public void testSimpleSet() throws Exception {
		Set list = (Set) this.beanFactory.getBean("simpleSet");
		assertTrue(list.contains("Rob Harrop"));
	}

	public void testMapWithRef() throws Exception {
		Map map = (Map) this.beanFactory.getBean("mapWithRef");
		assertTrue(map instanceof TreeMap);
		assertEquals(this.beanFactory.getBean("testBean"), map.get("bean"));
	}

	public void testNestedCollections() throws Exception {
		TestBean bean = (TestBean) this.beanFactory.getBean("nestedCollectionsBean");

		List list = bean.getSomeList();
		assertEquals(1, list.size());
		assertEquals("foo", list.get(0));

		Set set = bean.getSomeSet();
		assertEquals(1, set.size());
		assertTrue(set.contains("foo"));

		Map map = bean.getSomeMap();
		assertEquals(1, map.size());
		assertEquals("bar", map.get("foo"));
	}

	public void testNestedInCollections() throws Exception {
		TestBean bean = (TestBean) this.beanFactory.getBean("nestedCustomTagBean");
		Integer min = new Integer(Integer.MIN_VALUE);

		List list = bean.getSomeList();
		assertEquals(1, list.size());
		assertEquals(min, list.get(0));

		Set set = bean.getSomeSet();
		assertEquals(1, set.size());
		assertTrue(set.contains(min));

		Map map = bean.getSomeMap();
		assertEquals(1, map.size());
		assertEquals(min, map.get("min"));
	}

	public void testCircularCollections() throws Exception {
		TestBean bean = (TestBean) this.beanFactory.getBean("circularCollectionsBean");

		List list = bean.getSomeList();
		assertEquals(1, list.size());
		assertEquals(bean, list.get(0));

		Set set = bean.getSomeSet();
		assertEquals(1, set.size());
		assertTrue(set.contains(bean));

		Map map = bean.getSomeMap();
		assertEquals(1, map.size());
		assertEquals(bean, map.get("foo"));
	}

	public void testCircularCollectionBeansStartingWithList() throws Exception {
		this.beanFactory.getBean("circularList");
		TestBean bean = (TestBean) this.beanFactory.getBean("circularCollectionBeansBean");

		List list = bean.getSomeList();
		assertTrue(Proxy.isProxyClass(list.getClass()));
		assertEquals(1, list.size());
		assertEquals(bean, list.get(0));

		Set set = bean.getSomeSet();
		assertFalse(Proxy.isProxyClass(set.getClass()));
		assertEquals(1, set.size());
		assertTrue(set.contains(bean));

		Map map = bean.getSomeMap();
		assertFalse(Proxy.isProxyClass(map.getClass()));
		assertEquals(1, map.size());
		assertEquals(bean, map.get("foo"));
	}

	public void testCircularCollectionBeansStartingWithSet() throws Exception {
		this.beanFactory.getBean("circularSet");
		TestBean bean = (TestBean) this.beanFactory.getBean("circularCollectionBeansBean");

		List list = bean.getSomeList();
		assertFalse(Proxy.isProxyClass(list.getClass()));
		assertEquals(1, list.size());
		assertEquals(bean, list.get(0));

		Set set = bean.getSomeSet();
		assertTrue(Proxy.isProxyClass(set.getClass()));
		assertEquals(1, set.size());
		assertTrue(set.contains(bean));

		Map map = bean.getSomeMap();
		assertFalse(Proxy.isProxyClass(map.getClass()));
		assertEquals(1, map.size());
		assertEquals(bean, map.get("foo"));
	}

	public void testCircularCollectionBeansStartingWithMap() throws Exception {
		this.beanFactory.getBean("circularMap");
		TestBean bean = (TestBean) this.beanFactory.getBean("circularCollectionBeansBean");

		List list = bean.getSomeList();
		assertFalse(Proxy.isProxyClass(list.getClass()));
		assertEquals(1, list.size());
		assertEquals(bean, list.get(0));

		Set set = bean.getSomeSet();
		assertFalse(Proxy.isProxyClass(set.getClass()));
		assertEquals(1, set.size());
		assertTrue(set.contains(bean));

		Map map = bean.getSomeMap();
		assertTrue(Proxy.isProxyClass(map.getClass()));
		assertEquals(1, map.size());
		assertEquals(bean, map.get("foo"));
	}

	public void testNestedInConstructor() throws Exception {
		TestBean bean = (TestBean) this.beanFactory.getBean("constructedTestBean");
		assertEquals("Rob Harrop", bean.getName());
	}

}