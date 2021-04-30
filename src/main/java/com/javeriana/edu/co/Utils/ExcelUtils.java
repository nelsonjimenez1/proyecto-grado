package com.javeriana.edu.co.Utils;

import com.javeriana.edu.co.Graph.Edge;
import com.javeriana.edu.co.Graph.Vertex;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 * This class creates and allows you to manipulate the Excel files
 * @author Nelson David Jimenez Ortiz
 * @author Santos David Nuñez Villamil
 * @author Juan Sebastián Prado Valero
 * @author Gustavo Antonio Rivera Delgado
 */
public class ExcelUtils {

    /**
     * Allows you to load the nodes by reading an excel file, 
     * where the specifications for the network are found.
     * @param nodes an empty hashmap to be filled with the nodes found in the excel file
     */
    public void loadNodes(HashMap<String, Vertex> nodes) {
        String[] split = {System.getProperty("user.dir"), "input", "graph.xlsx"};
        String filename = String.join(File.separator, split);

        try {
            FileInputStream fis = new FileInputStream(filename);
            XSSFWorkbook workBook = new XSSFWorkbook(fis);
            XSSFSheet hssfsheetNodes = workBook.getSheetAt(0);
            Iterator rowIterator = hssfsheetNodes.rowIterator();
            int row = 0;

            while (rowIterator.hasNext()) {

                XSSFRow hssfRow = (XSSFRow) rowIterator.next();

                if (row == 0) {
                    row = 1;
                    continue;
                }

                Iterator it = hssfRow.cellIterator();
                Vertex node = new Vertex();

                int cell = 0;

                while (it.hasNext()) {
                    XSSFCell hssfCell = (XSSFCell) it.next();

                    switch (cell) {
                        case 0:
                            node.setId(hssfCell.toString());
                            break;
                        case 1:
                            node.setPackageName(hssfCell.toString());
                            break;
                        case 2:
                            node.setName(hssfCell.toString());
                            break;
                        case 3:
                            node.setLabel(hssfCell.toString());
                            break;
                        case 4:
                            node.setType(hssfCell.toString());
                            break;
                        case 5:
                            node.setSubType(hssfCell.toString());
                            break;
                        case 6:
                            node.setMicroservice(hssfCell.toString());
                            break;
                    }
                    cell++;
                }
                nodes.put(node.getId(), node);
            }
            System.out.println("--------------------------------------");
            System.out.println("graph nodes reading completed");        
            System.out.println("--------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Allows the edges to be loaded by reading an excel file, 
     * where the specifications for the network are found.
     * @param edges an empty hashmap to be filled with the edges found in the excel file
     */
    public void loadConnections(HashMap<String, ArrayList<Edge>> edges) {
        String[] split = {System.getProperty("user.dir"), "input", "graph.xlsx"};
        String filename = String.join(File.separator, split);

        try {
            FileInputStream fis = new FileInputStream(filename);
            XSSFWorkbook workBook = new XSSFWorkbook(fis);
            XSSFSheet hssfsheetNodes = workBook.getSheetAt(1);
            Iterator rowIterator = hssfsheetNodes.rowIterator();
            int row = 0;

            while (rowIterator.hasNext()) {

                XSSFRow hssfRow = (XSSFRow) rowIterator.next();

                if (row == 0) {
                    row = 1;
                    continue;
                }

                Iterator it = hssfRow.cellIterator();
                Edge edge = new Edge();

                int cell = 0;

                while (it.hasNext()) {
                    XSSFCell hssfCell = (XSSFCell) it.next();

                    switch (cell) {
                        case 0:
                            edge.setIdSrc(hssfCell.toString());
                            break;
                        case 1:
                            edge.setIdDest(hssfCell.toString());
                            break;
                        case 2:
                            edge.setTypeRelation(hssfCell.toString());
                            break;
                        case 3:
                            edge.setLabel(hssfCell.toString());
                            break;
                    }
                    cell++;
                }
                if (edges.get(edge.getIdSrc()) == null) {
                    edges.put(edge.getIdSrc(), new ArrayList<>());
                }
                edges.get(edge.getIdSrc()).add(edge);
            }
            System.out.println("--------------------------------------");
            System.out.println("graph connections reading completed");   
            System.out.println("--------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
