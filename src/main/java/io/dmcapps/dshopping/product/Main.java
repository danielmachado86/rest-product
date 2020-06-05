package io.dmcapps.dshopping.product;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import static io.quarkus.runtime.LaunchMode.DEVELOPMENT;
import static io.quarkus.runtime.LaunchMode.NORMAL;
import static io.quarkus.runtime.LaunchMode.TEST;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;

import org.bson.Document;
import org.jboss.logging.Logger;

import io.quarkus.runtime.LaunchMode;

@QuarkusMain
public class Main {
    public static void main(String... args) {
        Quarkus.run(App.class, args);
    }

    
    public static class App implements QuarkusApplication {

        private static final Logger LOGGER = Logger.getLogger(App.class);

        @Override
        public int run(String... args) throws Exception {
            //TODO: Add support to multiple classes

            MongoCollection<Document> collection = Product.mongoDatabase().getCollection("products");
            
            if (LaunchMode.current() == DEVELOPMENT) {

                System.out.println("Working Directory = " + System.getProperty("user.dir"));

                int count = 0;
                int batch = 100;

                List<InsertOneModel<Document>> docs = new ArrayList<>();

                try (BufferedReader br = new BufferedReader(new FileReader("/home/danielmc86/p_projects/dshopping-quarkus/rest-product/src/main/resources/import.json"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println("Line readed: " + line);  
                        docs.add(new InsertOneModel<>(Document.parse(line)));
                        count++;
                        if (count == batch) {
                            collection.bulkWrite(docs, new BulkWriteOptions().ordered(false));
                        docs.clear();
                        count = 0;
                        }
                    }
                }
                if (count > 0) {
                    collection.bulkWrite(docs, new BulkWriteOptions().ordered(false));
                }     
                LOGGER.debug("import.json loaded");  
            }
            Quarkus.waitForExit();
            return 0;
        }
    }
}