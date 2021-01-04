package blog.controller;

import blog.entity.Blog;
import blog.entity.BlogListResult;
import blog.entity.BlogResult;
import blog.entity.User;
import blog.service.AuthService;
import blog.service.BlogService;
import blog.utils.AssertUtils;
import com.mysql.cj.util.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.Map;

@Controller
public class BlogController {
    private AuthService authService;
    private BlogService blogService;

    @Inject
    public BlogController(BlogService blogService, AuthService authService) {
        this.blogService = blogService;
        this.authService = authService;
    }

    @GetMapping("/blog")
    @ResponseBody
    public BlogListResult getBlog(@RequestParam("page") Integer page, @RequestParam(value = "userId", required = false) Integer userId) {
        if (page == null || page <= 0) {
            page = 1;
        }
        return blogService.getBlogs(page, 10, userId);
    }

    @GetMapping("/blog/{blogId}")
    @ResponseBody
    public BlogResult getBlog(@PathVariable("blogId") Integer blogId) {
        return blogService.getBlogById(blogId);
    }

    @PostMapping("/blog")
    @ResponseBody
    public BlogResult newBlog(@RequestBody Map<String, String> param) {
        try {
            return authService
                    .getCurrentUser()
                    .map(user -> blogService.insertBlog(createValidBlogFromParam(param, user)))
                    .orElse(BlogResult.failure("当前没有登陆用户,登录后才能操作"));
        } catch (IllegalArgumentException e) {
            return BlogResult.failure(e);
        }
    }

    @PatchMapping("/blog/{blogId}")
    @ResponseBody
    public BlogResult updateBlog(@PathVariable("blogId") int blogId, @RequestBody Map<String, String> param) {
        try {
            return authService
                    .getCurrentUser()
                    .map(user -> blogService.updateBlog(blogId, createValidBlogFromParam(param, user)))
                    .orElse(BlogResult.failure("当前没有登录用户,登录后才能操作"));
        } catch (IllegalArgumentException e) {
            return BlogResult.failure(e);
        }
    }

    @DeleteMapping("/blog/{blogId}")
    @ResponseBody
    public BlogResult deleteBlog(@PathVariable("blogId") int blogId, @RequestBody Map<String, String> param) {
        try {
            return authService
                    .getCurrentUser()
                    .map(user -> blogService.deleteBlog(blogId, user))
                    .orElse(BlogResult.failure("当前没有登录用户,登陆后才能操作"));
        } catch (IllegalArgumentException e) {
            return BlogResult.failure(e);
        }
    }

    /**
     * 判断blog的字段是否合法，手动设置后返回一个新的blog
     *
     * @param params 传入的blog k-v map结构
     * @param user   当前用户
     * @return
     */
    private Blog createValidBlogFromParam(Map<String, String> params, User user) {
        Blog blog = new Blog();
        String title = params.get("title");
        String content = params.get("content");
        String description = params.get("description");
        /**
         * title : 博客标题, 博客标题不能为空，且不超过100个字符
         * content : 博客内容, 博客内容不能为空，且不超过10000个字符
         * description: 博客内容简要描述,可为空，如果为空则后台自动从content中提取
         */
        AssertUtils.assertTrue(!StringUtils.isNullOrEmpty(title) && title.length() < 100, "title is valid!");
        AssertUtils.assertTrue(!StringUtils.isNullOrEmpty(content) && content.length() < 10000, "content is valid!");
        if (StringUtils.isNullOrEmpty(description)) {
            description = content.substring(0, Math.min(content.length(), 10)) + "...";
        }

        //手动设置blog
        blog.setTitle(title);
        blog.setContent(content);
        blog.setDescription(description);
        blog.setUser(user);
        return blog;
    }

}
