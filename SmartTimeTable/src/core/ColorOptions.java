/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Regateiro
 */
public class ColorOptions implements Serializable {

    private static final long serialVersionUID = -4837462587349867095L;
    private Colors all;
    private Map<String, Colors> colorMap;
    private ColorMode colormode;

    public ColorOptions() {
        this.all = new Colors();
        this.colorMap = new HashMap<String, Colors>();
        this.colormode = ColorMode.ABSOLUTE;
    }

    public ColorOptions(ColorOptions colorOptions) {
        this.all = new Colors(colorOptions.all);
        this.colormode = colorOptions.colormode;
        this.colorMap = new HashMap<String, Colors>();

        Iterator<Entry<String, Colors>> itr = colorOptions.colorMap.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<String, Colors> entry = itr.next();
            this.colorMap.put(entry.getKey(), new Colors(entry.getValue()));
        }
    }

    public void setColor(String cadeira, Componente comp, Color c) {
        if (colormode == ColorMode.RELATIVE) {
            colorMap.get(cadeira).setColor(comp, c);
        } else {
            all.setColor(comp, c);
        }
    }

    public Color getColor(String cadeira, Componente comp) {
        if (colormode == ColorMode.RELATIVE) {
            return colorMap.get(cadeira).getColor(comp);
        }

        return all.getColor(comp);
    }

    public void setColorMode(ColorMode mode) {
        this.colormode = mode;
    }

    public void addCadeira(String cadeira) {
        colorMap.put(cadeira, new Colors());
    }

    public void removeCadeira(String cadeira) {
        colorMap.remove(cadeira);
    }

    public ColorMode getColorMode() {
        return this.colormode;
    }

    public String toXML() {
        String out = "";

        out += "\t<colormode>" + colormode.toString() + "</colormode>\n";

        out += "\t<allcolors>\n";
        out += all.toXML();
        out += "\t</allcolors>\n";
        
        
        for (Entry<String, Colors> entry : colorMap.entrySet()) {
            out += "\t<colormapping>\n";
            out += "\t\t<subject>" + entry.getKey() + "</subject>\n";
            out += entry.getValue().toXML();
            out += "\t</colormapping>\n";
        }

        return out;
    }
}
