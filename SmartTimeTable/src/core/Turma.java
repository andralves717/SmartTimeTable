package core;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

public class Turma implements Serializable, Conflictable<Turma>, Comparable<Turma> {

    private static final long serialVersionUID = -1774234232283709385L;
    private String cadeira;
    private Componente comp; //Tem de pertencer a lista de limites na cadeira!
    private String turma;
    private SortedSet<Aula> aulas;
    private boolean valid;
    private boolean include;

    public Turma(String c, Componente cmp, String t) {
        if (c == null || t == null) {
            throw new IllegalArgumentException("Erro: Um ou mais parametros de entradas estao incorrectos.");
        }

        this.cadeira = c;
        this.comp = cmp;
        this.turma = t;
        this.aulas = new TreeSet<Aula>();
        this.valid = false; /* ********************************************************************************* */
        this.include = true;
    }

    public void setCadeira(String cadeira) {
        this.cadeira = cadeira;
    }

    public void setComp(Componente comp) {
        this.comp = comp;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }

    public boolean addAula(Aula a) {
        if (a == null) {
            throw new IllegalArgumentException("Erro: Um ou mais parametros de entradas estao incorrectos.");
        }

        return aulas.add(a);
    }

    public boolean removeAula(Aula a) {
        if (a == null) {
            throw new IllegalArgumentException("Erro: Um ou mais parametros de entradas estao incorrectos.");
        }

        return aulas.remove(a);
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @param valid the valid to set
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public void setInclude(boolean include) {
        this.include = include;
    }
    
    public boolean toBeIncluded() {
        return this.include;
    }

    public String getCadeira() {
        return cadeira;
    }

    public Componente getComponente() {
        return comp;
    }

    public String getTurma() {
        return turma;
    }

    @Override
    public boolean conflictsWith(Turma obj, OverlapOptions oo) {
        if (getCadeira().equals(obj.getCadeira()) && getComponente() == obj.getComponente()) {
            return true;
        }

        for (Aula a : this.aulas) {
            for (Aula b : obj.aulas) {
                if (a.conflictsWith(b, oo)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        Turma other = (Turma) obj;

        return this.cadeira.equals(other.cadeira) && this.comp == other.comp && this.turma.equals(other.turma);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.cadeira != null ? this.cadeira.hashCode() : 0);
        hash = 17 * hash + (this.comp != null ? this.comp.hashCode() : 0);
        hash = 17 * hash + (this.turma != null ? this.turma.hashCode() : 0);
        return hash;
    }

    public Aula[] toArray() {
        Aula[] array = new Aula[this.aulas.size()];
        this.aulas.toArray(array);
        return array;
    }

    @Override
    public String toString() {
        return getCadeira() + " - " + getComponente() + getTurma();
    }

    public String toStringCompact() {
        if (getCadeira().length() < 3) {
            return getCadeira().toUpperCase() + "-" + getComponente() + getTurma();
        }

        return getCadeira().substring(0, 3).toUpperCase() + "-" + getComponente() + getTurma();
    }

    public boolean existsInTime(int i, Hora h) {
        for (Aula a : aulas) {
            if (a.getDia().ordinal() == i) {
                if (h.compareTo(a.getInicio()) >= 0 && h.compareTo(a.getFim()) < 0) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int compareTo(Turma arg0) {
        return (getCadeira() + getComponente() + getTurma()).compareTo(arg0.getCadeira() + arg0.getComponente() + arg0.getTurma());
    }
    
    public String toXML() {
        String out = "";
        
        out += "\t\t<subject>" + cadeira + "</subject>\n";
        out += "\t\t<comp>" + comp.toString() + "</comp>\n";
        out += "\t\t<descr>" + turma + "</descr>\n";
        out += "\t\t<include>" + include + "</include>\n";
        
        for(Aula a : aulas) {
            out += "\t\t<lecture>\n";
            out += a.toXML();
            out += "\t\t</lecture>\n";
        }
        
        return out;
    }
}
