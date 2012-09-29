package core;

import java.awt.Color;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the construction of timetables given the possible classes.
 *
 * @author Diogo Regateiro
 *
 */
public final class TimeTable implements Serializable {

    private static final long serialVersionUID = -3424231891195751641L;
    private static final File dir = new File("TimeTables");
    private int fileID;
    private SortedSet<Turma> horario;
    private SortedSet<String> cadeiras;
    private ColorOptions colors;
    private boolean deadlock;
    private OverlapOptions overlap;

    /**
     * Constructs an empty TimeTable Object.
     */
    public TimeTable() {
        this.horario = new TreeSet<Turma>();
        this.cadeiras = new TreeSet<String>();
        this.colors = new ColorOptions();
        this.overlap = new OverlapOptions(OverlapMode.NONE, null, null);
    }

    /**
     * Gets all classes added.
     *
     * @return An array containing all classes.
     */
    public Turma[] turmasToArray() {
        Turma[] array = new Turma[this.horario.size()];
        this.horario.toArray(array);
        return array;
    }

    /**
     * Checks if a complete timetable has been made.
     *
     * @return true if a timetable has the correct number of hours in it, false
     * otherwise.
     */
    private boolean horarioCompleted(Turma[] buffer) {
        Set<String> check = new HashSet<String>();

        for (Turma t : buffer) {
            if (t.toBeIncluded()) {
                check.add(t.getCadeira() + t.getComponente());
            }
        }

        for (Turma t : buffer) {
            if (t.isValid()) {
                check.remove(t.getCadeira() + t.getComponente());
            }
        }

        return check.isEmpty();
    }

