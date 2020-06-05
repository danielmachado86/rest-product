package io.dmcapps.dshopping.product;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import javax.enterprise.context.Dependent;
import org.jboss.logging.Logger;

@QuarkusMain
@Dependent
public class Main {
    public static void main(String... args) {
        Quarkus.run(App.class, args);
    }

    
    public static class App implements QuarkusApplication {

        private static final Logger LOGGER = Logger.getLogger(App.class);

        @Override
        public int run(String... args) throws Exception {
            Quarkus.waitForExit();
            return 0;
        }
    }
}