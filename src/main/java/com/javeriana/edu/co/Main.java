package com.javeriana.edu.co;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.visitor.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.resolution.types.ResolvedType;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.regex.*;

public class Main {

  private static final String FILE_PATH = "C:/Users/nelso/Downloads/javaparser-maven-sample-master/src/main/resources/ExampleA.java";
  public static String fileSeparator = File.separator;
 
  
  public static void main(String[] args) throws Exception {
    
    //cambiar ruta
    //String borrame = "C:\\Tools\\spring-petclinic-master"; //Gustavo
    String borrame = "C:\\Users\\prado\\OneDrive\\Documentos\\TG\\spring-petclinic-master"; //Sebastián
   
    if(/*args.length > 0*/ true) {

        Properties properties= new Properties();
        try {
            File f = new File(System.getProperty("user.dir")+fileSeparator+"configuracion.properties");
            properties.load(new FileInputStream(f));     
            properties.setProperty("INPUTPATH", borrame);
            properties.store(new FileOutputStream(f), null);
            //if(args[0] != null)                
                //properties.setProperty("INPUTPATH", args[0]);
        } catch (Exception e) {
            System.out.println(e.getMessage());   
        }
    }
    
    Controller c = new Controller();
    System.out.println("ToyFuncionando guiño guiño");    
  }

  private static class MethodNamePrinter extends VoidVisitorAdapter<Void> {
    @Override
    public void visit(MethodDeclaration md, Void arg) {
      //necesario para visitar todos los nodos hijos
      super.visit(md, arg);
      System.out.println("Method Name Printed: " + md.getName());
    }
  }

  private static class MethodNameCollector extends VoidVisitorAdapter<List<String>> {
    @Override
    public void visit(MethodDeclaration md, List<String> collector) {
      //necesario para visitar todos los nodos hijos
      super.visit(md, collector);
      collector.add(md.getNameAsString());
    }
  }

  private static class IntegerLiteralModifier extends ModifierVisitor<Void> {

    private static final Pattern LOOK_AHEAD_THREE = Pattern.compile("(\\d)(?=(\\d{3})+$)");

    @Override
    public FieldDeclaration visit(FieldDeclaration fd, Void arg) {
      //necesario para visitar todos los nodos hijos
      super.visit(fd, arg);
      fd.getVariables().forEach(v ->
        v.getInitializer().ifPresent(i ->
          i.ifIntegerLiteralExpr(il ->
            v.setInitializer(formatWithUnderscores(il.getValue()))
          )
        )
      );
      return fd;
    }

    static String formatWithUnderscores(String value) {
        String withoutUnderscores = value.replaceAll("_", "");
        return LOOK_AHEAD_THREE.matcher(withoutUnderscores).replaceAll("$1_");
    }
  }

  private static class CommentReportEntry {
    private String type;
    private String text;
    private int lineNumber;
    private boolean isOrphan;

    CommentReportEntry(String type, String text, int lineNumber, boolean isOrphan) {
      this.type = type;
      this.text = text;
      this.lineNumber = lineNumber;
      this.isOrphan = isOrphan;
    }

    @Override
    public String toString() {
      return lineNumber + "|" + type + "|" + isOrphan + "|" + text.replaceAll("\\n","").trim();
    }
  }

  private static void getTypeOfReference() throws FileNotFoundException {
    String file_path = "C:/Users/nelso/Downloads/javaparser-maven-sample-master/src/main/resources/Bar.java";

    TypeSolver typeSolver = new CombinedTypeSolver();
    JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
    StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
    CompilationUnit cu = StaticJavaParser.parse(new File(file_path));
    cu.findAll(AssignExpr.class).forEach(ae -> {
      ResolvedType resolvedType = ae.calculateResolvedType();
      System.out.println(ae.toString() + " is a: " + resolvedType);
    });
  }
}
