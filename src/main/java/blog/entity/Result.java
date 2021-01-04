package blog.entity;

public abstract class Result<T> {//只能使用abstract的子类

    public enum ResultStatus {
        OK("ok"),
        FAIL("fail");

        private String status;

        ResultStatus(String status) {
            this.status = status;
        }
    }

    ResultStatus status;
    String msg;
    T data;


    //静态工场模式的特点就是工厂方法返回一个实例，构造器定义为private
//    public static Result failure(String message) {
//        return new Result("fail", message, false);
//    }
//
//    public static Result success(String message) {
//        return new Result("ok", message, true);
//    }

    protected Result(ResultStatus status, String msg) {
        this(status, msg, null);
    }

    protected Result(ResultStatus status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}