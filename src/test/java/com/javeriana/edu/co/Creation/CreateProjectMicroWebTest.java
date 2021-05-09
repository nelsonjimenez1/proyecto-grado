/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co.Creation;

import java.io.File;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 * @author nelso
 */
public class CreateProjectMicroWebTest {
    private CreateProjectMicroWeb createProjectMicroWeb;

    public CreateProjectMicroWebTest() {
        createProjectMicroWeb = new CreateProjectMicroWeb();
    }
    
    @Test
    @DisplayName("")
    public void testCopyFolder() {
        createProjectMicroWeb.copyFolder();
        assertEquals(true, new File("output","microservices-web").exists());
    }
}
