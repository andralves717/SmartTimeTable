/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Regateiro
 */
public class HTMLViewer extends JFrame implements ActionListener {

    private JScrollPane editorScrollPane, listScrollPane;
    private JPrintableEditorPane editorPane;
    private JPanel controls, top, jump_panel;
    private JButton first, prev, next, last, delete, print;
    private JLabel label, jump;
    private JTextField tf_jump;
    private JTextArea listArea;
    private File[] list;
    private int currentTT;

    @SuppressWarnings("LeakingThisInConstructor")
    public HTMLViewer(final Interface parent) {
        super("TimeTable Viewer");
        parent.setEnabled(false);
        currentTT = -1;

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parent.setEnabled(true);
            }
        });

        buildList();
        if (list.length > 0) {
            currentTT = 0;
        }

        top = new JPanel(new BorderLayout());
        label = new JLabel("", JLabel.CENTER);
        updateTop();

        jump = new JLabel("Jump: ");
        tf_jump = new JTextField(list.length + "");
        jump_panel = new JPanel(new BorderLayout());
        jump_panel.add(jump, BorderLayout.WEST);
        jump_panel.add(tf_jump, BorderLayout.CENTER);

        tf_jump.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent key) {
                if (key.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        int j = Integer.parseInt(tf_jump.getText());

                        if (j <= 0 || j > list.length) {
                            throw new Exception();
                        }

                        currentTT = j - 1;
                        readTimetable(currentTT);
                        updateTop();
                        updateControls();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Inserted number is not valid", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        top.add(label, BorderLayout.CENTER);
        top.add(jump_panel, BorderLayout.EAST);

        listArea = new JTextArea();
        listArea.setEditable(false);

        editorPane = new JPrintableEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");

        readTimetable(currentTT);
        
        listScrollPane = new JScrollPane(listArea);

        editorScrollPane = new JScrollPane(editorPane);

        first = new JButton("<<");
        first.addActionListener(this);
        prev = new JButton("<");
        prev.addActionListener(this);
        delete = new JButton("Delete");
        delete.addActionListener(this);
        print = new JButton("Print");
        print.addActionListener(this);
        next = new JButton(">");
        next.addActionListener(this);
        last = new JButton(">>");
        last.addActionListener(this);
        controls = new JPanel(new GridLayout(1, 6));
        controls.add(first);
        controls.add(prev);
        controls.add(delete);
        controls.add(print);
        controls.add(next);
        controls.add(last);

        updateControls();

        this.setLayout(new BorderLayout());
        this.add(top, BorderLayout.NORTH);
        this.add(editorScrollPane, BorderLayout.CENTER);
        this.add(listScrollPane, BorderLayout.EAST);
        this.add(controls, BorderLayout.SOUTH);

        this.pack();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        if (dim.height * 0.95 < this.getHeight()) {
            this.setSize(this.getWidth(), (int) (dim.height * 0.95));
        }

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    private String readFile(File file) throws FileNotFoundException, IOException {
        String content;
        BufferedReader in = new BufferedReader(new FileReader(file));
        String line;
        content = "";
        while ((line = in.readLine()) != null) {
            content += line;
        }
        in.close();
        return content;
    }

    private void updateTop() {
        label.setText("Timetable " + (currentTT + 1) + " of " + list.length);
    }

    private void updateControls() {
        first.setEnabled(true);
        prev.setEnabled(true);
        next.setEnabled(true);
        last.setEnabled(true);
        delete.setEnabled(true);
        print.setEnabled(true);

        if (currentTT == 0) {
            first.setEnabled(false);
            prev.setEnabled(false);
        }

        if (currentTT == list.length - 1) {
            next.setEnabled(false);
            last.setEnabled(false);
        }

        if (currentTT == -1) {
            first.setEnabled(false);
            prev.setEnabled(false);
            next.setEnabled(false);
            last.setEnabled(false);
            delete.setEnabled(false);
            print.setEnabled(false);
        }
    }

    private void buildList() {
        File folder = new File("TimeTables");

        if (!folder.exists() && !folder.mkdir()) {
            list = new File[0];
        }

        list = (new File("TimeTables")).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                try {
                    Integer.parseInt(name.split("[.]")[0]);
                } catch (Exception ex) {
                    return false;
                }

                return true;
            }
        });

        Arrays.sort(list, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                int i1, i2;

                i1 = Integer.parseInt(o1.getName().split("[.]")[0]);
                i2 = Integer.parseInt(o2.getName().split("[.]")[0]);

                return i1 - i2;
            }
        });
    }

    private void readTimetable(int currentTT) {
        if (list != null && list.length > 0) {
            try {
                BufferedReader in = new BufferedReader(new FileReader(list[currentTT]));
                String temp;
                boolean start = false;

                listArea.setText("\n List of classes: \n\n ");
                while ((temp = in.readLine()) != null) {
                    if (temp.equals("-->")) {
                        break;
                    }
                    
                    if (start) {
                        listArea.append(temp + "\n ");
                    }
                    
                    if (temp.equals("<!-- LOCMN37")) {
                        start = true;
                    }
                }
                
                editorPane.setText(readFile(list[currentTT]));
                in.close();
            } catch (Exception ex) {
                editorPane.setText("<h1>Error while trying to read the timetable<h1>");
            }
        } else {
            listArea.setText("");
            editorPane.setText("<h1>No timetables were found inside the folder \"TimeTables\"<h1>");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == first) {
            currentTT = 0;
        } else if (e.getSource() == prev) {
            currentTT--;
        } else if (e.getSource() == next) {
            currentTT++;
        } else if (e.getSource() == last) {
            currentTT = list.length - 1;
        } else if (e.getSource() == delete) {
            if (!list[currentTT].delete()) {
                JOptionPane.showMessageDialog(null, "An error occurred while trying to delete the timetable", "Error", JOptionPane.ERROR_MESSAGE);
            }

            buildList();

            if (currentTT == list.length) {
                currentTT--;
            }
        } else if (e.getSource() == print) {
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setPrintable(editorPane);
            if (printJob.printDialog()) {
                try {
                    printJob.print();
                } catch (PrinterException pe) {
                    JOptionPane.showMessageDialog(null, "An error occurred while trying to print the timetable", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        readTimetable(currentTT);
        updateTop();
        updateControls();
    }
}
