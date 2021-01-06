package blog.entity;

import java.util.List;

public class BlogListResult extends Result<List<Blog>> {
    private int total;
    private int page;
    private int totalPage;

    private BlogListResult(ResultStatus status, String msg, List<Blog> data, int total, int page, int totalPage) {
        super(status, msg, data);
        this.page = page;
        this.total = total;
        this.totalPage = totalPage;
    }

    public static BlogListResult success(List<Blog> data, int total, int page, int totalPage) {
        return new BlogListResult(ResultStatus.OK, "获取成功", data, total, page, totalPage);
    }

    public static BlogListResult failure(String msg) {
        return new BlogListResult(ResultStatus.FAIL, msg, null, 0, 0, 0);
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
