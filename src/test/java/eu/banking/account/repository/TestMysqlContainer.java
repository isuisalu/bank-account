package eu.banking.account.repository;

import lombok.SneakyThrows;
import org.testcontainers.containers.MySQLContainer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class TestMysqlContainer extends MySQLContainer<TestMysqlContainer> {
    private static final String IMAGE_VERSION = "mysql:8.0.21";
    private static TestMysqlContainer container;

    private TestMysqlContainer() {
        super(IMAGE_VERSION);
    }

    public static TestMysqlContainer getInstance() {
        if (container == null) {
            container = new TestMysqlContainer();
         }
        return container;
    }

    @SneakyThrows
    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
    @SuppressWarnings("unchecked")
    private static Map<String, String> getModifiableEnvironment() throws Exception
    {
        Class<?> pe = Class.forName("java.lang.ProcessEnvironment");
        Method getenv = pe.getDeclaredMethod("getenv", String.class);
        getenv.setAccessible(true);
        Field props = pe.getDeclaredField("theCaseInsensitiveEnvironment");
        props.setAccessible(true);
        return (Map<String, String>) props.get(null);
    }
}
