/* Copyright 2004-2005 Graeme Rocher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.grails.commons

import junit.framework.TestCase

import org.codehaus.groovy.grails.orm.hibernate.cfg.DefaultGrailsDomainConfiguration
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsDomainBinder
import org.codehaus.groovy.grails.orm.hibernate.validation.HibernateConstraintsEvaluator
import org.codehaus.groovy.grails.plugins.GrailsPlugin
import org.codehaus.groovy.grails.plugins.MockGrailsPluginManager
import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.codehaus.groovy.grails.validation.NullableConstraint
import org.hibernate.cfg.ImprovedNamingStrategy

/**
 * Tests for the GrailsDomainConfigurationUtil class.
 *
 * @author Graeme Rocher
 * @since 0.5
 */
class GrailsDomainConfigurationUtilTests extends TestCase {

	@Override
	protected void setUp() {
		super.setUp()
		ExpandoMetaClass.enableGlobally()
		MockGrailsPluginManager pluginManager = new MockGrailsPluginManager()
		PluginManagerHolder.setPluginManager(pluginManager)
		pluginManager.registerMockPlugin([getName: { -> 'hibernate4' }] as GrailsPlugin)
	}

	@Override
	protected void tearDown() {
		super.tearDown()
		GrailsDomainBinder grailsDomainBinder = new GrailsDomainBinder()
		grailsDomainBinder.NAMING_STRATEGIES.clear()
		grailsDomainBinder.NAMING_STRATEGIES.put(
			GrailsDomainClassProperty.DEFAULT_DATA_SOURCE, ImprovedNamingStrategy.INSTANCE)
		PluginManagerHolder.setPluginManager(null)
	}

	void testIsNotConfigurational() {
		assertTrue(GrailsDomainConfigurationUtil.isNotConfigurational("foo"))
		assertFalse(GrailsDomainConfigurationUtil.isNotConfigurational("belongsTo"))
	}

	void testIsBasicType() {
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(boolean))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(long))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(int))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(short))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(char))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(double))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(float))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(byte))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(Boolean))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(Long))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(Integer))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(Short))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(Character))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(Double))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(Float))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(Byte))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(Date))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(URL))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(URI))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(boolean[]))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(long[]))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(int[]))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(short[]))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(char[]))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(double[]))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(float[]))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(byte[]))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(Boolean[]))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(Long[]))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(Integer[]))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(Short[]))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(Character[]))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(Double[]))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(Float[]))
		assertTrue(GrailsDomainConfigurationUtil.isBasicType(Byte[]))
	}

	void testEvaluateConstraintsInsertableShouldBeNullableByDefault() {
		GroovyClassLoader cl = new GroovyClassLoader()
		GrailsDomainClass domainClass = new DefaultGrailsDomainClass(
			cl.parseClass("""
class TestInsertableUpdateableDomain {
	Long id
	Long version
	String testString1
	String testString2

	static mapping = {
		testString1 insertable:false
		testString2 max:50
	}
}"""))

		getDomainConfig(cl, [domainClass.getClazz()] as Class[])
		Map<String, ConstrainedProperty> mapping = new HibernateConstraintsEvaluator().evaluate(domainClass.getClazz(), domainClass.getProperties())
		ConstrainedProperty property1 = mapping.get("testString1")
		assertTrue("constraint was not nullable and should be", ((NullableConstraint)property1.getAppliedConstraint(ConstrainedProperty.NULLABLE_CONSTRAINT)).isNullable())
		ConstrainedProperty property2 = mapping.get("testString2")
		assertFalse("constraint was nullable and shouldn't be", ((NullableConstraint)property2.getAppliedConstraint(ConstrainedProperty.NULLABLE_CONSTRAINT)).isNullable())
	}

	@SuppressWarnings("rawtypes")
	private DefaultGrailsDomainConfiguration getDomainConfig(GroovyClassLoader cl, Class[] classes) {
		GrailsApplication grailsApplication = new DefaultGrailsApplication(classes, cl)
		grailsApplication.initialise()
		DefaultGrailsDomainConfiguration config = new DefaultGrailsDomainConfiguration(grailsApplication: grailsApplication)
		config.buildMappings()
		config
	}
}
