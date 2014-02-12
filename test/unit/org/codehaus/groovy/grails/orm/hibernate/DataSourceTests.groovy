package org.codehaus.groovy.grails.orm.hibernate

import javax.sql.PooledConnection
import org.hibernate.engine.jdbc.spi.JdbcWrapper
import org.springframework.jdbc.datasource.ConnectionProxy

class DataSourceTests extends AbstractGrailsHibernateTests {

	protected void onSetUp() {
		gcl.parseClass '''
import grails.persistence.Entity
@Entity
class Flanglurb {}
'''
	}

	void testConnectionTypes() {
		def dc = ga.getDomainClass('Flanglurb').clazz
		dc.withTransaction { s ->
			def dataSource = appCtx.dataSource
			def dataSourceUnproxied = appCtx.dataSourceUnproxied

			def sessionFactoryConnection = sessionFactory.currentSession.connection()

			assertTrue sessionFactoryConnection instanceof ConnectionProxy
			sessionFactoryConnection = sessionFactoryConnection.getTargetConnection()

			assertTrue sessionFactoryConnection instanceof PooledConnection
			sessionFactoryConnection = sessionFactoryConnection.connection

			def dataSourceConnection = dataSource.connection

			assertTrue dataSourceConnection instanceof ConnectionProxy
			dataSourceConnection = dataSourceConnection.getTargetConnection()

			assertTrue dataSourceConnection instanceof ConnectionProxy
			dataSourceConnection = dataSourceConnection.getTargetConnection()

			assertTrue dataSourceConnection instanceof PooledConnection
			dataSourceConnection = dataSourceConnection.connection

			def unproxiedConnection = dataSourceUnproxied.connection
			assertFalse unproxiedConnection instanceof ConnectionProxy
			assertFalse unproxiedConnection instanceof JdbcWrapper
			assertTrue unproxiedConnection instanceof PooledConnection
			unproxiedConnection = dataSourceUnproxied.connection

			assertTrue sessionFactoryConnection.is(dataSourceConnection)
			assertFalse unproxiedConnection.is(dataSourceConnection)
		}
	}
}
