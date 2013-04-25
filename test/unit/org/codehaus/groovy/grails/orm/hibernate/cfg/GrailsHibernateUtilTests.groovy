package org.codehaus.groovy.grails.orm.hibernate.cfg

import static org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsHibernateUtil.ARGUMENT_FETCH_SIZE
import static org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsHibernateUtil.ARGUMENT_TIMEOUT
import static org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsHibernateUtil.ARGUMENT_READ_ONLY

import org.hibernate.Criteria
import org.hibernate.FlushMode

class GrailsHibernateUtilTests extends GroovyTestCase {

	void testPopulateArgumentsForCriteria_fetchSize() {
		assertMockedCriteriaCalledFor("setFetchSize", ARGUMENT_FETCH_SIZE, 10)
	}

	void testPopulateArgumentsForCriteria_timeout() {
		assertMockedCriteriaCalledFor("setTimeout", ARGUMENT_TIMEOUT, 60)
	}

	void testPopulateArgumentsForCriteria_readOnly() {
		assertMockedCriteriaCalledFor("setReadOnly", ARGUMENT_READ_ONLY, true)
	}

	// works for criteria methods with primitive arguments
	protected assertMockedCriteriaCalledFor(String methodName, String keyName, value) {
		Boolean methodCalled = false

		Criteria criteria = [
				(methodName): { passedValue ->
					assertEquals value, passedValue
					methodCalled = true
					return
				}
		] as Criteria

		GrailsHibernateUtil.populateArgumentsForCriteria(null, null, criteria, [(keyName): value])
		assertTrue methodCalled
	}

	void testPopulateArgumentsForCriteria_flushMode() {
		Boolean methodCalled = false
		FlushMode value = FlushMode.MANUAL

		Criteria criteria = [
				setFlushMode: { FlushMode passedValue ->
					assertEquals value, passedValue
					methodCalled = true
					return
				}
		] as Criteria

		GrailsHibernateUtil.populateArgumentsForCriteria(null, null, criteria, [flushMode: value])
		assertTrue methodCalled
	}
}
