/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import core.Hora;
import core.OverlapMode;
import core.TimeTable;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.Number;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Regateiro
 */
public class OverlapOptionsWindow extends JFrame implements ActionListener {

    private final Interface parent;
    private final TimeTable tt;
    private JPanel central, bpanel;
    private ButtonGroup grp;
    private JRadioButton none, partial;
    private JCheckBox number, time;
    private JButton cancel, ok;
    private JSpinner count, hour;
    private JComboBox minutes;

    public OverlapOptionsWindow(final Interface parent, TimeTable tt) {
        super("Overlapping Options");
        parent.setEnabled(false);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parent.setEnabled(true);
            }
        });

        this.parent = parent;
        this.tt = tt;

        buildWindow();

        this.pack();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    private void buildWindow() {
        this.setLayout(new BorderLayout());

        SpinnerModel countmodel = new SpinnerNumberModel(1, 1, 99, 1);
        SpinnerModel hourmodel = new SpinnerNumberModel(0, 0, 23, 1);

        central = new JPanel(new MigLayout());

        central.add(none = new JRadioButton("No Overlapping"), "wrap");
        central.add(partial = new JRadioButton("Limited Overlapping"), "wrap");
        central.add(number = new JCheckBox("Maximum number of overlaps:"), "gap unrel");
        central.add(count = new JSpinner(countmodel), "wrap push");
        central.add(time = new JCheckBox("Maximum overlapping period of time:"), "gap unrel");
        central.add(hour = new JSpinner(hourmodel));
        central.add(minutes = new JComboBox(new String[]{"00", "30"}), "wrap");

        none.addActionListener(this);
        partial.addActionListener(this);
        number.addActionListener(this);
        time.addActionListener(this);

        number.setEnabled(false);
        count.setEnabled(false);
        time.setEnabled(false);
        hour.setEnabled(false);
        minutes.setEnabled(false);

        switch (tt.getOverlapMode()) {
            case NONE:
                none.setSelected(true);
                break;
            case PARTIAL:
                partial.setSelected(true);
                number.setEnabled(true);
                time.setEnabled(true);

                if (tt.getOverlapCount() != null) {
                    number.setSelected(true);
                    count.setEnabled(true);
                    count.setValue(tt.getOverlapCount());
                }

                if (tt.getOverlapTime() != null) {
                    time.setSelected(true);
                    hour.setEnabled(true);
                    minutes.setEnabled(true);
                    hour.setValue(tt.getOverlapTime().getHora());
                    minutes.setSelectedIndex(tt.getOverlapTime().getMinutes() / 30);
                }
        }

        // Colocar os radio buttons no mesmo grupo
        grp = new ButtonGroup();
        grp.add(none);
        grp.add(partial);

        // BUTTONS
        bpanel = new JPanel(new MigLayout("rtl"));

        cancel = new JButton("Cancel");
        cancel.addActionListener(this);

        ok = new JButton("OK");
        ok.addActionListener(this);

        bpanel.add(ok, "tag ok");
        bpanel.add(cancel, "tag cancel");

        // Adicionar tudo à janela
        this.add(central, BorderLayout.CENTER);
        this.add(bpanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == none) {
            number.setEnabled(false);
            count.setEnabled(false);
            time.setEnabled(false);
            hour.setEnabled(false);
            minutes.setEnabled(false);
        } else if (e.getSource() == partial) {
            partial.setSelected(true);
            number.setEnabled(true);
            time.setEnabled(true);

            if (tt.getOverlapCount() != null) {
                number.setSelected(true);
                count.setEnabled(true);
                count.setValue(tt.getOverlapCount());
            }

            if (tt.getOverlapTime() != null) {
                time.setSelected(true);
                hour.setEnabled(true);
                minutes.setEnabled(true);
                hour.setValue(tt.getOverlapTime().getHora());
                minutes.setSelectedIndex(tt.getOverlapTime().getMinutes() / 30);
            }
        } else if (e.getSource() == number) {
            count.setEnabled(number.isSelected());

            if (tt.getOverlapCount() != null) {
                count.setValue(tt.getOverlapCount());
            } else {
                count.setValue(1);
            }
        } else if (e.getSource() == time) {
            hour.setEnabled(time.isSelected());
            minutes.setEnabled(time.isSelected());
            
            if (tt.getOverlapTime() != null) {
                hour.setValue(tt.getOverlapTime().getHora());
                minutes.setSelectedIndex(tt.getOverlapTime().getMinutes() / 30);
            } else {
                hour.setValue(0);
                minutes.setSelectedIndex(1);
            }
        } else if (e.getSource() == cancel) {
            WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
        } else if (e.getSource() == ok) {
            OverlapMode om;
            Integer c = null;
            Hora t = null;

            if (partial.isSelected()) {
                om = OverlapMode.PARTIAL;

                if (number.isSelected()) {
                    c = (Integer) count.getValue();
                }

                if (time.isSelected()) {
                    t = new Hora((Integer) hour.getValue(), minutes.getSelectedIndex() * 30);
                }
            } else {
                om = OverlapMode.NONE;
            }

            tt.setOverlapOptions(om, c, t);

            parent.setChanged();

            WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
        }
    }
}
