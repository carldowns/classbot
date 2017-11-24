package com.pwllc.h2;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 */
public class H2Database {

    static {
        try {
            // load the driver
            Class.forName("org.h2.Driver");
        }
        catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    public static Connection getPersistentConnection() throws Exception {
        return DriverManager.getConnection("jdbc:h2:file:./data/h2", "sa", "");
    }

    public static Connection getInMemoryConnection() throws Exception {
        return DriverManager.getConnection("jdbc:h2:mem", "sa", "");
    }
}
