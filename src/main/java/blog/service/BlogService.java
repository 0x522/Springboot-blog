package blog.service;

import blog.dao.BlogDao;
import blog.entity.Blog;
import blog.entity.BlogListResult;
import blog.entity.BlogResult;
import blog.entity.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

@Service
public class BlogService {
    private BlogDao blogDao;
    private UserService userService;

    @Inject
    public BlogService(BlogDao blogDao, UserService userService) {
        this.blogDao = blogDao;
        this.userService = userService;
    }

    public BlogListResult getBlogs(Integer page, Integer pageSize, Integer userId) {
        try {
            List<Blog> blogs = blogDao.getBlogs(page, pageSize, userId);
            int count = blogDao.count(userId);
            //如果能整除不加1，不能整除加1
            int pageCount = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
            return BlogListResult.success(blogs, count, page, pageCount);
        } catch (Exception e) {
            return BlogListResult.failure("系统异常");
        }
    }

    public BlogResult getBlogById(Integer blogId) {
        try {
            return BlogResult.success("获取成功", blogDao.selectBlogById(blogId));
        } catch (Exception e) {
            return BlogResult.failure(e);
        }
    }

    public BlogResult insertBlog(Blog newBlog) {
        try {
            return BlogResult.success("创建成功", blogDao.insertBlog(newBlog));
        } catch (Exception e) {
            return BlogResult.failure(e);
        }
    }

    public BlogResult updateBlog(int blogId, Blog targetBlog) {
        Blog blogInDB = blogDao.selectBlogById(blogId);
        if (blogInDB == null) {
            return BlogResult.failure("博客不存在");
        }
        if (!Objects.equals(blogId, blogInDB.getId())) {
            return BlogResult.failure("无法修改别人的博客");
        }
        try {
            targetBlog.setId(blogId);
            return BlogResult.success("修改成功", blogDao.updateBlog(targetBlog));
        } catch (Exception e) {
            return BlogResult.failure(e);
        }
    }

    public BlogResult deleteBlog(int blogId, User user) {
        Blog blogInDB = blogDao.selectBlogById(blogId);
        if (blogInDB == null) {
            return BlogResult.failure("博客不存在");
        }
        if (!Objects.equals(blogId, blogInDB.getId())) {
            return BlogResult.failure("无法修改别人的博客");
        }
        try {
            blogDao.deleteBlog(blogId);
            return BlogResult.success("删除成功");
        } catch (Exception e) {
            return BlogResult.failure(e);
        }
    }

}
