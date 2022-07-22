import com.yoki.reggie.utils.EmailUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-16 19:24
 */
//@RunWith(SpringRunner.class)
@SpringBootTest(classes = {com.yoki.reggie.ReggieApplication.class},properties = "application.yml")
public class TestReggie {
    @Autowired
    private EmailUtils emailUtils;

    @Test
    public void testEmail() {
        emailUtils.send("y-CHess02@qq.com", "瑞吉外卖邮件测试", "1234");
    }
}
