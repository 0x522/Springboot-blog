package blog.service;

import blog.dao.BlogDao;
import blog.entity.Blog;
import blog.entity.BlogResult;
import blog.entity.Result;
import blog.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
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

        Assertions.assertEquals("fail", result.getStatus());
        Assertions.assertEquals("系统异常", result.getMsg());
    }

    @Test
    void returnFailureWhenBlogNotFound() {
        when(blogDao.selectBlogById(1)).thenReturn(null);
        BlogResult result = blogService.deleteBlog(1, mock(User.class));

        Assertions.assertEquals("fail", result.getStatus());
        Assertions.assertEquals("博客不存在", result.getMsg());

    }

    @Test
    void returnFailureWhenBlogUserIdNotMatch() {
        User blogAuthor = new User(123, "blogAuthor", "");
        User operator = new User(456, "operator", "");

        Blog targetBlog = new Blog();
        targetBlog.setId(1);
        targetBlog.setUser(operator);

        Blog blogInDB = new Blog();
        blogInDB.setUser(blogAuthor);

        when(blogDao.selectBlogById(1)).thenReturn(blogInDB);

        BlogResult result = blogService.updateBlog(1, targetBlog);

        Assertions.assertEquals("fail", result.getStatus());
        Assertions.assertEquals("无法修改别人的博客", result.getMsg());
    }
}
