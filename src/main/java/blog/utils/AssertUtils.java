package blog.utils;

public class AssertUtils {
    //断言工具类
    public static void assertTrue(boolean value, String message) {
        if (!value) {
            throw new IllegalArgumentException(message);
        }
    }
}
