package org.codehaus.groovy.grails.orm.hibernate

import grails.persistence.Entity

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.orm.hibernate4.HibernateSystemException

/**
 * @author Graeme Rocher
 * @since 1.1
 */
class NaturalIdentifierTests extends AbstractGrailsHibernateTests {

	protected getDomainClasses() {
		[NaturalAuthor, NaturalBook, NaturalBook2]
	}

	void testNaturalIdentifier() {
		def a = new NaturalAuthor(name:"Stephen King").save(flush:true)
		def b = new NaturalBook(author:a, title:"The Stand").save(flush:true)
		assertNotNull b

		b.title = "Changed"

		// should fail with an attempt to alter an immutable natural identifier
		shouldFail(HibernateSystemException) {
			b.save(flush:true)
		}

		// should fail with a unique constraint violation exception
		shouldFail(DataIntegrityViolationException) {
			new NaturalBook(author:a, title:"The Stand").save(flush:true)
		}
	}

	void testMutableNaturalIdentifier() {
		def a = new NaturalAuthor(name:"Stephen King").save(flush:true)
		def b = new NaturalBook2(author:a, title:"The Stand").save(flush:true)
		assertNotNull b

		b.title = "Changed"
		// mutable identifier so no problem
		b.save(flush:true)

		// should fail with a unique constraint violation exception
		shouldFail(DataIntegrityViolationException) {
			new NaturalBook2(author:a, title:"Changed").save(flush:true)
		}
	}
}

@Entity
class NaturalAuthor {
	String name
}

@Entity
class NaturalBook {
	String title
	NaturalAuthor author

	static mapping = {
		id natural:['title', 'author']
	}
}

@Entity
class NaturalBook2 {
	String title
	NaturalAuthor author

	static mapping = {
		id natural:[properties:['title', 'author'], mutable:true]
	}
}
