package embedded.mysql

import groovy.sql.Sql
import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

class EnabledEmbeddedMysqlSpec extends Specification implements GrailsUnitTest {

    Closure doWithConfig() {{ config ->
        config.dataSource.with {
            embeddedMysqld=true
            embeddedPort=56566
            url='jdbc:mysql://localhost:56566/embedded_db'
            username = 'embedded_db'
            password = 'embedded_db'
        }
    }}

    Set<String> getIncludePlugins() {
        ["dataSource",'embeddedMysql'].toSet()
    }

    def "Embedded Mysql with custom options"() {
        when:
        def dataSource = applicationContext.getBean('dataSource')
        Sql sql = new Sql(dataSource)
        String version = sql.rows('SHOW VARIABLES LIKE "%version_comment%"').first().getProperty('Value')

        then:
        version.toLowerCase().contains('mysql')
    }
}
