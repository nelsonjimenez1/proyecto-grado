
package com.javeriana.edu.co.Creation;

import com.javeriana.edu.co.Generation.JavaGeneratorWeb;
import com.javeriana.edu.co.Graph.Graph;
import com.javeriana.edu.co.Utils.FileUtilsProject;
import java.io.File;


public class CreateProjectMicroWeb {

    private Graph graph;   
    private FileUtilsProject utils;
    private JavaGeneratorWeb generator;

    public CreateProjectMicroWeb(Graph graph) {
        this.graph = graph;
        this.utils = new FileUtilsProject();
        this.generator = new JavaGeneratorWeb(graph);
        copyFolder();
        this.generator.generateServices();
        this.generator.generateControllers();
        this.generator.moveEntities();
    }

    private void copyFolder() {
        String[] split = {System.getProperty("user.dir"), "templates", "microservices-web"};
        String path = String.join(File.separator, split);
        String[] splitMicro = {System.getProperty("user.dir"), "output"};
        String pathMicro = String.join(File.separator, splitMicro);
        this.utils.copyAnotherDirectory(path, pathMicro);
        System.out.println("--------------------------------------");
        System.out.println("microservices-web construction finished");
        System.out.println("--------------------------------------");
    }  
}
