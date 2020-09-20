package io;

import core.*;
import core.xml.Parser;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import net.miginfocom.swing.MigLayout;
import org.xml.sax.SAXException;

public class Interface extends JFrame implements ActionListener, ListSelectionListener {

    private static final long serialVersionUID = -3974681986485924703L;
    private static int CELL_HEIGHT;
    private File saveFile;
    private TimeTable tt;
    private JPanel mainpanel, checkboxPanel, aulasPanel;
    private JScrollPane scrollCadeiras, scrollAulas, scrollLectures;
    private JList listCadeiras;
    private JList listTurmas;
    private JList listLectures;
    private JPanel editCadeiras, editAulas, editLectures, controlCadeiras, controlAulas, controlLectures;
    private JPanel aulaLabels, subLectureL1, subLectureL2, aulaFields, subLectureF1, subLectureF2;
    private JPanel aulaButtons, cadeiraLabels, cadeiraFields, cadeiraButtons, lectureLabels, lectureFields, lectureButtons;
    private JMenuBar menu;
    private JMenu file, horario, options, help;
    private JMenuItem novo, open, save, saveAs, close, exit, construir, view, colors, overlap, about;
    private JTextField cn, at, aih, aim, afh, afm;
    private JLabel cname, ac, acmp, atr, ads, ai, af, cad, aulas, lect;
    private JButton cadd, cremove, cedit, aadd, aremove, aedit, ladd, lremove, ledit;
    private JComboBox acad, acomp, adia;
    private Font bold, plain;
    private JCheckBox allAulas;
    private boolean hasChanged;

