/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import annotations.Injectable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;

/**
 *
 * @author Manel
 */
@Injectable
public class SerializationUtils {

    public final String serializationDirectory = "serialized";

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
}
