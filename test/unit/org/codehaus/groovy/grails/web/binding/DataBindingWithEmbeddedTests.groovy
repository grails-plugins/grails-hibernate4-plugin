package org.codehaus.groovy.grails.web.binding

import org.codehaus.groovy.grails.orm.hibernate.AbstractGrailsHibernateTests
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest

/**
 * @author Rob Fletcher
 * @since 1.3.3
 */
class DataBindingWithEmbeddedTests extends AbstractGrailsHibernateTests {

	protected void onSetUp() {
		gcl.parseClass("""
package databindingwithembeddedtests

import grails.persistence.*

@Entity
class Reader {
	String name
	Book currentlyReading
}

@Entity
class Book {
	Author author
	String title
	static embedded = ["author"]
}

class Author {
	String name
	static constraints = {
		name blank: false
	}
}
""")
	}

	void testDataBindingWithEmbeddedProperty() {
		def Book = ga.getDomainClass("databindingwithembeddedtests.Book").clazz

		def request = new MockHttpServletRequest()
		request.addParameter("title", "Pattern Recognition")
		request.addParameter("author.name", "William Gibson")
		def params = new GrailsParameterMap(request)

		def book = Book.newInstance()

		assertNull "Embedded property before binding", book.author

		book.properties = params

		assertNotNull "Embedded property after binding", book.author
		assertEquals "Embedded property after binding", "William Gibson", book.author.name
	}

	void testDataBindingWithEmbeddedPropertyOfAssociation() {
		def Reader = ga.getDomainClass("databindingwithembeddedtests.Reader").clazz

		def request = new MockHttpServletRequest()
		request.addParameter("currentlyReading.title", "Pattern Recognition")
		request.addParameter("currentlyReading.author.name", "William Gibson")
		def params = new GrailsParameterMap(request)

		def reader = Reader.newInstance()
		reader.properties = params

		assertEquals "Regular association property", "Pattern Recognition", reader.currentlyReading.title
		assertNotNull "Embedded association property", reader.currentlyReading.author
		assertEquals "Embedded association property", "William Gibson", reader.currentlyReading.author.name
	}
}
