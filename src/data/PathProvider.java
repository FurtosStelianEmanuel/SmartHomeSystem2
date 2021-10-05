/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import annotations.Injectable;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Manel
 */
@Injectable
public class PathProvider {

    private final Path currentWorkingDirectory;

    public PathProvider() {
        currentWorkingDirectory = Paths.get(System.getProperty("user.dir"));
    }

    public String getCurrentWorkingDirectory() {
        return currentWorkingDirectory.toString();
    }
}
