package blog.entity;

public class LoginResult extends Result<User> {
    boolean isLogin;

    public static Result success(String msg, boolean isLogin, User user) {
        return new LoginResult(ResultStatus.OK, msg, user, isLogin);
    }

    public static Result success(String msg, boolean isLogin) {
        return success(msg, isLogin, null);
    }

    protected LoginResult(ResultStatus status, String msg, User user, boolean isLogin) {
        super(status, msg, user);
        this.isLogin = isLogin;
    }

    public static Result failure(String msg, boolean isLogin) {
        return new LoginResult(ResultStatus.FAIL, msg, null, isLogin);
    }

    public static Result failure(String msg) {
        return failure(msg, false);
    }

    public boolean isLogin() {
        return isLogin;
    }
}
