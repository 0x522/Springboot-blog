package hello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpSession;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class AuthControllerTest {
    private MockMvc mvc;
    @Mock
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.
                standaloneSetup(
                        new AuthController(authenticationManager, userService)).
                build();
    }

    @Test
    void returnNotLoginByDefault() throws Exception {
        //perform用来执行http方法
        mvc.perform(get("/auth"))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> Assertions.assertTrue(mvcResult.getResponse().
                        getContentAsString(Charset.forName("utf-8")).
                        contains("用户没有登录")));
    }

    @Test
    void testLogin() throws Exception {
        //未登录时，/auth接口返回未登录状态
        mvc.perform(get("/auth"))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> Assertions.assertTrue(mvcResult.getResponse().
                        getContentAsString(Charset.forName("utf-8")).
                        contains("用户没有登录")));

        //使用/auth/login登录
        Map<String, String> usernameAndPassword = new HashMap<>();
        usernameAndPassword.put("username", "myUser");
        usernameAndPassword.put("password", "myPassword");

        //Mock Spring User 规避NullException
        when(userService.loadUserByUsername("myUser"))
                .thenReturn(
                        new User("myUser", bCryptPasswordEncoder.encode("myPassword"), Collections.emptyList()));
        //Mock entity User
        when(userService.getUserByUsername("myUser"))
                .thenReturn(
                        new hello.entity.User(123, "myUser", bCryptPasswordEncoder.encode("myPassword")));

        MvcResult response =
                mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(usernameAndPassword)))
                        .andExpect(status().isOk())
                        .andExpect(mvcResult -> Assertions
                                .assertTrue(mvcResult.getResponse()
                                        .getContentAsString(Charset.forName("utf-8")).contains("登录成功")))
                        .andReturn();
        //拿到session
        HttpSession session = response.getRequest().getSession();

        //再次检查/auth的返回值,用户已经处于登录状态
        assert session != null;
        mvc.perform(get("/auth").session((MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    System.out.println(mvcResult
                            .getResponse()
                            .getContentAsString(Charset.forName("utf-8")));
                    Assertions.assertTrue(mvcResult.getResponse()
                            .getContentAsString(Charset.forName("utf-8"))
                            .contains("myUser"));
                });
    }


}