package embedded.mysql

import com.wix.mysql.config.MysqldConfig
import static com.wix.mysql.config.Charset.UTF8;

import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.distribution.Version.v5_7_latest;
import static com.wix.mysql.config.DownloadConfig.aDownloadConfig;
import static com.wix.mysql.config.ProxyFactory.aHttpProxy;

import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.config.DownloadConfig
import grails.plugins.Plugin
import groovy.util.logging.Slf4j
import org.springframework.util.SocketUtils

@Slf4j
class EmbeddedMysqlGrailsPlugin extends Plugin {
    def grailsVersion   = "3.2.10 > *"
    def title           = "Embedded Mysql"
    def author          = "purpleraven"
    def authorEmail     = "purpleraven@gmail.com"
    def description     = 'Plugin starts local instance of mysql and after program termination clears temporary data. Simulates embedded mysql.'
    def documentation   = "https://github.com/purpleraven/embedded-mysql-grails-plugin"
    def license         = "APACHE"
    def organization    = [name: "purpleraven", url: "https://github.com/purpleraven/embedded-mysql-grails-plugin"]
    def issueManagement = [ system: "GITHUB", url: "https://github.com/purpleraven/embedded-mysql-grails-plugin/issues" ]
    def scm             = [ url: "https://github.com/purpleraven/embedded-mysql-grails-plugin" ]
    def pluginExcludes  = []
    def developers      = [ [name: 'purpleraven'] ]
    def dependsOn = [dataSource: grailsVersion]
    def loadBefore = ['dataSource']
    def scopes = [excludes:'war']

    Closure doWithSpring() { {->
        def config = grailsApplication.config
        if (config.dataSource.embeddedMysqld) {
            def dataSourceName = "dataSource"
            def mysqldDb = startEmbeddedMysql(config.dataSource, dataSourceName)
            embeddedMysqld(EmbeddedMysqlHolder,mysqldDb)
        }
        for(def entry: config.dataSources) {
            def dataSourceName = "dataSource_${entry.key}"
            def embeddedName = "embeddedMysql_${entry.key}"
            if (entry.value.embeddedMysqld) {
                def mysqldDb = startEmbeddedMysql(entry.value, dataSourceName)
                "$embeddedName"(EmbeddedMysqlHolder, mysqldDb)
            }
        }
    } }

    private def startEmbeddedMysql(sourceConfig, dataSourceName){

        def port = sourceConfig.embeddedPort
        
        if (port){
            port = (int) port.toInteger()
        }
        
        if(!port){
            port = SocketUtils.findAvailableTcpPort()
        }
        
        def username = sourceConfig.username?: 'root'
        def password = sourceConfig.password?: 'root'
        def schema = sourceConfig.password?: 'mysql'

        log.info("Embedded MySQL plugin is starting under ${dataSourceName} bean on ${port} port...")

        if(!sourceConfig.url) {
            sourceConfig.url="jdbc:mysql://localhost:${port}/${schema}?autoReconnect=true&characterEncoding=UTF-8"
            log.debug("Embedded MySQL will use DEFAULT url: {}", sourceConfig.url)
        }


        DownloadConfig downloadConfig = aDownloadConfig()
                .withProxy(aHttpProxy("remote.host", 8080))
                .withCacheDir(System.getProperty("java.io.tmpdir"))
                .build()

        def builder = aMysqldConfig(v5_7_latest)
                .withCharset(UTF8)
                .withUser(username, password)
//                .withTimeZone("Europe/Vilnius")
//                .withTimeout(2, TimeUnit.MINUTES)
                .withServerVariable("max_connect_errors", 666)
                .withPort(port)


        MysqldConfig config = builder.build()


        EmbeddedMysql mysqld = anEmbeddedMysql(config, downloadConfig)
                .addSchema(schema)
                .start()

        mysqld
    }
}
