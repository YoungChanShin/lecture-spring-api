package edu.magnet.interactiveblog.configs;

import edu.magnet.interactiveblog.accounts.Account;
import edu.magnet.interactiveblog.accounts.AccountRole;
import edu.magnet.interactiveblog.accounts.AccountService;
import edu.magnet.interactiveblog.common.BaseControllerTest;
import edu.magnet.interactiveblog.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {
    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("인증토큰 발급받는 테스트")
    public void getAuthToken() throws Exception {
        String clientId = "myApp";
        String clientSecret = "pass";

        String email = "chan8149@naver.com";
        String chan1 = "chan";
        Account chan = Account.builder()
                .email(email)
                .password(chan1)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(chan);

        this.mockMvc.perform(post("/oauth/token")
                    .with(httpBasic(clientId, clientSecret))
                    .param("username",email)
                    .param("password",chan1)
                    .param("grant_type", "password")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
        ;
    }

}