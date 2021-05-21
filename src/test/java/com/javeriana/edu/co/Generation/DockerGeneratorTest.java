/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co.Generation;

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
public class DockerGeneratorTest {

    private DockerGenerator dockerGenerator;

    public DockerGeneratorTest() {
        dockerGenerator = new DockerGenerator();
    }

    @Test
    @DisplayName("CP07")
    public void testWrite() {
        String path = System.getProperty("user.dir") + File.separator + "tests" + File.separator + "test3.txt";
        String line = "";
        ArrayList<String> list = new ArrayList<>();
        list.add("hola");
        list.add("hola2");
        File fileP = new File(path);
        
        if(fileP.exists()) {
            fileP.delete();
        }
        
        dockerGenerator.write(fileP, list);
        boolean sw = true;
        int i = 0;

        try {                                    
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                line = myReader.nextLine();
                if (!line.equals(list.get(i))) {
                    sw = false;
                }
                i++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        assertEquals(true, sw);
    }
}
