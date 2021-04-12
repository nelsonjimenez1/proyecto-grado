
package com.javeriana.edu.co;

import com.javeriana.edu.co.Utils.FileUtilsProject;


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
        String path = String.join(FileUtilsProject.FILE_SEPARATOR, split);
        String[] splitMicro = {System.getProperty("user.dir"), "output"};
        String pathMicro = String.join(FileUtilsProject.FILE_SEPARATOR, splitMicro);
        this.utils.copyAnotherDirectory(path, pathMicro);
    }
    
}
