/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.xml;

import core.Aula;
import core.ColorMode;
import core.ColorOptions;
import core.Componente;
import core.DiaSemanal;
import core.Hora;
import core.OverlapMode;
import core.TimeTable;
import core.Turma;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author regateiro
 */
public class Parser {

    private Parser() {
    }

    public static boolean exportXML(File f, TimeTable tt) {
        PrintWriter pw = null;
        boolean ret;

        try {
            pw = new PrintWriter(f);
            pw.write("<?xml version=\"1.0\"?>\n");
            pw.write(tt.toXML());
            pw.close();
            ret = true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
            ret = false;
        } finally {
            pw.close();
        }

        return ret;
    }

    public static TimeTable importXML(File f) throws ParserConfigurationException, SAXException, IOException {
        TimeTable tt = new TimeTable();

        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //Using factory get an instance of document builder
        DocumentBuilder db = dbf.newDocumentBuilder();
        //parse using builder to get DOM representation of the XML file
        Document dom = db.parse(f);

        //get the root element
        org.w3c.dom.Element docEle = dom.getDocumentElement();

        //get a nodelist of subjects
        NodeList nl = docEle.getElementsByTagName("subject");
        if (nl != null) {
            //get the element
            for (int i = 0; i < nl.getLength(); i++) {
                org.w3c.dom.Element el = (org.w3c.dom.Element) nl.item(i);
                tt.addCadeira(el.getFirstChild().getNodeValue());
            }
        } else {
            throw new NullPointerException("Error parsing the XML file");
        }

        nl = docEle.getElementsByTagName("class");
        if (nl != null) {
            //get the element
            for (int i = 0; i < nl.getLength(); i++) {
                org.w3c.dom.Element el = (org.w3c.dom.Element) nl.item(i);

                String sub = Parser.getTextValue(el, "subject");
                Componente comp = Componente.valueOf(Parser.getTextValue(el, "comp"));
                String descr = Parser.getTextValue(el, "descr");
                Boolean inc = Boolean.valueOf(Parser.getTextValue(el, "include"));

                if (sub == null || comp == null || descr == null || inc == null) {
                    throw new NullPointerException("Error parsing the XML file");
                }

                Turma t = new Turma(sub, comp, descr);
                t.setInclude(inc);

                // Add lectures
                NodeList lectures = el.getElementsByTagName("lecture");
                for (int j = 0; j < lectures.getLength(); j++) {
                    org.w3c.dom.Element el2 = (org.w3c.dom.Element) lectures.item(j);

                    DiaSemanal ds = DiaSemanal.valueOf(Parser.getTextValue(el2, "day"));
                    Hora inicio = new Hora(Integer.parseInt(Parser.getTextValue(el2, "start")));
                    Hora fim = new Hora(Integer.parseInt(Parser.getTextValue(el2, "finish")));

                    if (ds == null || inicio == null || fim == null) {
                        throw new NullPointerException("Error parsing the XML file");
                    }

                    t.addAula(new Aula(ds, inicio, fim));
                }
                
                tt.addTurma(t);
            }
        } else {
            throw new NullPointerException("Error parsing the XML file");
        }

        ColorOptions co = new ColorOptions();

        nl = docEle.getElementsByTagName("allcolors");
        if (nl != null && nl.getLength() == 1) {
            //get the element
            org.w3c.dom.Element el = (org.w3c.dom.Element) nl.item(0);
            
            co.setColorMode(ColorMode.ABSOLUTE);
            co.setColor(null, Componente.T, new Color(Integer.parseInt(Parser.getTextValue(el, "t"))));
            co.setColor(null, Componente.TP, new Color(Integer.parseInt(Parser.getTextValue(el, "tp"))));
            co.setColor(null, Componente.P, new Color(Integer.parseInt(Parser.getTextValue(el, "p"))));
            co.setColor(null, Componente.OT, new Color(Integer.parseInt(Parser.getTextValue(el, "ot"))));

        } else {
            throw new NullPointerException("Error parsing the XML file");
        }

        nl = docEle.getElementsByTagName("colormapping");
        if (nl != null) {
            //get the element
            co.setColorMode(ColorMode.RELATIVE);
            for (int i = 0; i < nl.getLength(); i++) {
                org.w3c.dom.Element el = (org.w3c.dom.Element) nl.item(i);

                String sub = Parser.getTextValue(el, "subject");

                if (sub == null) {
                    throw new NullPointerException("Error parsing the XML file");
                }

                co.addCadeira(sub);
                co.setColor(sub, Componente.T, new Color(Integer.parseInt(Parser.getTextValue(el, "t"))));
                co.setColor(sub, Componente.TP, new Color(Integer.parseInt(Parser.getTextValue(el, "tp"))));
                co.setColor(sub, Componente.P, new Color(Integer.parseInt(Parser.getTextValue(el, "p"))));
                co.setColor(sub, Componente.OT, new Color(Integer.parseInt(Parser.getTextValue(el, "ot"))));
            }
        } else {
            throw new NullPointerException("Error parsing the XML file");
        }

        nl = docEle.getElementsByTagName("colormode");
        if (nl != null && nl.getLength() == 1) {
            //get the element
            org.w3c.dom.Element el = (org.w3c.dom.Element) nl.item(0);
            co.setColorMode(ColorMode.valueOf(el.getFirstChild().getNodeValue()));
        } else {
            throw new NullPointerException("Error parsing the XML file");
        }

        tt.setColorOptions(co);

        nl = docEle.getElementsByTagName("overlapping");
        if (nl != null && nl.getLength() == 1) {
            //get the element
            org.w3c.dom.Element el = (org.w3c.dom.Element) nl.item(0);

            String s_count = Parser.getTextValue(el, "count");
            String s_time = Parser.getTextValue(el, "time");

            OverlapMode mode = OverlapMode.valueOf(Parser.getTextValue(el, "mode"));
            Integer count = null;
            Hora time = null;

            if (!s_count.equals("null")) {
                count = Integer.parseInt(s_count);
            }

            if (!s_time.equals("null")) {
                time = new Hora(Integer.parseInt(s_time));
            }

            if (mode == null) {
                throw new NullPointerException("Error parsing the XML file");
            }

            tt.setOverlapOptions(mode, count, time);

        } else {
            throw new NullPointerException("Error parsing the XML file");
        }

        return tt;
    }

    private static String getTextValue(org.w3c.dom.Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            org.w3c.dom.Element el = (org.w3c.dom.Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }

        return textVal;
    }
}
