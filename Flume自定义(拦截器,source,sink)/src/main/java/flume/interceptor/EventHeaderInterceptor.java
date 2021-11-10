package flume.interceptor;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 自定义拦截器：需要实现flume提供的Interceptor接口
 * 并且要通过创建内部类里面的builder方法返回类对象
 */
public class EventHeaderInterceptor implements Interceptor {
    @Override
    public void initialize() {

    }

    @Override
    public Event intercept(Event event) {
        //拦截方法
        //1.获取event的headers
        Map<String, String> headers = event.getHeaders();
        //2.获取event的body(一般编码格式为UTF-8)
        String body = new String(event.getBody(), StandardCharsets.UTF_8);
        //3.判断body中是否包含 “honglang”,"bushi"
        if (body.contains("honglang")){
            headers.put("title","at");
        }else if (body.contains("bushi")){
            headers.put("title","bt");
        }else {
            headers.put("title","ot");
        }

        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        //遍历每一个event
        for (Event event : events) {
            intercept(event);
        }
        return events;
    }

    @Override
    public void close() {

    }

    public static class MyBuilder implements Builder{
        @Override
        public Interceptor build() {
            return new EventHeaderInterceptor();
        }

        @Override
        public void configure(Context context) {

        }
    }
}
