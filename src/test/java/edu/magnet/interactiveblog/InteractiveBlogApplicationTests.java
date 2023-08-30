package edu.magnet.interactiveblog;

import edu.magnet.interactiveblog.common.BaseTest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class InteractiveBlogApplicationTests extends BaseTest {

	@Test
	void contextLoads() {
	}

}
