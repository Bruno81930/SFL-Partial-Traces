package org.diagnosis.algorithms.parser;

import org.diagnosis.algorithms.entities.Hit;
import org.diagnosis.algorithms.entities.HitVector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class ExecutionsParser {

    public HitVector parse(String path) {
        HitVector hitVector = new HitVector();

        try {
            File file = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true); // Enable namespace awareness
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            dbFactory.setFeature("http://xml.org/sax/features/validation", false);
            dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList packageList = doc.getElementsByTagName("package");

            for (int i = 0; i < packageList.getLength(); i++) {
                Node packageNode = packageList.item(i);
                if (packageNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element packageElement = (Element) packageNode;
                    String packageName = packageElement.getAttribute("name");

                    NodeList classList = packageElement.getElementsByTagName("class");
                    for (int j = 0; j < classList.getLength(); j++) {
                        Node classNode = classList.item(j);
                        if (classNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element classElement = (Element) classNode;
                            String className = classElement.getAttribute("name");

                            NodeList methodList = classElement.getElementsByTagName("method");
                            for (int k = 0; k < methodList.getLength(); k++) {
                                Node methodNode = methodList.item(k);
                                if (methodNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element methodElement = (Element) methodNode;
                                    String methodName = methodElement.getAttribute("name");

                                    NodeList lineList = methodElement.getElementsByTagName("line");
                                    int numberOfHits = 0;
                                    for (int l = 0; l < lineList.getLength(); l++) {
                                        Element lineElement = (Element) lineList.item(l);
                                        numberOfHits += Integer.parseInt(lineElement.getAttribute("hits"));
                                    }

                                    className = className.split("\\.")[className.split("\\.").length - 1];

                                    methodName = methodName.replace("<init>", "new");

                                    hitVector.add(new Hit(packageName, className, methodName, numberOfHits));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hitVector;
    }

}
