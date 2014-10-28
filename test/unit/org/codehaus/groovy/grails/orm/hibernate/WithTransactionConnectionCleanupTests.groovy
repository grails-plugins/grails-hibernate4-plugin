package org.codehaus.groovy.grails.orm.hibernate

import grails.persistence.Entity

import org.hibernate.Session
import org.springframework.orm.hibernate4.SessionFactoryUtils
import org.springframework.orm.hibernate4.SessionHolder
import org.springframework.transaction.support.TransactionSynchronizationManager

// test for GRAILS-8108
class WithTransactionConnectionCleanupTests extends AbstractGrailsHibernateTests {

	@Override
	protected getDomainClasses() {
		[AuthorLeak, BookLeak]
	}


	private void nestedWithNewSessionWithNewTransactionLoop(boolean unbindSessionHolder = false) {
		100.times { i ->
			def currentSession = session
			BookLeak.withNewSession { session ->
				assert currentSession != session
				Long authorId = AuthorLeak.withTransaction {
					AuthorLeak author = new AuthorLeak(firstName: 'John', lastName: "Doe ${i}")
					author.save()
					author.id
				}

//				AuthorLeak author = new AuthorLeak(firstName: 'John', lastName: "Doe ${i}")
//				author.save()
//				Long authorId = author.id

				AuthorLeak another = AuthorLeak.get(authorId)
				BookLeak book = new BookLeak(title: "The story of ${i}")
				another.addToBooks(book)
				another.save()
			}
			if (unbindSessionHolder) {
				TransactionSynchronizationManager.unbindResource(sessionFactory)
			}
		}
	}

	private void nestedSafeWithNewSessionWithNewTransactionLoop(boolean withException = false) {
		100.times { i ->
			def currentSession = session
			AuthorLeak.withNewSession { session ->
				assert currentSession != session
				Long authorId = AuthorLeak.withTransaction {
					AuthorLeak author = new AuthorLeak(firstName: 'John', lastName: "Doe ${i}")
					author.save()
					author.id
				}
				AuthorLeak another = AuthorLeak.get(authorId)
				BookLeak book = new BookLeak(title: "The story of ${i}")
				another.addToBooks(book)
				if (withException) {
					throw new RuntimeException("Faking error (${i})")
				}
				another.save()
			}
		}
	}

	private void closeSession() {
		SessionHolder sessionHolder = TransactionSynchronizationManager.getResource(sessionFactory)
		if (sessionHolder) {
			TransactionSynchronizationManager.unbindResource(sessionFactory)
			SessionFactoryUtils.closeSession(sessionHolder.getSession())
		}
	}

	void testDbConnectionLeak() {
		nestedWithNewSessionWithNewTransactionLoop()
	}

	void testDbConnectionLeakSessionClosed() {
		closeSession()
		nestedWithNewSessionWithNewTransactionLoop()
	}

	void testNestedCustomWithNewSessionWithNewTransaction() {
		// Will succeed because using custom safeWithNewSession
		nestedSafeWithNewSessionWithNewTransactionLoop()
	}

	void testNestedCustomWithNewSessionWithNewTransactionSessionClosed() {
		// Will succeed because using custom safeWithNewSession
		// Must be run by itself because once the main session is closed, the other tests don't run correctly
		closeSession()
		nestedSafeWithNewSessionWithNewTransactionLoop()
	}

	void testExceptionInNestedCustomWithNewSessionWithNewTransaction() {
		shouldFail {
			nestedSafeWithNewSessionWithNewTransactionLoop(true)
		}
	}
}

@Entity
class BookLeak {
	String title
	static belongsTo = [author: AuthorLeak]
}

@Entity
class AuthorLeak {
	String firstName
	String lastName
	static hasMany = [books: BookLeak]
}
