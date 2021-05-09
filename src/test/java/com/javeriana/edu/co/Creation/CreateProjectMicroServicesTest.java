/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co.Creation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 * @author nelso
 */
public class CreateProjectMicroServicesTest {

    private CreateProjectMicroServices createProjectMicroServices;

    public CreateProjectMicroServicesTest() {
        createProjectMicroServices = new CreateProjectMicroServices();
    }

    @Test
    @DisplayName("CP02")
    public void testListDirectory() {
        ArrayList<File> listDirectory = createProjectMicroServices.listDirectory(System.getProperty("user.dir") + File.separator + "src");
        String[] array = {"main", "test"};
        int i = 0;
        boolean sw = true;
        for (File file : listDirectory) {
            if (i == 0) {
                if (!file.getName().equals(array[i])) {
                    sw = false;
                }
            } else if (i == 1) {
                if (!file.getName().equals(array[i])) {
                    sw = false;
                }
            }
            i++;
        }

        assertEquals(true, sw);
    }

    @Test
    @DisplayName("CP03")
    public void testCreateFiles() {
        ArrayList<String> rootList = new ArrayList<>();
        String path1 = System.getProperty("user.dir") + File.separator + "tests" + File.separator + "test1.txt";
        String path2 = System.getProperty("user.dir") + File.separator + "tests" + File.separator + "test2.txt";
        rootList.add(path1);
        rootList.add(path2);
        createProjectMicroServices.createFiles(rootList);
        File test1 = new File(path1);
        File test2 = new File(path2);
        boolean sw = false;
        if (test1.exists() && test2.exists()) {
            sw = true;
        }
        assertEquals(true, sw);
    }

    @Test
    @DisplayName("CP04")
    public void testConcatV() {
        String[] array1 = {"left", "left"};
        String[] array2 = {"rigth", "right"};
        String[] concat = createProjectMicroServices.concatV(array1, array2);
        boolean sw = true;
        for (int i = 0; i < concat.length; i++) {
            if (i < 2) {
                if (!array1[i].equals(concat[i])) {
                    sw = false;
                }
            } else {
                if (!array2[i-2].equals(concat[i])) {
                    sw = false;
                }
            }

        }

        assertEquals(true, sw);
    }

    @Test
    @DisplayName("CP05")
    public void testWriteFile() {
        String path = System.getProperty("user.dir") + File.separator + "tests" + File.separator + "test1.txt";
        String line = "";
        createProjectMicroServices.writeFile(new File(path), "hola");

        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                line = myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        
        boolean sw = false;
        if (line.equals("hola")) {
            sw = true;
        }
        
        assertEquals(true, sw);
    }
}
