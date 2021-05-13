package com.javeriana.edu.co.Creation;

import com.javeriana.edu.co.Generation.JavaGeneratorWeb;
import com.javeriana.edu.co.Graph.Graph;
import com.javeriana.edu.co.Utils.FileUtilsProject;
import java.io.File;

/**
 * This class creates the microservices-web with the necessary files generating
 * and copying
 *
 * @author Nelson David Jimenez Ortiz
 * @author Santos David Nuñez Villamil
 * @author Juan Sebastián Prado Valero
 * @author Gustavo Antonio Rivera Delgado
 */
public class CreateProjectMicroWeb {

    private Graph graph;
    private FileUtilsProject utils;
    private JavaGeneratorWeb generator;

    /**
     * Contructor
     */
    public CreateProjectMicroWeb() {
        this.utils = new FileUtilsProject();
    }

    /**
     * Contructor in this constructor, class attributes are initialized and
     * function calls are executed to move Entities and the services and
     * Controllers generation
     *
     * @param graph: class with the Graph information
     */
    public CreateProjectMicroWeb(Graph graph) {
        this.graph = graph;
        this.utils = new FileUtilsProject();
        this.generator = new JavaGeneratorWeb(graph);
        copyFolder();
        this.generator.generateServices();
        this.generator.generateControllers();
        this.generator.moveEntities();

        System.out.println("--------------------------------------");
        System.out.println("microservices-web construction finished");
        System.out.println("--------------------------------------");

    }

    /**
     * Allows copying files and folders from the web template
     */
    public void copyFolder() {
        String[] split = {System.getProperty("user.dir"), "templates", "microservices-web"};
        String path = String.join(File.separator, split);
        String[] splitMicro = {System.getProperty("user.dir"), "output"};
        String pathMicro = String.join(File.separator, splitMicro);
        this.utils.copyAnotherDirectory(path, pathMicro);
    }
}
