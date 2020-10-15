import java.io.File;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-20
 * @time 21:36
 */
public class Test {

    public static void main(String[] args) {
        System.out.println(File.separator);

        ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
        System.out.println(threadClassLoader);

    }
}
