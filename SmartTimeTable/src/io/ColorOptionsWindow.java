/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import core.ColorMode;
import core.ColorOptions;
import core.Componente;
import core.TimeTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Regateiro
 */
public class ColorOptionsWindow extends JFrame implements ActionListener {

    private TimeTable tt;
    private JPanel central, bpanel, wpanel;
    private JLabel tlabel, tplabel, plabel, otlabel, warning;
    private JButton tbutton, tpbutton, pbutton, otbutton, cancel, ok;
    private JLabel warning2;
    private JLabel warning3;
    private JCheckBox cbox;
    private JComboBox cadeiras;
    private final Interface parent;
    private ColorOptions temp;

    public ColorOptionsWindow(final Interface parent, TimeTable tt) {
        super("Color Options");
        parent.setEnabled(false);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parent.setEnabled(true);
            }
        });

        this.parent = parent;
        this.tt = tt;
        this.temp = new ColorOptions(tt.getColorOptions());

        cbox = new JCheckBox("Apply colors per subject: ");
        cbox.setSelected(tt.getColorOptions().getColorMode() == ColorMode.RELATIVE);

        cadeiras = new JComboBox(tt.cadeirasToArray());
        if (cadeiras.getItemCount() == 0) {
            cbox.setSelected(false);
            cbox.setEnabled(false);
            cadeiras.setEnabled(false);
        } else {
            cadeiras.setEnabled(cbox.isSelected());
        }

        buildWindow();

        this.pack();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    private void buildWindow() {
        this.setLayout(new BorderLayout());

        cbox.addActionListener(this);
        cadeiras.addActionListener(this);

        warning = new JLabel("This window allows you to change the color of the "
                + "cells on each timetable.");
        warning2 = new JLabel("Also, keep in mind that only 100% opacity is permitted.");
        warning3 = new JLabel("So the color might be different than the one you chose.");

        tlabel = new JLabel("Color of cells with T classes");
        if (cbox.isSelected()) {
            tlabel.setBackground(tt.getColorOptions().getColor((String) cadeiras.getSelectedItem(), Componente.T));
        } else {
            tlabel.setBackground(tt.getColorOptions().getColor(null, Componente.T));
        }
        tlabel.setOpaque(true);
        tlabel.setBorder(BorderFactory.createLineBorder(Color.black));
        tbutton = new JButton("Change Color");
        tbutton.addActionListener(this);

        tplabel = new JLabel("Color of cells with TP classes");
        if (cbox.isSelected()) {
            tplabel.setBackground(tt.getColorOptions().getColor((String) cadeiras.getSelectedItem(), Componente.TP));
        } else {
            tplabel.setBackground(tt.getColorOptions().getColor(null, Componente.TP));
        }
        tplabel.setOpaque(true);
        tplabel.setBorder(BorderFactory.createLineBorder(Color.black));
        tpbutton = new JButton("Change Color");
        tpbutton.addActionListener(this);

        plabel = new JLabel("Color of cells with P classes");
        if (cbox.isSelected()) {
            plabel.setBackground(tt.getColorOptions().getColor((String) cadeiras.getSelectedItem(), Componente.P));
        } else {
            plabel.setBackground(tt.getColorOptions().getColor(null, Componente.P));
        }
        plabel.setOpaque(true);
        plabel.setBorder(BorderFactory.createLineBorder(Color.black));
        pbutton = new JButton("Change Color");
        pbutton.addActionListener(this);

        otlabel = new JLabel("Color of cells with OT classes");
        if (cbox.isSelected()) {
            otlabel.setBackground(tt.getColorOptions().getColor((String) cadeiras.getSelectedItem(), Componente.OT));
        } else {
            otlabel.setBackground(tt.getColorOptions().getColor(null, Componente.OT));
        }
        otlabel.setOpaque(true);
        otlabel.setBorder(BorderFactory.createLineBorder(Color.black));
        otbutton = new JButton("Change Color");
        otbutton.addActionListener(this);

        cancel = new JButton("Cancel");
        cancel.addActionListener(this);

        ok = new JButton("OK");
        ok.addActionListener(this);

        central = new JPanel(new MigLayout("fill, ins 20 0 20 0"));
        bpanel = new JPanel(new MigLayout("rtl"));

        central.add(tlabel, "ax center");
        central.add(tplabel, "ax center");

        central.add(tbutton, "newline, ax center");
        central.add(tpbutton, "ax center");

        central.add(plabel, "newline, ax center, gaptop 10");
        central.add(otlabel, "ax center, gaptop 10");

        central.add(pbutton, "newline, ax center");
        central.add(otbutton, "ax center");

        bpanel.add(ok, "tag ok");
        bpanel.add(cancel, "tag cancel");

        wpanel = new JPanel(new MigLayout("fill"));
        wpanel.add(warning, "wrap");
        wpanel.add(warning2, "wrap");
        wpanel.add(warning3, "wrap");
        wpanel.add(cbox, "gap unrel, gaptop unrel, id cbox");
        wpanel.add(cadeiras, "pos (cbox.x+cbox.w) cbox.y");

        this.add(wpanel, BorderLayout.NORTH);
        this.add(central, BorderLayout.CENTER);
        this.add(bpanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cbox) {
            String cadeira = cbox.isSelected() ? (String) cadeiras.getSelectedItem() : null;

            if (cbox.isSelected()) {
                this.temp.setColorMode(ColorMode.RELATIVE);
                this.cadeiras.setEnabled(true);
            } else {
                this.temp.setColorMode(ColorMode.ABSOLUTE);
                this.cadeiras.setEnabled(false);
            }

            tlabel.setBackground(temp.getColor(cadeira, Componente.T));
            tplabel.setBackground(temp.getColor(cadeira, Componente.TP));
            plabel.setBackground(temp.getColor(cadeira, Componente.P));
            otlabel.setBackground(temp.getColor(cadeira, Componente.OT));

            this.repaint();
        } else if (e.getSource() == cadeiras) {
            String cadeira = cbox.isSelected() ? (String) cadeiras.getSelectedItem() : null;
            
            tlabel.setBackground(temp.getColor(cadeira, Componente.T));
            tplabel.setBackground(temp.getColor(cadeira, Componente.TP));
            plabel.setBackground(temp.getColor(cadeira, Componente.P));
            otlabel.setBackground(temp.getColor(cadeira, Componente.OT));

            this.repaint();
        } else if (e.getSource() == tbutton) {
            String cadeira = cbox.isSelected() ? (String) cadeiras.getSelectedItem() : null;
            Color c = JColorChooser.showDialog(null, "T Class Color", temp.getColor(cadeira, Componente.T));

            if (c != null) {
                temp.setColor(cadeira, Componente.T, new Color(c.getRGB() & 0xffffff));
                tlabel.setBackground(temp.getColor(cadeira, Componente.T));
                tlabel.repaint();
            }
        } else if (e.getSource() == tpbutton) {
            String cadeira = cbox.isSelected() ? (String) cadeiras.getSelectedItem() : null;
            Color c = JColorChooser.showDialog(null, "TP Class Color", temp.getColor(cadeira, Componente.TP));

            if (c != null) {
                temp.setColor(cadeira, Componente.TP, new Color(c.getRGB() & 0xffffff));
                tplabel.setBackground(temp.getColor(cadeira, Componente.TP));
                tplabel.repaint();
            }
        } else if (e.getSource() == pbutton) {
            String cadeira = cbox.isSelected() ? (String) cadeiras.getSelectedItem() : null;
            Color c = JColorChooser.showDialog(null, "P Class Color", temp.getColor(cadeira, Componente.P));

            if (c != null) {
                temp.setColor(cadeira, Componente.P, new Color(c.getRGB() & 0xffffff));
                plabel.setBackground(temp.getColor(cadeira, Componente.P));
                plabel.repaint();
            }
        } else if (e.getSource() == otbutton) {
            String cadeira = cbox.isSelected() ? (String) cadeiras.getSelectedItem() : null;
            Color c = JColorChooser.showDialog(null, "OT Class Color", temp.getColor(cadeira, Componente.OT));

            if (c != null) {
                temp.setColor(cadeira, Componente.OT, new Color(c.getRGB() & 0xffffff));
                otlabel.setBackground(temp.getColor(cadeira, Componente.OT));
                otlabel.repaint();
            }
        } else if (e.getSource() == cancel) {
            WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
        } else if (e.getSource() == ok) {
            tt.setColorOptions(temp);
            parent.setChanged();

            WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
        }
    }
}
