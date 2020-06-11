package io.dmcapps.dshopping.product;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileFinder {

    private List<CollectionTuple<String, File>> files;
    private static final String FILE_NAME_SEPARATOR = "-";
    private static final String FILE_NAME_SUFFIX = "import";

    public FileFinder(String path, String fileType){

        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            files = paths
                .filter(p -> p
                        .toString()
                        .endsWith(
                            FILE_NAME_SEPARATOR +
                            FILE_NAME_SUFFIX + "." + 
                            fileType))
                        .map(p -> new CollectionTuple<String, File>(getCollectionName(p, fileType), p.toFile()))
                .collect(Collectors.toList());
        } catch (IOException e) {
            //TODO: Add exception behavior
        }
    }

	private String getCollectionName(Path path, String fileType) {
        String fileName = path.getFileName().toString();
        String collectionName = fileName.replaceAll(
            FILE_NAME_SEPARATOR +
            FILE_NAME_SUFFIX + "." + 
            fileType, "");
		return collectionName;
	}

	public List<CollectionTuple<String, File>> getFiles() {
		return files;
	}

}