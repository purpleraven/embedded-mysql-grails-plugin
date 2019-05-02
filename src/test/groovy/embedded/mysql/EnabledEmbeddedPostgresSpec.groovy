package embedded.mysql

import embedded.postgres.utils.DriverUtils
import groovy.sql.Sql
import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

class EnabledEmbeddedPostgresSpec extends Specification implements GrailsUnitTest {

    Closure doWithConfig() {{ config ->
        config.dataSource.with {
            embeddedPostgres=true
            embeddedPort=56566
            url='jdbc:postgresql://localhost:56566/mysql'
            username='mysql'
            password='mysql'
        }
    }}

    Set<String> getIncludePlugins() {
        ["dataSource",'embeddedPostgres'].toSet()
    }

    def setup() {
        DriverUtils.refreshPostgres()
    }

    def "Embedded Postgres with custom options"() {
        when:
        def dataSource = applicationContext.getBean('dataSource')
        Sql sql = new Sql(dataSource)
        String version = sql.rows('SELECT version() as version').first().getProperty('version')

        then:
        version.startsWith('PostgreSQL')
    }
}
