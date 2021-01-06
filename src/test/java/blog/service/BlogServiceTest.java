package blog.service;

import blog.dao.BlogDao;
import blog.entity.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class BlogServiceTest {
    @Mock
    BlogDao blogDao;
    @InjectMocks
    BlogService blogService;

    @Test
    void getBlogsFromDB() {
        blogService.getBlogs(1, 10, null);
        verify(blogDao).getBlogs(1, 10, null);
    }

    @Test
    void returnFailureWhenExceptionThrow() {
        when(blogDao.getBlogs(anyInt(), anyInt(), any())).thenThrow(new RuntimeException());
        Result result = blogService.getBlogs(1, 10, null);

        Assertions.assertEquals(Result.ResultStatus.FAIL, result.getStatus());
        Assertions.assertEquals("系统异常", result.getMsg());
    }
}
