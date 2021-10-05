/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import annotations.Injectable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manel
 */
@Injectable
public class SerializationUtils {

    public final String serializationDirectory = "serialized";
    
    public void serialize(Object obj, String directoryPath, String fileName) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean directoryCreated = directory.mkdir();
            Logger.getLogger(SerializationUtils.class.getName()).log(Level.INFO, directoryCreated + "",
                    directoryCreated ? "A trebuit sa creez un nou folder " : "Nu am putut un nou folder");
        }

        File serializationTarget = Paths.get(directoryPath, fileName).toFile();
        
        try (FileOutputStream file = new FileOutputStream(serializationTarget); ObjectOutputStream out = new ObjectOutputStream(file)) {
            out.writeObject(obj);
        }
    }

    public <T> T deserialize(String filePath) throws IOException, ClassNotFoundException {
        T object1;
        
        try (FileInputStream file = new FileInputStream(filePath); ObjectInputStream in = new ObjectInputStream(file)) {
            object1 = (T) in.readObject();
        }

        return object1;
    }
}
