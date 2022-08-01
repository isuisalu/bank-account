package eu.banking.account.repository;

import lombok.SneakyThrows;
import org.testcontainers.containers.OracleContainer;

public class MyDBContainer extends OracleContainer {
    private static final String IMAGE_VERSION = "gvenzl/oracle-xe:18.4.0-slim";
    private static MyDBContainer container;

    private MyDBContainer() {
        super(IMAGE_VERSION);
    }

    public static MyDBContainer getInstance() {
        if (container == null) {
            container = new MyDBContainer();
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

}
