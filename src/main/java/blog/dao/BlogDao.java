package blog.dao;

import blog.entity.Blog;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BlogDao {
    private final SqlSession sqlSession;

    @Inject
    public BlogDao(final SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public List<Blog> getBlogs(Integer page, Integer pageSize, Integer userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("offset", (page - 1) * pageSize);
        params.put("limit", pageSize);
        return sqlSession.selectList("selectBlog", params);
    }

    public int count(Integer userId) {
        return sqlSession.selectOne("countBlog", userId);
    }

    public Blog selectBlogById(Integer blogId) {
        return sqlSession.selectOne("selectBlogById", blogId);
    }

    public Blog insertBlog(Blog newBlog) {
        sqlSession.insert("insertBlog", newBlog);
        return selectBlogById(newBlog.getId());
    }

    public Blog updateBlog(Blog targetBlog) {
        sqlSession.update("updateBlog", targetBlog);
        return selectBlogById(targetBlog.getId());
    }

    public void deleteBlog(int blogId) {
        sqlSession.delete("deleteBlog", blogId);
    }
}
