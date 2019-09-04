import com.ytx.wechat.api.weather.WeatherApi;
import com.ytx.wechat.client.WeChatClient;
import com.ytx.wechat.entity.message.WXMessage;
import com.ytx.wechat.entity.message.WXText;
import com.ytx.wechat.listener.WeChatClientListener;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Main {
    private static WeChatClient weChatClient;

    public static void main(String[] args) {
        weChatClient = new WeChatClient();
        weChatClient.setListener(new WeChatClientListener());
        weChatClient.start();
    }
}
