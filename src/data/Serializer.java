/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import annotations.Injectable;
import bananaconvert.BananaConvert;
import bananaconvert.marshaler.exception.DeserializationException;
import bananaconvert.marshaler.exception.SerializationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import persistance.Repository;

/**
 *
 * @author Manel
 */
@Injectable
public class Serializer {

    private final BananaConvert bananaConvert;
    private final PathProvider pathProvider;
    public final String serializationDirectory = "serialized";

    public Serializer(PathProvider pathProvider) {
        bananaConvert = new BananaConvert();
        this.pathProvider = pathProvider;
    }

    public BananaConvert getBananaConvert() {
        return bananaConvert;
    }

    public <K extends Repository> void updateRepository(K repository) throws FileNotFoundException, SerializationException {
        checkSerializationDirectory();

        String serializedToJson = bananaConvert.serializeToJson(repository.mapToSerializedFormat());

        serializeAsJson(serializedToJson, getPathToRepository(repository));
    }

    public <K extends Repository, T> T loadRepository(K repository) throws FileNotFoundException, DeserializationException {
        checkSerializationDirectory();

        return (T) bananaConvert.deserializeJson(getPathToRepository(repository), repository.mapToSerializedFormat().getClass());
    }

    public Path getSerializationPath() {
        return Paths.get(pathProvider.getCurrentWorkingDirectory(), serializationDirectory);
    }

    public void serializeAsByteData(Object object, Path pathToStore) throws IOException {
        File serializationTarget = pathToStore.toFile();

        try (FileOutputStream file = new FileOutputStream(serializationTarget); ObjectOutputStream out = new ObjectOutputStream(file)) {
            out.writeObject(object);
        }
    }

    public <T> T deserializeByteData(String filePath) throws IOException, ClassNotFoundException {
        T object1;

        try (FileInputStream file = new FileInputStream(filePath); ObjectInputStream in = new ObjectInputStream(file)) {
            object1 = (T) in.readObject();
        }

        return object1;
    }

    public void serializeAsJson(String serializedObject, Path pathToStore) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(pathToStore.toFile())) {
            out.println(serializedObject);
        }
    }

    private <K extends Repository> Path getPathToRepository(K repository) {
        return Paths.get(getSerializationPath().toString(), String.format("%s.json", repository.getClass().getName()));
    }

    private void checkSerializationDirectory() throws FileNotFoundException {
        File serializationDirectoryFile = getSerializationPath().toFile();
        if (serializationDirectoryFile.exists()) {
            return;
        }

        if (serializationDirectoryFile.mkdir()) {
            return;
        }

        throw new FileNotFoundException();
    }
}
