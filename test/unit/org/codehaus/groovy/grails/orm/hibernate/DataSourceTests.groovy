package org.codehaus.groovy.grails.orm.hibernate

import java.sql.Connection

import javax.sql.DataSource

import org.springframework.jdbc.datasource.ConnectionProxy
import org.springframework.jdbc.datasource.DataSourceUtils

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
            DataSource dataSource = appCtx.dataSource
            DataSource dataSourceUnproxied = appCtx.dataSourceUnproxied

            Connection dataSourceConnection = unwrapAndCheckConnection(dataSource.connection)

            Connection sessionFactoryConnection = unwrapAndCheckConnection(sessionFactory.currentSession.connection())

            Connection unproxiedConnection = unwrapAndCheckConnection(dataSourceUnproxied.connection)

            assertTrue sessionFactoryConnection.is(dataSourceConnection)
            assertFalse unproxiedConnection.is(dataSourceConnection)
        }
    }

    Connection unwrapAndCheckConnection(Connection conn) {
        conn = DataSourceUtils.getTargetConnection(conn)
        assert !(conn instanceof ConnectionProxy)
        return conn
    }
}
