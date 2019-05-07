package embedded.mysql

import embedded.mysql.utils.DriverUtils
import groovy.sql.Sql
import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

class EnabledEmbeddedMysqlSpec extends Specification implements GrailsUnitTest {

    Closure doWithConfig() {{ config ->
        config.dataSource.with {
            embeddedPostgres=true
            embeddedPort=56566
            url='jdbc:mysql://localhost:56566/mysql'
            username='root'
            password='root'
        }
    }}

    Set<String> getIncludePlugins() {
        ["dataSource",'embeddedMysql'].toSet()
    }

//    def setup() {
//        DriverUtils.refreshPostgres()
//    }

    def "Embedded Postgres with custom options"() {
        when:
        def dataSource = applicationContext.getBean('dataSource')
        Sql sql = new Sql(dataSource)
        String version = sql.rows('SHOW VARIABLES LIKE "%version_comment%"').first().getProperty('Value')

        then:
        version.contains('MySql')
    }
}
