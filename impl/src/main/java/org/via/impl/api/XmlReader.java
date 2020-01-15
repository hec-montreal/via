package org.via.impl.api;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import java.io.StringWriter;

import java.net.URL;
import java.net.URLConnection;


import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlReader 
{
    private Document xmlDocument;
    private XPath xPath;
    private String mainNode = "";
    private InputStream xmlIs;
    private boolean success = false;
    private String filePath = "";
    private boolean traceErrorNodeNotFound = true;

    public String FilePath() {
        return filePath;
    }


    public XmlReader(InputStream input, String MainNode) {
        this.xmlIs = input;
        mainNode = MainNode;
        initObjectsStream();
    }


    public XmlReader(URL url, String MainNode) {
        mainNode = MainNode;
        initObjectsUrl(url);
    }

    /*public XmlReader(String xml) {
        log.debug("Load " + xml);
        filePath = xml.toLowerCase();
        initObjectString(xml);
    }*/

    public void SetMainNode(String MainNode) {
        mainNode = MainNode;
    }

    public void SetTraceErrorNodeNotFound(boolean traceErrorNodeNotFound) {
        this.traceErrorNodeNotFound = traceErrorNodeNotFound;
    }

    public boolean isCreate() {
        return success;
    }

    public void Clear() {
        xmlDocument = null;
        xmlIs = null;
        xPath = null;
    }


    /* private void initObjectString(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            setFactorySettings(factory);
            DocumentBuilder builder = factory.newDocumentBuilder();
            xmlDocument = builder.parse(new File(xml));
            //xmlDocument = builder.parse(new InputSource(new StringReader(xml)));
            xPath = XPathFactory.newInstance().newXPath();
            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/

    private void initObjectsUrl(URL url) {
        try {
            URLConnection conn = url.openConnection();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            setFactorySettings(factory);
            DocumentBuilder builder = factory.newDocumentBuilder();
            xmlDocument = builder.parse(conn.getInputStream());
            xmlDocument.getDocumentElement().normalize();
            xPath = XPathFactory.newInstance().newXPath();
            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void setFactorySettings(DocumentBuilderFactory factory) {
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
    }

    private void initObjectsStream() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            setFactorySettings(factory);
            DocumentBuilder builder = factory.newDocumentBuilder();
            xmlDocument = builder.parse(xmlIs);
            xmlDocument.getDocumentElement().normalize();
            xPath = XPathFactory.newInstance().newXPath();
            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public String ReadString(String expression) {
        String var = read(expression, XPathConstants.STRING).toString();
        
        return var.toString();

    }

    public NodeList GetMainNodeList() {
        if (xmlDocument != null)
            return xmlDocument.getChildNodes();
        else
            return null;
    }

    public NodeList ReadNodeList(String expression) {
        Object var = read(expression, XPathConstants.NODESET);
        if (var != null) {
            return (NodeList)var;
        } else {
            return null;
        }
    }

    public Node ReadNode(String expression) {
        Object var = read(expression, XPathConstants.NODE);
        if (var != null) {
            return (Node)var;
        } else {
            return null;
        }
    }

    public String ReadNodeAtt(String expression, String attName) {
        Node myNode = ReadNode(expression);
        return ReadNodeAttribute(myNode, attName);
    }

    public String GetStringNode(String expression) {
        Node n = ReadNode(expression);
        if (n != null) {

            if (n.hasChildNodes()) {
                NodeList nl = n.getChildNodes();
                if (nl.getLength() == 1) {
                    Node nChild = nl.item(0);
                    if (nChild.getNodeType() == Node.TEXT_NODE) {
                        n = null;
                        return nChild.getNodeValue();
                    }
                }

            }
        }
        n = null;
        return "";
    }


    public static String GetStringNode(Node node,String expression) {
        if (node != null) {

            if (node.hasChildNodes()) {
                NodeList nl = node.getChildNodes();
                if (nl.getLength() > 0) {
                    for(int i = 0; i < nl.getLength(); i++){
                        Node nChild = nl.item(i);
                        if(nChild.getNodeType() == Node.ELEMENT_NODE && nChild.getNodeName().equals(expression)){
                            return nChild.getTextContent();
                        }
                    }                
                }

            }
        }
        node = null;
        return "";
    }
    

    public String GetInnerXmlNode(String expression) {
        Node n = ReadNode(expression);
        
        StringBuilder resultBuilder = new StringBuilder();
        // Get all children of the given parent node
        NodeList children = n.getChildNodes();
        try {

            // Set up the output transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter stringWriter = new StringWriter();
            StreamResult streamResult = new StreamResult(stringWriter);
    
            for (int index = 0; index < children.getLength(); index++) {
            Node child = children.item(index);
    
            // Print the DOM node
            DOMSource source = new DOMSource(child);
            trans.transform(source, streamResult);
            // Append child to end result
            resultBuilder.append(stringWriter.toString());
        }
        } catch (Exception e) {
        //Errro handling goes here
        }
        return resultBuilder.toString();
    }

    public String GetOuterXmlNode(String expression) {
        Node n = ReadNode(expression);
        Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(n), new StreamResult(writer));
            return writer.toString(); 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
             
        return "";
    }
    
    public static String ReadNodeAttribute(Node node, String attName) {
        if (node != null) {
            String value = "";

            if (node.hasAttributes()) {
                NamedNodeMap att = node.getAttributes();
                Node myAttNode = att.getNamedItem(attName);

                if (myAttNode != null)
                    value = myAttNode.getNodeValue();
            }

            return value;
        } else {
            return "";
        }
    }
    
    



    private Object read(String expression, QName returnType) {
        try {
            if (xmlDocument == null)
                return null;
            String exp = getExpression(expression);
            XPathExpression xPathExpression = xPath.compile(exp);
            return xPathExpression.evaluate(xmlDocument, returnType);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private String getExpression(String exp) {
        if (mainNode == null || mainNode.equals(""))
            return exp;
        else
            return mainNode + "/" + exp;
    }
}
