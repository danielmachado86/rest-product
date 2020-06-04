package io.dmcapps.dshopping.product;

import java.util.Collections;
import java.util.Map;

import org.testcontainers.containers.MongoDBContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class DatabaseResource implements QuarkusTestResourceLifecycleManager {

    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer();

    @Override
    public Map<String, String> start() {
        mongoDBContainer.start();
        return Collections.singletonMap(
            "quarkus.mongodb.connection-string",
            "mongodb://" + mongoDBContainer.getContainerIpAddress() + ":" + mongoDBContainer.getFirstMappedPort()
        );
    }

    @Override
    public void stop() {
        mongoDBContainer.stop();
    }
}