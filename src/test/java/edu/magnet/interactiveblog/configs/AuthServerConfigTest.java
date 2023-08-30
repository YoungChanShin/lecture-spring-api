package edu.magnet.interactiveblog.configs;

import edu.magnet.interactiveblog.accounts.AccountService;
import edu.magnet.interactiveblog.common.AppProperties;
import edu.magnet.interactiveblog.common.BaseTest;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseTest {
    @Autowired
    AccountService accountService;
    @Autowired
    AppProperties appProperties;

    @Test
    @DisplayName("인증토큰 발급받는 테스트")
    public void getAuthToken() throws Exception {
        String clientId = appProperties.getClientId();
        String clientSecret = appProperties.getClientSecret();

        String email = appProperties.getUserUsername();
        String chan1 = appProperties.getUserPassword();
//        Account chan = Account.builder()
//                .email(email)
//                .password(chan1)
//                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
//                .build();
//        this.accountService.saveAccount(chan);

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