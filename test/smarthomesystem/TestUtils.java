/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem;

/**
 *
 * @author Manel
 */
public class TestUtils {

    public String expectedErrorShouldHaveOccured(Class expectedException) {
        return String.format("An expected %s should have been thrown during a test", expectedException.getName());
    }

    public String unexpectedError(Exception ex) {
        return String.format("An unexpected error was thrown during a test : %s -> %s", ex.getClass().getName(), ex.getMessage());
    }
}