    /**
     * Checks if a given class does not conflict with others enabled.
     *
     * @param a A class to check if fits
     * @return true if the class fits, false otherwise
     */
    private boolean turmaFits(Turma t) {
        for (Turma u : this.horario) {
            if (u.isValid()) {
                if (t.conflictsWith(u, overlap)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Constructs all possible timetables with the given classes and tries to
     * save them in the "TimeTables" directory.
     *
     * @return Whether at least one timetable was created
     * @throws FileNotFoundException
     * @throws IOException
     */
    public boolean constructHorarios() throws FileNotFoundException, IOException {
        if (dir.exists() && !dir.isDirectory()) {
            throw new IllegalArgumentException("Be sure that no file named \"TimeTables\" exists in the current directory.");
        }

        deleteDir();
        this.fileID = 1;
        overlap.resetLim();
        constructHorariosRec(this.turmasToArray(), 0, true);

        return this.fileID != 1;
    }

    /**
     * Constructs all possible timetables with the given classes and tries to
     * save them in the "TimeTables" directory. This function is recursive and
     * is called by constructHorarios().
     *
     * @param buffer An array containing all classes.
     * @param pos The current position on the buffer.
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void constructHorariosRec(Turma[] buffer, int pos, boolean save) throws FileNotFoundException, IOException {
        if (horarioCompleted(buffer)) {
            if (save) {
                saveValidHorarioAsHTML(buffer);
            }

            this.deadlock = false;
            return;
        }

        for (; pos < buffer.length; pos++) {
            if (buffer[pos].toBeIncluded() && turmaFits(buffer[pos])) {
                buffer[pos].setValid(true);
                constructHorariosRec(buffer, pos + 1, save);
                buffer[pos].setValid(false);
                overlap.removeFromLim(buffer[pos]);

                if (!save && !this.deadlock) {
                    return;
                }
            }
        }
    }

    /**
     * Adds a class.
     *
     * @param t The class to be added.
     * @return true if the class was added successfully, false otherwise.
     */
    public boolean addTurma(Turma t) {
        if (t == null) {
            throw new NullPointerException("Turma cannot be null");
        }

        if (this.cadeiras.contains(t.getCadeira())) {
            return this.horario.add(t);
        }

        return false;
    }

    /**
     * Removes a class.
     *
     * @param t The class to be removed.
     * @return true if the class was removed successfully, false otherwise.
     */
    public boolean removeTurma(Turma t) {
        if (t == null) {
            throw new NullPointerException("Turma cannot be null");
        }

        return this.horario.remove(t);
    }

    /**
     * Adds a subject.
     *
     * @param c The subject to be added.
     * @return true if the subject was added successfully, false otherwise.
     */
    public boolean addCadeira(String c) {
        if (c == null) {
            throw new NullPointerException("Cadeira cannot be null");
        }

        this.colors.addCadeira(c);
        return this.cadeiras.add(c);
    }

    /**
     * Removes a subject.
     *
     * @param c The subject to be removed.
     * @return true if the subject was removed successfully, false otherwise.
     */
    public boolean removeCadeira(String c) {
        boolean ret = true;
        ArrayList<Turma> toRemove = new ArrayList<Turma>();

        if (c == null) {
            throw new NullPointerException("Cadeira cannot be null");
        }

        ret &= this.cadeiras.remove(c);

        for (Turma t : this.horario) {
            if (t.getCadeira().equals(c)) {
                toRemove.add(t);
            }
        }

        ret &= this.horario.removeAll(toRemove);

        this.colors.removeCadeira(c);
        return ret;
    }

    /**
     * Gets an iterator for the classes.
     *
     * @return An iterator of classes.
     */
    public Iterator<Turma> iterator() {
        return this.horario.iterator();
    }

    /**
     * Saves the enabled classes that make a valid timetable into a file.
     *
     * @param buffer An array of classes.
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void saveValidHorario(Turma[] buffer) throws FileNotFoundException, IOException {
        PrintWriter pw = new PrintWriter(dir.getAbsolutePath() + File.separator + fileID++ + ".txt");
        pw.write("|-------|----------|----------|----------|----------|----------|\n");
        pw.write("| HORAS | SEGUNDA  |  TERCA   |  QUARTA  |  QUINTA  |  SEXTA   |\n");
        pw.write("|-------|----------|----------|----------|----------|----------|\n");

        for (Hora h = new Hora(8, 0); h.compareTo(new Hora(22, 0)) < 0; h = new Hora(h.getTotalTimeInMins() + 30)) {
            pw.printf("| %5s |", h);

            for (int i = 0; i < 5; i++) {
                boolean found = false;

                for (Turma t : buffer) {
                    if (t.toBeIncluded() && t.isValid()) {
                        if (t.existsInTime(i, h)) {
                            pw.printf(" %-8s |", t.toStringCompact());
                            found = true;
                            break;
                        }
                    }
                }

                if (!found) {
                    pw.print("          |");
                }
            }

            pw.write("\n|-------|----------|----------|----------|----------|----------|\n");
        }

        pw.flush();
        pw.close();
    }

    /**
     * Saves the enabled classes that make a valid timetable into a file.
     *
     * @param buffer An array of classes.
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void saveValidHorarioAsHTML(Turma[] buffer) throws FileNotFoundException, IOException {
        PrintWriter pw = new PrintWriter(dir.getAbsolutePath() + File.separator + fileID + ".html");
        pw.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\n");
        pw.write("\"http://www.w3.org/TR/html4/loose.dtd\">\n");
        pw.write("<html lang=\"en\">\n");
        
        //Write the classes (LOCMN37 - List Of Classes Magic Number 37)
        pw.write("<!-- LOCMN37\n");
        for(Turma t : buffer) {
            if(t.toBeIncluded() && t.isValid()) {
                pw.write(t.toStringCompact() + "\n");
            }
        }
        pw.write("-->\n");
        
        pw.write("<body>\n");
        pw.write("<h1>Timetable " + fileID + "</h1>\n");
        fileID++;

        //Construct the header of the table
        pw.write("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">\n");
        pw.write("<tr>\n");
        pw.write("<th style=\"background-color:gray;\">Hour</th>\n");
        pw.write("<th style=\"background-color:gray;\">Monday</th>\n");
        pw.write("<th style=\"background-color:gray;\">Tuesday</th>\n");
        pw.write("<th style=\"background-color:gray;\">Wednesday</th>\n");
        pw.write("<th style=\"background-color:gray;\">Thursday</th>\n");
        pw.write("<th style=\"background-color:gray;\">Friday</th>\n\t\t\t</tr>\n");

        //Construct the rest of the table
        for (Hora h = new Hora(8, 0); h.compareTo(new Hora(22, 0)) < 0; h = new Hora(h.getTotalTimeInMins() + 30)) {
            pw.write("<tr>\n");
            pw.printf("<td style=\"background-color:gray;\">%s</td>\n", h);

            for (int i = 0; i < 5; i++) {
                boolean found = false;

                pw.write("<td><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>");
                
                for (Turma t : buffer) {
                    if (t.toBeIncluded() && t.isValid()) {
                        if (t.existsInTime(i, h)) {
                            pw.write("<td align=\"center\"");
                            pw.write(colorToHTML(colors.getColor(t.getCadeira(), t.getComponente())));
                            pw.write(">&nbsp;" + t.toStringCompact() + "&nbsp;</td>\n");
                            found = true;
                        }
                    }
                }

                if (!found) {
                    pw.write("<td></td>\n");
                }
                
                pw.write("</tr></table></td>");
            }

            pw.write("</tr>\n");
        }

        //Finish the html file
        pw.write("</table>\n");
        pw.write("</body>\n");
        pw.write("</html>");

        pw.flush();
        pw.close();
    }

    /**
     * Saves this Object using serialization.
     *
     * @param f The file were it is to be saved.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void save(File f) throws FileNotFoundException, IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
        out.writeObject(this);
        out.flush();
        out.close();
    }

    /**
     * Attempts to load a TimeTable object from the given file.
     *
     * @param f The file to try to load.
     * @return The loaded TimeTable object.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static TimeTable load(File f) throws FileNotFoundException, IOException, ClassNotFoundException {
        TimeTable tt;
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
        tt = (TimeTable) in.readObject();
        in.close();
        return tt;
    }

    /**
     * If the saving directory already exists, its contents are erased, else the
     * directory is created.
     */
    private void deleteDir() {
        if (!dir.exists()) {
            dir.mkdir();
        } else {
            for (File file : dir.listFiles()) {
                file.delete();
            }
        }
    }

    /**
     * Gets the path were the timetables are saved.
     *
     * @return A string representing the absolute path.
     */
    public static String getConstructionPath() {
        return dir.getAbsolutePath();
    }

    /**
     * Gets an array of the subjects added.
     *
     * @return An array with the added subjects.
     */
    public String[] cadeirasToArray() {
        String[] c = new String[this.cadeiras.size()];
        cadeiras.toArray(c);
        return c;
    }

    private String colorToHTML(Color c) {
        if (c == null) {
            c = new Color(255, 255, 255);
        }
        
        String hex = Integer.toHexString(c.getRGB() & 0x00ffffff);
        while(hex.length() != 6) {
            hex = "0" + hex;
        }
        
        return String.format("style=\"background-color:#%s;\"", hex);
    }

    public boolean renameCadeira(String oldName, String newName) {
        if (!cadeiras.contains(newName)) {
            cadeiras.remove(oldName);
            cadeiras.add(newName);

            for (Turma t : turmasToArray()) {
                if (t.getCadeira().equals(oldName)) {
                    t.setCadeira(newName);
                }
            }
        } else {
            return false;
        }

        return true;
    }

    public boolean changeTurma(Turma ot, Turma nt) {
        if (!horario.contains(nt)) {
            ot.setCadeira(nt.getCadeira());
            ot.setComp(nt.getComponente());
            ot.setTurma(nt.getTurma());
        } else {
            return false;
        }

        return true;
    }

    /**
     * Invoking this function assumes that there is a deadlock.
     *
     * @return The Classes that are causing the deadlock.
     */
    public String[] deadlockFault() {
        SortedSet<Turma> buffer = new TreeSet<Turma>(horario);
        boolean deadlock_pass;

        try {
            do {
                deadlock_pass = false;
                Turma[] temp = buffer.toArray(new Turma[0]);
                for (int i = 0; i < temp.length; i++) {
                    // Remove the class
                    buffer.remove(temp[i]);

                    // Check if this class was included, if not just remove it
                    if (!temp[i].toBeIncluded()) {
                        continue;
                    }

                    // Assume that there is a deadlock
                    this.deadlock = true;

                    // try to build a timetable
                    constructHorariosRec(buffer.toArray(new Turma[0]), 0, false);

                    // If the deadlock was lifted, add the class again and check a non deadlock pass
                    if (this.deadlock) {
                        deadlock_pass = true;
                        break;
                    }

                    // If the cycle continued the deadlock was lifted,
                    // so add the guilty class again
                    buffer.add(temp[i]);
                }
            } while (deadlock_pass);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TimeTable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TimeTable.class.getName()).log(Level.SEVERE, null, ex);
        }

        Set<String> res = new HashSet<String>();
        for (Turma t : buffer) {
            res.add(t.toStringCompact());
        }
        return res.toArray(new String[0]);
    }

    public OverlapMode getOverlapMode() {
        return overlap.getOverlapmode();
    }

    public Integer getOverlapCount() {
        return overlap.getOverlapcount();
    }

    public Hora getOverlapTime() {
        return overlap.getOverlaptime();
    }

    public void setOverlapOptions(OverlapMode om, Integer count, Hora time) {
        this.overlap = new OverlapOptions(om, count, time);
    }

    public ColorOptions getColorOptions() {
        return this.colors;
    }

    public void setColorOptions(ColorOptions temp) {
        this.colors = new ColorOptions(temp);
    }
    
    public String toXML() {
        String out = "";
        
        out += "<timetable>\n";
        
        for(String sub : cadeiras) {
            out += "\t<subject>" + sub + "</subject>\n";
        }
        
        for(Turma t : horario) {
            out += "\t<class>\n";
            out += t.toXML();
            out += "\t</class>\n";
        }
        
        out += colors.toXML();
        out += overlap.toXML();
        
        out += "</timetable>\n";
        
        return out;
    }
}
