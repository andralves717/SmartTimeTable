/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.awt.Color;
import java.io.Serializable;

/**
 *
 * @author Regateiro
 */
public class Colors implements Serializable {
    private static final long serialVersionUID = -3987490583729387429L;
    private Color p_color, tp_color, t_color, ot_color;
    
    public Colors() {
        this.p_color = new Color(255, 255, 255);
        this.tp_color = new Color(255, 255, 255);
        this.t_color = new Color(255, 255, 255);
        this.ot_color = new Color(255, 255, 255);
    }

    public Colors(Colors c) {
        this.p_color = new Color(c.p_color.getRGB());
        this.tp_color = new Color(c.tp_color.getRGB());
        this.t_color = new Color(c.t_color.getRGB());
        this.ot_color = new Color(c.ot_color.getRGB());
    }
    
    public void setColor(Componente comp, Color c) {
        switch (comp) {
            case P:
                p_color = c;
                break;
            case TP:
                tp_color = c;
                break;
            case T:
                t_color = c;
                break;
            case OT:
                ot_color = c;
        }
    }

    public Color getColor(Componente comp) {
        switch (comp) {
            case P:
                return p_color;
            case TP:
                return tp_color;
            case T:
                return t_color;
            case OT:
                return ot_color;
        }

        return null; //This CAN'T happen
    }
    
    public String toXML() {
        String out = "";
        
        out += "\t\t<t>" + this.t_color.getRGB() + "</t>\n";
        out += "\t\t<tp>" + this.tp_color.getRGB() + "</tp>\n";
        out += "\t\t<p>" + this.p_color.getRGB() + "</p>\n";
        out += "\t\t<ot>" + this.ot_color.getRGB() + "</ot>\n";
        
        return out;
    }
}