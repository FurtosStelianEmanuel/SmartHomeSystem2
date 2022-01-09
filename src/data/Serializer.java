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
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import persistance.Repository;

/**
 *
 * @author Manel
 */
@Injectable
public class ShsSerializer {

    private final BananaConvert bananaConvert;
    private final PathProvider pathProvider;
    private final SerializationUtils serializationUtils;

    public ShsSerializer(PathProvider pathProvider, SerializationUtils serializationUtils) {
        bananaConvert = new BananaConvert();
        this.pathProvider = pathProvider;
        this.serializationUtils = serializationUtils;
    }

    public BananaConvert getBananaConvert() {
        return bananaConvert;
    }

    public <K extends Repository> void updateRepository(K repository) throws FileNotFoundException, SerializationException {
        checkSerializationDirectory();

        String serializedToJson = bananaConvert.serializeToJson(repository.mapToSerializedFormat());

        serializationUtils.serializeAsJson(serializedToJson, getPathToRepository(repository));
    }

    public <K extends Repository, T> T loadRepository(K repository) throws FileNotFoundException, DeserializationException {
        checkSerializationDirectory();

        return (T) bananaConvert.deserializeJson(getPathToRepository(repository), repository.mapToSerializedFormat().getClass());
    }

    public Path getSerializationPath() {
        return Paths.get(pathProvider.getCurrentWorkingDirectory(), serializationUtils.serializationDirectory);
    }

    private <K extends Repository> Path getPathToRepository(K repository) {
        return Paths.get(getSerializationPath().toString(), String.format("%s.json", repository.getClass().getName()));
    }

    private void checkSerializationDirectory() throws FileNotFoundException {
        File serializationDirectory = getSerializationPath().toFile();
        if (serializationDirectory.exists()) {
            return;
        }

        if (serializationDirectory.mkdir()) {
            return;
        }

        throw new FileNotFoundException();
    }
}