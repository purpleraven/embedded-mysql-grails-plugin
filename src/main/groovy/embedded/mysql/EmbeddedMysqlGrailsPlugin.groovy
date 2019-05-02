package embedded.mysql

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.ScriptResolver.classPathScript;
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
            def embeddedName = "embeddedPostgres_${entry.key}"
            if (entry.value.embeddedPostgres) {
                def mysqldDb = startEmbeddedMysql(entry.value, dataSourceName)
                "$embeddedName"(EmbeddedMysqlHolder, mysqldDb)
            }
        }
    } }

    private def startEmbeddedMysql(sourceConfig, dataSourceName){

        if(!sourceConfig.embeddedPort){
            sourceConfig.embeddedPort=SocketUtils.findAvailableTcpPort()
            log.debug("Embedded MySQL will use DEFAULT port: {}", sourceConfig.embeddedPort)
        }

        log.info("Embedded MySQL plugin is starting under ${dataSourceName} bean on ${sourceConfig.embeddedPort} port...")

        if(!sourceConfig.url) {
            sourceConfig.url="jdbc:mysql://localhost:${sourceConfig.embeddedPort}/mysql?autoReconnect=true&characterEncoding=UTF-8"
            log.debug("Embedded MySQL will use DEFAULT url: {}", sourceConfig.url)
        }

        if(!sourceConfig.username)
            sourceConfig.username='root'

        if(!sourceConfig.password)
            sourceConfig.password='root'


        DownloadConfig downloadConfig = aDownloadConfig()
                .withProxy(aHttpProxy("remote.host", 8080))
                .withCacheDir(System.getProperty("java.io.tmpdir"))
                .build();

        EmbeddedMysql mysqld = anEmbeddedMysql(v5_7_latest, downloadConfig)
                .addSchema("aschema", classPathScript("db/001_init.sql")) //todo create DB???
                .start(); // todo define port

//        if(sourceConfig.embeddedPort) {
//            builder.setPort(sourceConfig.embeddedPort.toInteger())
//        }
        mysqld
    }
}
