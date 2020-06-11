package io.dmcapps.dshopping.product.init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.result.DeleteResult;

import org.bson.Document;
import org.jboss.logging.Logger;

import io.dmcapps.dshopping.product.CollectionTuple;
import io.dmcapps.dshopping.product.FileFinder;
import io.dmcapps.dshopping.product.Product;
import io.quarkus.runtime.LaunchMode;

public class DatabaseInitialization {

    private static final Logger LOGGER = Logger.getLogger(
            DatabaseInitialization.class);

    public static void mongoDBInitialize(String profile) {
        LOGGER.info("Launch mode: " + LaunchMode.current());
        LOGGER.info("Working Directory = " + 
                System.getProperty("user.dir"));

        String path = buildRootPath(profile);
        String fileType = "json";

        FileFinder fileFinder = new FileFinder(path, fileType);
        List<CollectionTuple<String, File>> files = fileFinder.getFiles();

        MongoDatabase database = Product.mongoDatabase();

        for(CollectionTuple<String, File> collectionFileInfo: files) {
            MongoCollection<Document> collection = database.getCollection(collectionFileInfo.name);
            emptyMongoCollection(profile, collection);
            refillMongoCollection(collection, collectionFileInfo);
        }
    }

    private static void refillMongoCollection(MongoCollection<Document> collection, CollectionTuple<String, File> collectionFileInfo){

        int count = 0;
        int batch = 100;

        List<InsertOneModel<Document>> docs = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(
                    new FileReader(collectionFileInfo.file));
            String line;
            while ((line = br.readLine()) != null) {
                docs.add(new InsertOneModel<>(
                        Document.parse(line)));
                count++;
                if (count == batch) {
                    collection.bulkWrite(
                        docs, 
                        new BulkWriteOptions()
                            .ordered(false));
                    LOGGER.info("\"" + collectionFileInfo.name + "-import.json\" - Number of New Document(s): " + batch);  
                    docs.clear();
                    count = 0;
                }
            }
            br.close();
        } catch (FileNotFoundException e){
            LOGGER.error("File \"" + collectionFileInfo.name + "-import.json\" not found");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        if (count > 0) {
            collection.bulkWrite(
                    docs, 
                    new BulkWriteOptions()
                        .ordered(false));
            LOGGER.info("\"" + collectionFileInfo.name + "-import.json\" - Number of New Document(s): "  + count);  
        }     
        LOGGER.info(collectionFileInfo.name + "-import.json file loaded");
    }

	private static String buildRootPath(String profile) {
        String path = "../src/main/resources/";

        if(profile == "test"){
            path = "./src/main/resources/";
        }
        return path;
    }

    private static void emptyMongoCollection(String profile, MongoCollection<Document> collection) {
        if (profile == "dev") {
            BasicDBObject document = new BasicDBObject();
            DeleteResult result = collection
                    .deleteMany(document);
            LOGGER.info("Number of Deleted Document(s) : " +
                        result.getDeletedCount());
        }
    }
}