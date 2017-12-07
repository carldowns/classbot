package com.pwllc.h2;

import com.codahale.metrics.health.HealthCheck;

/**
 *
 */
public class H2HealthCheck extends HealthCheck {

    public H2HealthCheck() {
    }

    @Override
    protected Result check() throws Exception {

        try {
            String catalog = H2Database.getPersistentConnection().getCatalog();
            System.out.println("H2 catalog: " + catalog);
            return Result.healthy();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return Result.unhealthy(e.getMessage());
        }
    }
}
