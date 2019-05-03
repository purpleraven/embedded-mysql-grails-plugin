package embedded.mysql

import com.wix.mysql.EmbeddedMysql
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.DisposableBean
import sun.reflect.generics.reflectiveObjects.NotImplementedException

@Slf4j
class EmbeddedMysqlHolder implements DisposableBean {
    private EmbeddedMysql db

    EmbeddedMysqlHolder(EmbeddedMysql db) {
        this.db = db
    }

    def getPort(){
        throw new NotImplementedException("NO PORT")
//        db.port // todo ???
    }

    @Override
    void destroy() throws Exception {
        log.warn "Stopping ${db.toString()}"
        db.stop()
    }
}

