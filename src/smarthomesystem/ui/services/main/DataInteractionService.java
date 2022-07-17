/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.ui.services.main;

/**
 *
 * @author Manel
 */
public interface DataInteractionService {

    void save() throws DataHandlingException;

    void cancel();

    void saveAndExit() throws DataHandlingException;

    void add();
    
    void edit();

    void delete(String identifier);
}
