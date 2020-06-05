package io.dmcapps.dshopping.product.beans;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.result.DeleteResult;

import org.bson.Document;
import org.jboss.logging.Logger;

import io.dmcapps.dshopping.product.Product;
import io.quarkus.runtime.LaunchMode;

public class DatabaseInitialization {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitialization.class);

    public void initialize() throws Exception {
        LOGGER.info("Launch mode: " + LaunchMode.current());
        LOGGER.info("Working Directory = " + System.getProperty("user.dir"));

    
        MongoCollection<Document> collection = Product.mongoDatabase().getCollection("products");

        BasicDBObject document = new BasicDBObject();
        DeleteResult result = collection.deleteMany(document);
        LOGGER.info("Number of Deleted Document(s) : " + result.getDeletedCount());

        int count = 0;
        int batch = 100;

        List<InsertOneModel<Document>> docs = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/import.json"))) {
            String line;
            while ((line = br.readLine()) != null) {
                docs.add(new InsertOneModel<>(Document.parse(line)));
                count++;
                if (count == batch) {
                    collection.bulkWrite(docs, new BulkWriteOptions().ordered(false));
                    LOGGER.info("Number of New Document(s): 100");  
                    docs.clear();
                    count = 0;
                }
            }
        }
        if (count > 0) {
            collection.bulkWrite(docs, new BulkWriteOptions().ordered(false));
            LOGGER.info("The Numbers of New Document(s): " + count);  
        }     
        LOGGER.info("import.json file loaded");
    }
}