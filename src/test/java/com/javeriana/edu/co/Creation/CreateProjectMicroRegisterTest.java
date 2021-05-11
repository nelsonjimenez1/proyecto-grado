/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co.Creation;

import java.io.File;
import org.apache.commons.io.FileUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 * @author nelso
 */
public class CreateProjectMicroRegisterTest {
    
    private CreateProjectMicroRegister createProjectMicroRegister;

    public CreateProjectMicroRegisterTest() {
        createProjectMicroRegister = new CreateProjectMicroRegister();
    }
   
    @Test
    @DisplayName("CP01")
    public void testCopyFolder() {
        createProjectMicroRegister.copyFolder();
        assertEquals(true, new File("output","microservices-register").exists());
    }

}
