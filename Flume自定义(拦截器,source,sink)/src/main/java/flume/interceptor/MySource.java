package flume.interceptor;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.source.AbstractSource;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 自定义source 需要：继承abstractSource,实现Configurable,PollableSource接口，
 *
 *
 */
public class MySource extends AbstractSource implements Configurable, PollableSource {
    private String prefix;

    @Override
    //该方法在flume的处理流程中是循环使用的
    public Status process() throws EventDeliveryException {
        Status status = null;
        //Source的核心处理方法
        //休眠一秒中
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {

            // Receive new data
            // 采集数据，封装成event对象
            Event e = getSomeData();
            // 将event对象交给ChannelProcessor进行处理
            getChannelProcessor().processEvent(e);
            // 正常处理,返回Status.READY
            status = Status.READY;
        } catch (Throwable t) {
            // 处理失败,返回 Status.BACKOFF
            status = Status.BACKOFF;
        }
        return status;
    }

    private Event getSomeData() {
        String data = UUID.randomUUID().toString();
        String  resultData = prefix+data;
        SimpleEvent event = new SimpleEvent();
        event.setBody(resultData.getBytes(StandardCharsets.UTF_8));
        event.getHeaders().put("auther","lhl");
        return event;

    }

    @Override
    //规避时间的增长步长
    public long getBackOffSleepIncrement() {
        return 1;
    }

    @Override
    //最大规避时间
    public long getMaxBackOffSleepInterval() {
        return 10;
    }

    @Override
    //用于读取flume的配置信息 xxx.conf文件
    public void configure(Context context) {
        prefix = context.getString("prefix","log-");
    }
}
