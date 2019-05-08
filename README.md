# embedded-mysql-grails-plugin 
[ ![Download](https://api.bintray.com/packages/purpleraven/plugins/embedded-mysql/images/download.svg?version=1.1) ](https://bintray.com/purpleraven/plugins/embedded-mysql/1.1/link)

Plugin replaces default embedded H2 datasource in awesome [Grails](http://grails.org) framework 
to MySQL. Plugin uses already existing solution [wix-embedded-mysql](https://github.com/wix/wix-embedded-mysql). 

The plugin based on [embedded-postgres-grails-plugin](https://github.com/Relaximus/embedded-postgres-grails-plugin) 

This plugin is not for production use, the main idea to have
your development and u-test environment similar to the standalone production MySQl (of course, if you use this DB in the project). 

## Getting Started

To get started with the minimum of configuration start from the following:
Add to your ***build.gradle***
```groovy
repositories {
    maven {
        url  "https://dl.bintray.com/grails/plugins" 
    }
}
```
```groovy
compile 'org.grails.plugins:embedded-mysql:1.1'
```
In your ***application.yml*** change the dataSource settings to the next one:
```yaml
dataSource:
    embeddedMysql: true
```

### Prerequisites

Plugin configured to work with Grails **3.0.0** or higher, but theoretically can be used even for 
older version of it. But this was not checked properly, so contributing is welcome.

### Configuration

Plugin uses next parameters in DataSource section:

|Parameter name|Description|Default value|
|--------------|-----------|-------------|
| *embeddedMysql* | Enabling of the plugin, main switcher. | false |
| *embeddedPort* | You can define the particular port, which will be used by MySQL instance | random free port |
| *url* | You can specify the url with any additional parameters, which MySQL understands | jdbc:mysql://localhost:<embeddedPort>/embedded_db |
| *username* || embedded_db |
| *password* || embedded_db |
| *schema* || embedded_db |

All other parameters common for the Grails Datasource configuration section are being used by grails dataSource plugin.
For example, you can set
```yaml
dataSource:
    pooled: true
    embeddedMysql: true
``` 
In this case embedded MySQL will be created with connection pool in front of it.

## License

[Apache License, Version 2.0](https://opensource.org/licenses/apache2.0.php)

## Acknowledgments

* Great respect to the [wix-embedded-mysql](https://github.com/wix/wix-embedded-mysql) for Embedded MySQL implementation.

## Known problems

`apt install libaio1`
'Stream closed':  `apt install libncurses5`