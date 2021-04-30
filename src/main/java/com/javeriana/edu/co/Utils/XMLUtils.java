package com.javeriana.edu.co.Utils;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility class to manipulate XML Files.
 *
 * Nota: Por simplicidad, todos los métodos están capturando sus respectivas
 * excepciones. Si necesitan obtener las excepciones por fuera de esta clase,
 * modifiquen esta clase como estimen necesario.
 *
 * @author jpavlich
 *
 */
public class XMLUtils {

    private XPath xpath;
    private Transformer xformer;
    private DocumentBuilder docBuilder;

    public XMLUtils() {
        try {
            xpath = XPathFactory.newInstance().newXPath();
            xformer = TransformerFactory.newInstance().newTransformer();
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens an XML file
     *
     * @param xmlFile The file to open
     * @return an instance of {@link Document} representing the xml file
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public Document openXMLFile(String xmlFile) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(xmlFile));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Read all nodes that match a given xpath expression
     *
     * @param doc The {@link Document} where we want to eliminate nodes
     * @param xpathExpression An XPath expression denoting all of the nodes that
     * we want to remove
     * @return an instance of {@link ArrayList<Node>} representing all nodes that match a given xpath expression
     * @throws XPathExpressionException
     * @throws DOMException
     */
    public ArrayList<Node> readXMLNodes(Document doc, String xpathExpression) {
        ArrayList<Node> nodesStr = new ArrayList<>();
        try {
            NodeList nodes = (NodeList) xpath.evaluate(xpathExpression, doc,
                    XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); i++) {
                nodesStr.add(nodes.item(i));
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (DOMException e) {
            e.printStackTrace();
        }
        return nodesStr;
    }

    /**
     * Removes all nodes that match a given xpath expression
     *
     * @param doc The {@link Document} where we want to eliminate nodes
     * @param xpathExpression An XPath expression denoting all of the nodes that
     * we want to remove
     * @throws XPathExpressionException
     */
    public void removeNodes(Document doc, String xpathExpression) {
        try {
            NodeList nodes = (NodeList) xpath.evaluate(xpathExpression, doc,
                    XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); i++) {
                nodes.item(i).getParentNode().removeChild(nodes.item(i));
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (DOMException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts a new node into an xml {@link Document}. To identify the parent
     * node, an xpath expression is used. The node is inserted into the first
     * node that satisfies the xpath expression.
     *
     * @param doc The {@link Document}
     * @param parentXpathExpression An xpath expression that represents the
     * parent node
     * @param node The xml string representing the node to insert into the
     * parent node
     */
    public void insertNode(Document doc, String parentXpathExpression, Node node) {
        try {
            NodeList nodes = (NodeList) xpath.evaluate(parentXpathExpression, doc,
                    XPathConstants.NODESET);
            if (nodes.getLength() > 0) {
                //Element elem = docBuilder.parse(new ByteArrayInputStream(node.getBytes())).getDocumentElement();

                nodes.item(0).appendChild(doc.importNode(node, true));
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (DOMException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a document to an xml file
     * 
     * @param doc The {@link Document} to save
     * @param outputFile The file to write
     * @throws TransformerConfigurationException
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     */
    public void saveXML(Document doc, String outputFile) {
        try {
            xformer.transform(new DOMSource(doc), new StreamResult(new File(
                    outputFile)));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