    public Interface() {
        super("SmartTimeTable");
        this.setIconImage(new ImageIcon(getClass().getResource("/icons/Time.png")).getImage());
        hasChanged = false;

        bold = new Font("Arial", Font.BOLD, 20);
        plain = new Font("Arial", Font.BOLD, 16);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (checkChange()) {
                    System.exit(0);
                }
            }
        });

        buildWindow();

        mainpanel = new JPanel(new BorderLayout());
        mainpanel.add(editCadeiras, BorderLayout.WEST);
        mainpanel.add(editAulas, BorderLayout.CENTER);
        mainpanel.add(editLectures, BorderLayout.EAST);
        this.setContentPane(mainpanel);
        mainpanel.setEnabled(false);

        buildMenu();

        setFonts();

        this.setSize(1000, 600);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setVisible(true);
    }

    private void setFonts() {
        cname.setFont(bold);
        ac.setFont(bold);
        acmp.setFont(bold);
        atr.setFont(bold);
        ads.setFont(bold);
        ai.setFont(bold);
        af.setFont(bold);

        cadd.setFont(plain);
        cremove.setFont(plain);
        cedit.setFont(plain);
        aadd.setFont(plain);
        aremove.setFont(plain);
        aedit.setFont(plain);
        ladd.setFont(plain);
        lremove.setFont(plain);
        ledit.setFont(plain);

        cn.setFont(plain);
        at.setFont(plain);
        aih.setFont(plain);
        aim.setFont(plain);
        afh.setFont(plain);
        afm.setFont(plain);
        acad.setFont(plain);
        acomp.setFont(plain);
        adia.setFont(plain);

        file.setFont(bold);
        horario.setFont(bold);
        options.setFont(bold);
        help.setFont(bold);
        novo.setFont(plain);
        open.setFont(plain);
        save.setFont(plain);
        saveAs.setFont(plain);
        close.setFont(plain);
        exit.setFont(plain);
        view.setFont(plain);
        construir.setFont(plain);
        colors.setFont(plain);
        overlap.setFont(plain);
        about.setFont(plain);

        listCadeiras.setFont(plain);
        listTurmas.setFont(plain);
        listLectures.setFont(plain);

        cad.setFont(new Font("Arial", Font.BOLD, 26));
        aulas.setFont(new Font("Arial", Font.BOLD, 26));
        lect.setFont(new Font("Arial", Font.BOLD, 26));
    }

    private void buildWindow() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == aim || e.getSource() == afm) {
                    ((JTextField) e.getSource()).setText("0");
                } else {
                    ((JTextField) e.getSource()).setText("");
                }
            }
        };

        FocusAdapter fa = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (e.getSource() == aim || e.getSource() == afm) {
                    ((JTextField) e.getSource()).setText("0");
                } else {
                    ((JTextField) e.getSource()).setText("");
                }
            }
        };

        ListCellRenderer renderer = new DefaultListCellRenderer();
        ((JLabel) renderer).setHorizontalAlignment(SwingConstants.CENTER);

        /*
         * Cadeiras
         */
        listCadeiras = new JList();
        listCadeiras.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listCadeiras.setLayoutOrientation(JList.VERTICAL);
        listCadeiras.setVisibleRowCount(-1);
        listCadeiras.addListSelectionListener(this);
        listCadeiras.setCellRenderer(renderer);
        listCadeiras.setToolTipText("SubjectName");
        scrollCadeiras = new JScrollPane(listCadeiras);

        cname = new JLabel("  Name:  ");
        cname.setHorizontalAlignment(JLabel.CENTER);

        cadeiraLabels = new JPanel(new BorderLayout());
        cadeiraLabels.add(cname, BorderLayout.CENTER);

        cn = new JTextField("Subject Name Goes Here");
        cn.setHorizontalAlignment(JTextField.CENTER);
        cn.addMouseListener(ma);
        cn.addFocusListener(fa);

        cn.setEditable(false);

        cadeiraFields = new JPanel(new BorderLayout());
        cadeiraFields.add(cn, BorderLayout.CENTER);

        cadd = new JButton(new ImageIcon(getClass().getResource("/icons/Add.png")));
        cadd.addActionListener(this);
        cadd.setEnabled(false);

        cremove = new JButton(new ImageIcon(getClass().getResource("/icons/Delete.png")));
        cremove.addActionListener(this);
        cremove.setEnabled(false);

        cedit = new JButton(new ImageIcon(getClass().getResource("/icons/Change.png")));
        cedit.addActionListener(this);
        cedit.setEnabled(false);

        cadeiraButtons = new JPanel(new GridLayout(1, 3));
        cadeiraButtons.add(cadd);
        cadeiraButtons.add(cremove);
        cadeiraButtons.add(cedit);

        controlCadeiras = new JPanel(new BorderLayout());
        controlCadeiras.add(cadeiraLabels, BorderLayout.WEST);
        controlCadeiras.add(cadeiraFields, BorderLayout.CENTER);
        controlCadeiras.add(cadeiraButtons, BorderLayout.SOUTH);

        cad = new JLabel("Subjects", new ImageIcon(getClass().getResource("/icons/Chair-32.png")), JLabel.LEFT);
        cad.setHorizontalAlignment(JLabel.CENTER);

        editCadeiras = new JPanel(new BorderLayout());
        editCadeiras.add(cad, BorderLayout.NORTH);
        editCadeiras.add(scrollCadeiras, BorderLayout.CENTER);
        editCadeiras.add(controlCadeiras, BorderLayout.SOUTH);

        /*
         * Aulas
         */
        aulasPanel = new JPanel(new BorderLayout());

        checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new GridLayout());

        listTurmas = new JList();
        listTurmas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listTurmas.setLayoutOrientation(JList.VERTICAL);
        listTurmas.setVisibleRowCount(-1);
        listTurmas.addListSelectionListener(this);
        listTurmas.setCellRenderer(renderer);
        listTurmas.setToolTipText("SubjectName - ComponentAndClassNumber");

        aulasPanel.add(listTurmas, BorderLayout.CENTER);
        aulasPanel.add(checkboxPanel, BorderLayout.WEST);
        scrollAulas = new JScrollPane(aulasPanel);

        ac = new JLabel("  Subject:  ");
        ac.setHorizontalAlignment(JLabel.CENTER);
        acmp = new JLabel("  Component:  ");
        acmp.setHorizontalAlignment(JLabel.CENTER);
        atr = new JLabel("  Class:  ");
        atr.setHorizontalAlignment(JLabel.CENTER);

        aulaLabels = new JPanel(new GridLayout(3, 1));

        aulaLabels.add(ac);
        aulaLabels.add(acmp);
        aulaLabels.add(atr);

        acad = new JComboBox();
        acad.setRenderer(renderer);
        acomp = new JComboBox(Componente.values());
        acomp.setRenderer(renderer);
        at = new JTextField("Class");
        at.setHorizontalAlignment(JTextField.CENTER);
        at.addMouseListener(ma);
        at.addFocusListener(fa);

        acad.setEnabled(false);
        acomp.setEnabled(false);
        at.setEditable(false);

        aulaFields = new JPanel(new GridLayout(3, 1));

        aulaFields.add(acad);
        aulaFields.add(acomp);
        aulaFields.add(at);

        aadd = new JButton(new ImageIcon(getClass().getResource("/icons/Add.png")));
        aadd.addActionListener(this);
        aadd.setEnabled(false);

        aremove = new JButton(new ImageIcon(getClass().getResource("/icons/Delete.png")));
        aremove.addActionListener(this);
        aremove.setEnabled(false);

        aedit = new JButton(new ImageIcon(getClass().getResource("/icons/Change.png")));
        aedit.addActionListener(this);
        aedit.setEnabled(false);

        aulaButtons = new JPanel(new GridLayout(1, 3));
        aulaButtons.add(aadd);
        aulaButtons.add(aremove);
        aulaButtons.add(aedit);

        controlAulas = new JPanel(new BorderLayout());
        controlAulas.add(aulaLabels, BorderLayout.WEST);
        controlAulas.add(aulaFields, BorderLayout.CENTER);
        controlAulas.add(aulaButtons, BorderLayout.SOUTH);

        aulas = new JLabel("Classes", new ImageIcon(getClass().getResource("/icons/Work area.png")), JLabel.LEFT);
        aulas.setHorizontalAlignment(JLabel.CENTER);

        // SELECT ALL/NONE
        allAulas = new JCheckBox();
        allAulas.setSelected(false);
        allAulas.addActionListener(this);
        allAulas.setEnabled(false);

        JPanel topAulas = new JPanel(new BorderLayout());
        topAulas.add(allAulas, BorderLayout.WEST);
        topAulas.add(aulas, BorderLayout.CENTER);

        editAulas = new JPanel(new BorderLayout());
        editAulas.add(topAulas, BorderLayout.NORTH);
        editAulas.add(scrollAulas, BorderLayout.CENTER);
        editAulas.add(controlAulas, BorderLayout.SOUTH);

        /*
         * Lectures
         */
        listLectures = new JList();
        listLectures.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listLectures.setLayoutOrientation(JList.VERTICAL);
        listLectures.setVisibleRowCount(-1);
        listLectures.addListSelectionListener(this);
        listLectures.setCellRenderer(renderer);
        listLectures.setToolTipText("DayOfTheWeek : HourOfStart --> HourOfFinish");
        scrollLectures = new JScrollPane(listLectures);

        ads = new JLabel("  Day:  ");
        ads.setHorizontalAlignment(JLabel.CENTER);
        ai = new JLabel("  Start:  ");
        ai.setHorizontalAlignment(JLabel.CENTER);
        af = new JLabel("  Finish:  ");
        af.setHorizontalAlignment(JLabel.CENTER);

        lectureLabels = new JPanel(new BorderLayout());
        subLectureL1 = new JPanel(new GridLayout(1, 1));
        subLectureL2 = new JPanel(new GridLayout(2, 1));

        lectureLabels.add(subLectureL1, BorderLayout.NORTH);
        lectureLabels.add(subLectureL2, BorderLayout.SOUTH);

        subLectureL1.add(ads);
        subLectureL2.add(ai);
        subLectureL2.add(af);

        adia = new JComboBox(DiaSemanal.values());
        adia.setRenderer(renderer);
        aih = new JTextField("Hours");
        aih.setHorizontalAlignment(JTextField.CENTER);
        aih.addMouseListener(ma);
        aih.addFocusListener(fa);
        aim = new JTextField("Minutes");
        aim.setHorizontalAlignment(JTextField.CENTER);
        aim.addMouseListener(ma);
        aim.addFocusListener(fa);
        afh = new JTextField("Hours");
        afh.setHorizontalAlignment(JTextField.CENTER);
        afh.addMouseListener(ma);
        afh.addFocusListener(fa);
        afm = new JTextField("Minutes");
        afm.setHorizontalAlignment(JTextField.CENTER);
        afm.addMouseListener(ma);
        afm.addFocusListener(fa);

        adia.setEnabled(false);
        aih.setEditable(false);
        aim.setEditable(false);
        afh.setEditable(false);
        afm.setEditable(false);

        lectureFields = new JPanel(new BorderLayout());
        subLectureF1 = new JPanel(new GridLayout(1, 1));
        subLectureF2 = new JPanel(new GridLayout(2, 2));

        lectureFields.add(subLectureF1, BorderLayout.NORTH);
        lectureFields.add(subLectureF2, BorderLayout.SOUTH);

        subLectureF1.add(adia);
        subLectureF2.add(aih);
        subLectureF2.add(aim);
        subLectureF2.add(afh);
        subLectureF2.add(afm);

        ladd = new JButton(new ImageIcon(getClass().getResource("/icons/Add.png")));
        ladd.addActionListener(this);
        ladd.setEnabled(false);

        lremove = new JButton(new ImageIcon(getClass().getResource("/icons/Delete.png")));
        lremove.addActionListener(this);
        lremove.setEnabled(false);

        ledit = new JButton(new ImageIcon(getClass().getResource("/icons/Change.png")));
        ledit.addActionListener(this);
        ledit.setEnabled(false);

        lectureButtons = new JPanel(new GridLayout(1, 3));
        lectureButtons.add(ladd);
        lectureButtons.add(lremove);
        lectureButtons.add(ledit);

        controlLectures = new JPanel(new BorderLayout());
        controlLectures.add(lectureLabels, BorderLayout.WEST);
        controlLectures.add(lectureFields, BorderLayout.CENTER);
        controlLectures.add(lectureButtons, BorderLayout.SOUTH);

        lect = new JLabel("Lectures", new ImageIcon(getClass().getResource("/icons/Lecture.png")), JLabel.LEFT);
        lect.setHorizontalAlignment(JLabel.CENTER);

        editLectures = new JPanel(new BorderLayout());
        editLectures.add(lect, BorderLayout.NORTH);
        editLectures.add(scrollLectures, BorderLayout.CENTER);
        editLectures.add(controlLectures, BorderLayout.SOUTH);
    }

    private void buildMenu() {
        /*
         * File
         */
        novo = new JMenuItem("New", new ImageIcon(getClass().getResource("/icons/New file.png")));
        novo.addActionListener(this);
        novo.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.Event.CTRL_MASK));

        open = new JMenuItem("Open", new ImageIcon(getClass().getResource("/icons/Open.png")));
        open.addActionListener(this);
        open.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK));

        save = new JMenuItem("Save", new ImageIcon(getClass().getResource("/icons/Save.png")));
        save.addActionListener(this);
        save.setEnabled(false);
        save.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK));

        saveAs = new JMenuItem("Save As", new ImageIcon(getClass().getResource("/icons/Save as.png")));
        saveAs.addActionListener(this);
        saveAs.setEnabled(false);

        close = new JMenuItem("Close", new ImageIcon(getClass().getResource("/icons/Close file.png")));
        close.addActionListener(this);
        close.setEnabled(false);

        exit = new JMenuItem("Exit", new ImageIcon(getClass().getResource("/icons/Exit.png")));
        exit.addActionListener(this);

        file = new JMenu("File");
        file.setIcon(new ImageIcon(getClass().getResource("/icons/Form.png")));
        file.add(novo);
        file.addSeparator();
        file.add(open);
        file.add(save);
        file.add(saveAs);
        file.addSeparator();
        file.add(close);
        file.add(exit);

        /*
         * Horario
         */
        view = new JMenuItem("View", new ImageIcon(getClass().getResource("/icons/view.png")));
        view.addActionListener(this);
        view.setEnabled(true);

        construir = new JMenuItem("Construct", new ImageIcon(getClass().getResource("/icons/Curve points.png")));
        construir.addActionListener(this);
        construir.setEnabled(false);

        horario = new JMenu("Timetable");
        horario.setIcon(new ImageIcon(getClass().getResource("/icons/Increase time.png")));
        horario.add(view);
        horario.addSeparator();
        horario.add(construir);
        horario.setEnabled(true);

        /*
         * Options
         */
        colors = new JMenuItem("Colors", new ImageIcon(getClass().getResource("/icons/Colors.png")));
        colors.addActionListener(this);
        colors.setEnabled(false);

        overlap = new JMenuItem("Overlapping", new ImageIcon(getClass().getResource("/icons/Overlap.png")));
        overlap.addActionListener(this);
        overlap.setEnabled(false);

        options = new JMenu("Options");
        options.setIcon(new ImageIcon(getClass().getResource("/icons/Options.png")));
        options.add(colors);
        options.add(overlap);
        options.setEnabled(true);

        /*
         * Help
         */
        about = new JMenuItem("About", new ImageIcon(getClass().getResource("/icons/About.png")));
        about.addActionListener(this);

        help = new JMenu("Help");
        help.setIcon(new ImageIcon(getClass().getResource("/icons/Help.png")));
        help.add(about);

        menu = new JMenuBar();
        menu.add(file);
        menu.add(horario);
        menu.add(options);
        menu.add(help);

        this.setJMenuBar(menu);
    }

    @Override
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == novo && checkChange()) {
            tt = null;
            closeHorario();

            tt = new TimeTable();
            openHorario();
        } else if (e.getSource() == open && checkChange()) {
            try {
                tt = null;
                closeHorario();
                loadDialog();
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, "Error while trying to open the timetable!\n\n" + e1);
                closeHorario();
            }
        } else if (e.getSource() == save) {
            try {
                if (saveFile != null) {
                    Parser.exportXML(saveFile, tt);

                    if (this.hasChanged) {
                        this.hasChanged = false;
                        this.setTitle(this.getTitle().substring(0, this.getTitle().length() - 1));
                    }
                } else {
                    saveDialog();
                }
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, "Error while trying to save the timetable!\n\n" + e1);
                saveFile = null;
            }
        } else if (e.getSource() == saveAs) {
            try {
                saveDialog();
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, "Error while trying to save the timetable!\n\n" + e1);
                saveFile = null;
            }
        } else if (e.getSource() == close && checkChange()) {
            closeHorario();
        } else if (e.getSource() == exit && checkChange()) {
            System.exit(0);
        } else if (e.getSource() == view) {
            new HTMLViewer(this).setIconImage(new ImageIcon(getClass().getResource("/icons/view.png")).getImage());
        } else if (e.getSource() == construir) {
            try {
                if (tt.constructHorarios()) {
                    int opt = JOptionPane.showConfirmDialog(null, "Timetables construction completed successfully!\n\nPath: " + TimeTable.getConstructionPath() + "\n\nDo you wish to see them?", "Success", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                    if (opt == JOptionPane.YES_OPTION) {
                        new HTMLViewer(this);
                    }
                } else {
                    String[] classes = tt.deadlockFault();
                    String message = "A class overlapping deadlock has ocorred preventing any timetable from being created.\n\nThe classes responsible for this are:\n";
                    for (String s : classes) {
                        message += s + "\n";
                    }
                    JOptionPane.showMessageDialog(null, message, "Overlapping Deadlock Found", JOptionPane.WARNING_MESSAGE);

                }
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, "Error while trying to make the timetables!\n\n" + e1);
            }
        } else if (e.getSource() == about) {
            JOptionPane.showMessageDialog(null, "Created By: Diogo Regateiro\nForked and Updated by: André Alves\nEmail: andr.alves@ua.pt\nVersion: 4.6\n\nPlease report bugs to the email above. Thank you for using this program! :)");
        } else if (e.getSource() == cadd) {
            try {
                if (cn.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Error! The subject name field cannot de empty!");
                    return;
                }

                tt.addCadeira(cn.getText());

                refreshLists(true);
                clearCadeiraFields();

                aadd.setEnabled(true);
                acad.setEnabled(true);
                acomp.setEnabled(true);
                at.setEditable(true);
                checkboxPanel.setEnabled(true);
                setChanged();
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, "Error! A field that should be a number isn't or is not a valid number for an hour\n\n" + e1);
            }
        } else if (e.getSource() == cremove) {
            int opt = JOptionPane.showConfirmDialog(null, "This action will delete this subject, associated classes and lectures.\nDo you want to proceed with the deletion?", "Delete Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (opt == JOptionPane.YES_OPTION) {
                tt.removeCadeira((String) listCadeiras.getSelectedValue());
                refreshLists(true);

                if (tt.cadeirasToArray().length == 0) {
                    aadd.setEnabled(false);
                    acad.setEnabled(false);
                    acomp.setEnabled(false);
                    at.setEditable(false);
                    checkboxPanel.setEnabled(false);
                }

                if (tt.turmasToArray().length == 0) {
                    allAulas.setEnabled(false);
                }

                setChanged();
            }
        } else if (e.getSource() == aadd) {
            try {
                if (at.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Error! The class field cannot de empty!");
                    return;
                }

                tt.addTurma(new Turma((String) acad.getSelectedItem(), (Componente) acomp.getSelectedItem(), at.getText()));

                refreshLists(false);

                allAulas.setEnabled(true);
                setChanged();
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, "Error! A field that should be a number isn't or is not a valid number for an hour or subject is missing.\n\n" + e1);
            }
        } else if (e.getSource() == aremove) {
            int opt = JOptionPane.showConfirmDialog(null, "This action will delete this class and associated lectures.\nDo you want to proceed with the deletion?", "Delete Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (opt == JOptionPane.YES_OPTION) {
                tt.removeTurma((Turma) listTurmas.getSelectedValue());
                refreshLists(false);

                if (tt.turmasToArray().length == 0) {
                    allAulas.setEnabled(false);
                }
                setChanged();
            }
        } else if (e.getSource() == ladd) {
            try {
                Hora inicio = new Hora(Integer.parseInt(aih.getText()), Integer.parseInt(aim.getText()));
                Hora fim = new Hora(Integer.parseInt(afh.getText()), Integer.parseInt(afm.getText()));

                ((Turma) listTurmas.getSelectedValue()).addAula(new Aula((DiaSemanal) adia.getSelectedItem(), inicio, fim));

                listLectures.setListData(((Turma) listTurmas.getSelectedValue()).toArray());
                setChanged();
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, "Error! A field that should be a number isn't or is not a valid number for an hour or subject is missing.\n\n" + e1);
            }
        } else if (e.getSource() == lremove) {
            int opt = JOptionPane.showConfirmDialog(null, "This action will delete this lecture.\nDo you want to proceed with the deletion?", "Delete Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (opt == JOptionPane.YES_OPTION) {
                ((Turma) listTurmas.getSelectedValue()).removeAula((Aula) listLectures.getSelectedValue());
                listLectures.setListData(((Turma) listTurmas.getSelectedValue()).toArray());
                setChanged();
            }
        } else if (e.getSource() == cedit) {
            if (tt.renameCadeira((String) listCadeiras.getSelectedValue(), cn.getText())) {
                refreshLists(true);
                clearCadeiraFields();
                setChanged();
            } else {
                JOptionPane.showMessageDialog(null, "Error! Subject name already exists!");
            }

        } else if (e.getSource() == aedit) {
            Turma ot = ((Turma) listTurmas.getSelectedValue());
            Turma nt = new Turma((String) acad.getSelectedItem(), (Componente) acomp.getSelectedItem(), at.getText());
            if (tt.changeTurma(ot, nt)) {
                refreshLists(false);
                setChanged();
            } else {
                JOptionPane.showMessageDialog(null, "Error! Class already exists!");
            }
        } else if (e.getSource() == ledit) {
            try {
                Aula oldAula = (Aula) listLectures.getSelectedValue();

                Hora inicio = new Hora(Integer.parseInt(aih.getText()), Integer.parseInt(aim.getText()));
                Hora fim = new Hora(Integer.parseInt(afh.getText()), Integer.parseInt(afm.getText()));

                oldAula.changeTo(new Aula((DiaSemanal) adia.getSelectedItem(), inicio, fim));

                listLectures.setListData(((Turma) listTurmas.getSelectedValue()).toArray());
                setChanged();
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, "Error! A field that should be a number isn't or is not a valid number for an hour or subject is missing.\n\n" + e1);
            }
        } else if (e.getSource() == colors) {
            new ColorOptionsWindow(this, tt).setIconImage(new ImageIcon(getClass().getResource("/icons/Colors.png")).getImage());
        } else if (e.getSource() == overlap) {
            new OverlapOptionsWindow(this, tt).setIconImage(new ImageIcon(getClass().getResource("/icons/Overlap.png")).getImage());
        } else if (e.getSource() == allAulas) {
            for (int i = 0; i < checkboxPanel.getComponentCount(); i++) {
                JCheckBox cbox = (JCheckBox) checkboxPanel.getComponent(i);
                cbox.setSelected(allAulas.isSelected());
                ((Turma) listTurmas.getModel().getElementAt(i)).setInclude(cbox.isSelected());
            }
            setChanged();
        } else {
            for (int i = 0; i < checkboxPanel.getComponentCount(); i++) {
                JCheckBox cbox = (JCheckBox) checkboxPanel.getComponent(i);
                if (cbox == e.getSource()) {
                    ((Turma) listTurmas.getModel().getElementAt(i)).setInclude(cbox.isSelected());
                }
            }
            setChanged();
        }
    }

    private void clearCadeiraFields() {
        cn.setText("Subject Name Goes Here");
    }

    private void openHorario() {
        save.setEnabled(true);
        saveAs.setEnabled(true);
        close.setEnabled(true);
        construir.setEnabled(true);
        mainpanel.setEnabled(true);
        cadd.setEnabled(true);
        cn.setEditable(true);
        colors.setEnabled(true);
        overlap.setEnabled(true);
        hasChanged = false;

        if (tt.cadeirasToArray().length != 0) {
            aadd.setEnabled(true);
            acad.setEnabled(true);
            acomp.setEnabled(true);
            at.setEditable(true);
            checkboxPanel.setEnabled(true);
            allAulas.setEnabled(true);
        }

        refreshLists(true);
    }

    private void closeHorario() {
        tt = null;
        /*
         * Disable Menus
         */

        saveFile = null;
        save.setEnabled(false);
        saveAs.setEnabled(false);
        close.setEnabled(false);
        construir.setEnabled(false);
        mainpanel.setEnabled(false);
        cadd.setEnabled(false);
        cremove.setEnabled(false);
        cedit.setEnabled(false);
        aadd.setEnabled(false);
        aremove.setEnabled(false);
        aedit.setEnabled(false);
        cn.setEditable(false);
        acad.setEnabled(false);
        acomp.setEnabled(false);
        at.setEditable(false);
        adia.setEnabled(false);
        aih.setEditable(false);
        aim.setEditable(false);
        afh.setEditable(false);
        afm.setEditable(false);
        ladd.setEnabled(false);
        lremove.setEnabled(false);
        ledit.setEnabled(false);
        checkboxPanel.setEnabled(false);
        allAulas.setEnabled(false);
        colors.setEnabled(false);
        overlap.setEnabled(false);
        hasChanged = false;

        clearLists();
        this.setTitle("SmartTimeTable");
    }

    private boolean saveDialog() throws FileNotFoundException, IOException {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("."));
        fc.setDialogTitle("Save Timetable");
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filterstt = new FileNameExtensionFilter(
                "SmartTimeTable XML file (.sttx)", "sttx");
        fc.addChoosableFileFilter(filterstt);
        fc.setFileFilter(filterstt);
        fc.setAcceptAllFileFilterUsed(false);

        if (fc.showDialog(null, "Save") == JFileChooser.APPROVE_OPTION) {
            saveFile = fc.getSelectedFile();
            if (!filterstt.accept(saveFile)) {
                saveFile = new File(saveFile.getCanonicalPath() + ".sttx");
            }
            Parser.exportXML(saveFile, tt);
            this.setTitle("SmartTimeTable - " + saveFile.getAbsolutePath());
            hasChanged = false;
            return true;
        }

        return false;
    }

    private void loadDialog() throws ParserConfigurationException, SAXException, IOException {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("."));
        fc.setDialogTitle("Open Timetable");
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filterstt = new FileNameExtensionFilter(
                "SmartTimeTable XML file (.sttx)", "sttx");
        fc.addChoosableFileFilter(filterstt);
        fc.setFileFilter(filterstt);
        fc.setAcceptAllFileFilterUsed(false);

        if (fc.showDialog(null, "Open") == JFileChooser.APPROVE_OPTION) {
            saveFile = fc.getSelectedFile();
            tt = Parser.importXML(saveFile);
            this.setTitle("SmartTimeTable - " + saveFile.getAbsolutePath());
            openHorario();
            hasChanged = false;
        }
    }

    private void refreshLists(boolean updateCadeiras) {
        Turma[] turmas = tt.turmasToArray();
        listCadeiras.setListData(tt.cadeirasToArray());
        listTurmas.setListData(turmas);
        if (updateCadeiras) {
            acad.setModel(new DefaultComboBoxModel(tt.cadeirasToArray()));
        }

        checkboxPanel.removeAll();
        checkboxPanel.setLayout(new MigLayout("flowy, gap 0 0, ins 0 0 0 0"));
        for (int i = 0; i < turmas.length; i++) {
            JCheckBox newBox = new JCheckBox();
            newBox.setSelected(turmas[i].toBeIncluded());
            newBox.addActionListener(this);
            checkboxPanel.add(newBox);
        }
        checkboxPanel.repaint();
        fixListsHeight();
    }

    private void clearLists() {
        listCadeiras.setListData(new String[0]);
        listTurmas.setListData(new Turma[0]);
        listLectures.setListData(new Aula[0]);
        checkboxPanel.removeAll();
    }

    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void main(String args[]) {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
        }

        new Interface();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == listCadeiras && !e.getValueIsAdjusting()) {
            if (listCadeiras.getSelectedIndex() == -1) {
                cremove.setEnabled(false);
                cedit.setEnabled(false);
            } else {
                cremove.setEnabled(true);
                cedit.setEnabled(true);

            }
        }

        if (e.getSource() == listTurmas && !e.getValueIsAdjusting()) {
            if (listTurmas.getSelectedIndex() == -1) {
                aremove.setEnabled(false);
                aedit.setEnabled(false);
                listLectures.setListData(new Aula[0]);
                adia.setEnabled(false);
                aih.setEditable(false);
                aim.setEditable(false);
                afh.setEditable(false);
                afm.setEditable(false);
                ladd.setEnabled(false);
                lremove.setEnabled(false);
                ledit.setEnabled(false);
            } else {
                aremove.setEnabled(true);
                aedit.setEnabled(true);
                listLectures.setListData(((Turma) listTurmas.getSelectedValue()).toArray());
                adia.setEnabled(true);
                aih.setEditable(true);
                aim.setEditable(true);
                afh.setEditable(true);
                afm.setEditable(true);
                ladd.setEnabled(true);
            }
        }

        if (e.getSource() == listLectures && !e.getValueIsAdjusting()) {
            if (listLectures.getSelectedIndex() == -1) {
                lremove.setEnabled(false);
                ledit.setEnabled(false);
            } else {
                lremove.setEnabled(true);
                ledit.setEnabled(true);
            }
        }
    }

    private void fixListsHeight() {
        if (checkboxPanel.getComponentCount() > 0) {
            CELL_HEIGHT = checkboxPanel.getComponent(0).getPreferredSize().height;
        } else {
            CELL_HEIGHT = 25;
        }

        listCadeiras.setFixedCellHeight(CELL_HEIGHT);
        listTurmas.setFixedCellHeight(CELL_HEIGHT);
        listLectures.setFixedCellHeight(CELL_HEIGHT);

        listCadeiras.repaint();
        listTurmas.repaint();
        listLectures.repaint();
    }

    private boolean checkChange() {
        if (this.hasChanged) {
            int opt = JOptionPane.showConfirmDialog(null, "There are unsaved changes, do you wish to save before proceeding?", "Unsaved changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (opt == JOptionPane.YES_OPTION) {
                try {
                    if (saveFile != null) {
                        Parser.exportXML(saveFile, tt);
                    } else if (!saveDialog()) {
                        return false;
                    }
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(null, "Error while trying to save the timetable!\n\n" + e1);
                    saveFile = null;
                }

                return true;
            } else if (opt == JOptionPane.NO_OPTION) {
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    public void setChanged() {
        if (!this.hasChanged) {
            this.hasChanged = true;
            this.setTitle(this.getTitle() + "*");
        }
    }
}
