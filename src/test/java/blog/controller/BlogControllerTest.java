package blog.controller;

import blog.entity.User;
import blog.service.AuthService;
import blog.service.BlogService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.Charset;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class BlogControllerTest {
    private MockMvc mvc;
    @Mock
    private AuthService authService;
    @Mock
    private BlogService blogService;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new BlogController(blogService, authService)).build();
    }

    @Test
    void requireLoginBeforeProceeding() throws Exception {
        mvc.perform(post("/blog")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> Assertions
                        .assertTrue(mvcResult.getResponse().getContentAsString(Charset.forName("utf-8"))
                                .contains("当前没有登陆用户,登录后才能操作")));
    }

    @Test
    void invalidRequestIfTitleIsEmpty() throws Exception {
        Mockito.when(authService.getCurrentUser()).thenReturn(Optional.of(new User(1, "mockUser", "")));
        mvc.perform(post("/blog").contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"123\"}"))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> Assertions
                        .assertTrue(mvcResult.getResponse().getContentAsString(Charset.forName("utf-8"))
                                .contains("title is invalid!")));
    }
}
