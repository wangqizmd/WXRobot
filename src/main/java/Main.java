import com.ytx.wechat.client.WeChatClient;
import com.ytx.wechat.listener.WeChatClientListener;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Main {
    public static void main(String[] args) {
        new WeChatClient().setListener(new WeChatClientListener()).start();
    }
}
