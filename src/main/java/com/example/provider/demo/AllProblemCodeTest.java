import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Field;

/**
 * 全类型缺陷汇总测试类，用于AI CodeReview工具可靠性验证
 * 缺陷覆盖：语法编译错、线程竞态、DCL错误、死锁、线程池泄漏、SQL注入、命令注入、XSS、不安全反序列化
 * 硬编码密钥、弱随机、IO流未关闭、NPE、数组越界、吞噬异常、字符串拼接性能、拆装箱、equals不重写hashCode
 * 静态集合OOM、double金额精度、反射篡改私有域、无同步共享变量等
 */
public class AllProblemCodeTest {

    // ====================== 1. 语法编译错误区域 ======================
    public void syntaxErrorBlock() {
        // 未定义变量直接使用
        System.out.println(num);
        // 类型不匹配赋值
        String testStr = 666;
        // 缺少分号
        int age = 25
        // 无返回值方法return数值
        return 123;
    }

    static void localStaticErr() {
        // 局部变量禁止static修饰
        static int temp = 100;
    }

    // ====================== 2. 线程安全-竞态条件 ======================
    private static int raceCount = 0;
    public void addRaceNum() {
        // count++ 非原子操作，多线程竞态
        raceCount++;
    }

    // ====================== 3. DCL单例缺失volatile ======================
    private static AllProblemCodeTest instance;
    private AllProblemCodeTest() {}
    public static AllProblemCodeTest getInstance() {
        if (instance == null) {
            synchronized (AllProblemCodeTest.class) {
                if (instance == null) {
                    instance = new AllProblemCodeTest();
                }
            }
        }
        return instance;
    }

    // ====================== 4. 标准死锁代码 ======================
    private static final Object lockA = new Object();
    private static final Object lockB = new Object();

    public void lockFuncA() {
        synchronized (lockA) {
            try { Thread.sleep(100); } catch (Exception e) {}
            synchronized (lockB) {
                System.out.println("A拿到双锁");
            }
        }
    }
    public void lockFuncB() {
        synchronized (lockB) {
            try { Thread.sleep(100); } catch (Exception e) {}
            synchronized (lockA) {
                System.out.println("B拿到双锁");
            }
        }
    }

    // ====================== 5. 无界线程池 + 不关闭资源泄漏 ======================
    public void badThreadPool() {
        ExecutorService pool = Executors.newCachedThreadPool();
        for (int i = 0; i < 10000; i++) {
            pool.submit(() -> {
                try { Thread.sleep(1000); } catch (Exception e) {}
            });
        }
        // 未执行shutdown/shutdownNow，线程永久泄漏
    }

    // ====================== 6. SQL注入 + 硬编码账号密码 ======================
    public void injectSqlQuery(String userName) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test", "root", "Root@123456");
        Statement stmt = conn.createStatement();
        // 直接拼接用户输入，注入漏洞
        String sql = "SELECT * FROM user WHERE name = '" + userName + "'";
        stmt.executeQuery(sql);
    }

    // ====================== 7. 操作系统命令注入 ======================
    public void execRiskCmd(String fileParam) throws Exception {
        Runtime.getRuntime().exec("cat " + fileParam);
    }

    // ====================== 8. XSS跨站脚本漏洞 ======================
    public void xssOutput(HttpServletResponse resp, String userInput) throws Exception {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        // 未转义直接输出可控内容
        out.write("欢迎用户：" + userInput);
    }

    // ====================== 9. 不安全反序列化 ======================
    public void unsafeDeserialize() throws Exception {
        FileInputStream fis = new FileInputStream("data.obj");
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object obj = ois.readObject();
        // 无白名单校验、无安全过滤
        ois.close();
    }

    // ====================== 10. 硬编码密钥 + 敏感信息打印日志 ======================
    private final String dbPassword = "Admin@654321";
    private final String apiSecret = "sk-abc123456secret";
    public void printSecretLog() {
        System.out.println("数据库密码:" + dbPassword + ",密钥:" + apiSecret);
    }

    // ====================== 11. 弱随机数（验证码/Token场景禁用） ======================
    public String weakCode() {
        int code = (int)(Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }

    // ====================== 12. IO流未关闭、无try-with-resources ======================
    public String readBadFile(String path) throws Exception {
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        // 未关闭流，异常时句柄泄漏
        return line;
    }

    // ====================== 13. NPE空指针风险代码 ======================
    public void npeTest(String inputStr) {
        System.out.println(inputStr.length());
        Object nullObj = null;
        String castStr = (String) nullObj;
    }

    // ====================== 14. 数组下标越界 ======================
    public void arrayIndexErr() {
        int[] arr = {10,20,30};
        System.out.println(arr[3]);
    }

    // ====================== 15. 空Catch吞噬异常，无堆栈无日志 ======================
    public void swallowException() {
        try {
            int zeroDiv = 1 / 0;
        } catch (Exception e) {
            // 完全吞异常，无打印无告警
        }
    }

    // ====================== 16. 循环字符串拼接（无StringBuilder性能灾难） ======================
    public String badStringConcat() {
        String res = "";
        for (int i = 0; i < 2000; i++) {
            res += i;
        }
        return res;
    }

    // ====================== 17. 循环频繁拆装箱 ======================
    public Integer autoBoxUnBox() {
        Integer total = 0;
        for (int i = 0; i < 10000; i++) {
            total = total + i;
        }
        return total;
    }

    // ====================== 18. 重写equals但不重写hashCode（契约破坏） ======================
    static class UserInfo {
        private Long id;
        private String name;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserInfo user = (UserInfo) o;
            return id.equals(user.id) && name.equals(user.name);
        }
        // 未重写hashCode，HashMap/HashSet存储异常
    }

    // ====================== 19. 静态全局无限List，内存持续膨胀OOM ======================
    private static final List<String> globalCache = new ArrayList<>();
    public void addCacheData(String data) {
        globalCache.add(data);
    }

    // ====================== 20. double浮点用于金额计算（精度丢失） ======================
    public void moneyDoubleError() {
        double num1 = 0.1;
        double num2 = 0.2;
        System.out.println(num1 + num2);
    }

    // ====================== 21. 反射暴力篡改私有字段，破坏封装 ======================
    public void hackPrivateField(Class<?> targetCls) throws Exception {
        Field secretField = targetCls.getDeclaredField("secretKey");
        secretField.setAccessible(true);
        secretField.set(targetCls.newInstance(), "cracked-key-001");
    }

    // ====================== 主方法：批量启动各类缺陷场景 ======================
    public static void main(String[] args) throws InterruptedException {
        AllProblemCodeTest test = new AllProblemCodeTest();

        // 并发竞态计数
        Thread t1 = new Thread(() -> {for(int i=0;i<1000;i++) test.addRaceNum();});
        Thread t2 = new Thread(() -> {for(int i=0;i<1000;i++) test.addRaceNum();});
        t1.start();t2.start();
        t1.join();t2.join();

        // 启动死锁线程
        new Thread(test::lockFuncA).start();
        new Thread(test::lockFuncB).start();

        // 调用其他缺陷方法
        test.badThreadPool();
        test.printSecretLog();
        test.badStringConcat();
        test.autoBoxUnBox();
        test.moneyDoubleError();
        test.swallowException();
        test.arrayIndexErr();
        test.addCacheData("test-data-1");
        test.addCacheData("test-data-2");
    }
}