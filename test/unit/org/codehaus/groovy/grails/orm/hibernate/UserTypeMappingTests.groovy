package org.codehaus.groovy.grails.orm.hibernate

import javax.sql.DataSource

/**
* @author Graeme Rocher
*/
class UserTypeMappingTests extends AbstractGrailsHibernateTests {

	protected void onSetUp() {
		gcl.parseClass '''
import org.hibernate.type.YesNoType

class UserTypeMappingTest {
	Long id
	Long version

	Boolean active

	static mapping = {
		table 'type_test'
		active (column: 'active', type: YesNoType)
	}
}
'''

		gcl.parseClass '''
import org.codehaus.groovy.grails.orm.hibernate.Weight
import org.codehaus.groovy.grails.orm.hibernate.WeightUserType

class UserTypeMappingTestsPerson {
	Long id
	Long version
	String name
	Weight weight

	static constraints = {
		name(unique: true)
		weight(nullable: true)
	}

	static mapping = {
		weight(type:WeightUserType)
	}
}
'''
	}

	void testCustomUserType() {
		def personClass = ga.getDomainClass("UserTypeMappingTestsPerson").clazz

		def person = personClass.newInstance(name:"Fred", weight: new Weight(200))

		person.save(flush:true)
		session.clear()

		person = personClass.get(1)

		assertNotNull person
		assertEquals 200, person.weight.pounds
	}

	void testUserTypeMapping() {

		def clz = ga.getDomainClass("UserTypeMappingTest").clazz

		assertNotNull clz.newInstance(active:true).save(flush:true)

		DataSource ds = (DataSource)applicationContext.getBean('dataSource')

		def con
		try {
			con = ds.getConnection()
			def statement = con.prepareStatement("select * from type_test")
			def result = statement.executeQuery()
			assertTrue result.next()
			def value = result.getString('active')

			assertEquals "Y", value
		}
		finally {
			con.close()
		}
	}

	void testUserTypePropertyMetadata() {
		def personDomainClass = ga.getDomainClass("UserTypeMappingTestsPerson")
		def personClass = personDomainClass.clazz

		def person = personClass.newInstance(name:"Fred", weight: new Weight(200))

		// the metaClass should report the correct type, not Object
		assertEquals Weight, personClass.metaClass.hasProperty(person, "weight").type

		// GrailsDomainClassProperty should not appear to be an association
		def prop = personDomainClass.getPropertyByName("weight")
		assertFalse prop.isAssociation()
		assertFalse prop.isOneToOne()
		assertEquals Weight, prop.type
	}
}
