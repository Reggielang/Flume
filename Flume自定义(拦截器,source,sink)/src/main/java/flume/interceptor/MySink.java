package flume.interceptor;

import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * 自定义Sink,需要继承flume提供的abstractsink,实现configurable接口
 */
public class MySink extends AbstractSink implements Configurable {
    Logger logger = LoggerFactory.getLogger(MySink.class);

    //sink的核心处理方法，该方法在flume处理流程中是循环调用的
    @Override
    public Status process() throws EventDeliveryException {
        Status status = null;
        // Start transaction
        //获取Channel
        Channel ch = getChannel();
        //获取事务对象
        Transaction txn = ch.getTransaction();
        //开启事务
        txn.begin();
        try {
            // 从channel中获取event
            Event event = ch.take();
            // 处理event
            storeSomeData(event);
            // 处理成功,提交事务
            txn.commit();
            status = Status.READY;
        } catch (Throwable t) {
            // 处理失败，回滚事务
            txn.rollback();

            status = Status.BACKOFF;

        } finally{
            //不论事务成功与否。都要关闭
            txn.close();
        }
        return status;
    }

    private void storeSomeData(Event event) {
        String printData = event.getHeaders()+"::"+ new String(event.getBody(), StandardCharsets.UTF_8);
        logger.info(printData);

    }

    @Override
    public void configure(Context context) {

    }
}
