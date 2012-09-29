package core;

import java.io.Serializable;

/**
 * Handles all information about the classes.
 *
 * @author Diogo Regateiro
 *
 */
public final class Aula implements Serializable, Comparable<Aula>, Conflictable<Aula> {

    private static final long serialVersionUID = -3514089225274681849L;
    private DiaSemanal dia;
    private Hora inicio;
    private Hora fim;

    /**
     * Constructs a Aula object.
     *
     * @param ds The day of the week this class is lectured.
     * @param inicio The starting hour.
     * @param fim The finishing hour.
     * @throws IllegalArgumentException if any parameter is null or if the class
     * number is under 1.
     */
    public Aula(DiaSemanal ds, Hora inicio, Hora fim) {
        if (inicio == null || fim == null || inicio.compareTo(fim) >= 0) {
            throw new IllegalArgumentException("Erro: Um ou mais parametros de entradas estao incorrectos.");
        }

        this.dia = ds;
        this.inicio = inicio;
        this.fim = fim;
    }

    /**
     * @return the dia
     */
    public DiaSemanal getDia() {
        return dia;
    }

    /**
     * @return the inicio
     */
    public Hora getInicio() {
        return inicio;
    }

    /**
     * @return the fim
     */
    public Hora getFim() {
        return fim;
    }

    /**
     * Calculates the duration of the class. It calls the timeLapse() function
     * with the start and end of the class as parameters.
     *
     * @return The duration in a Hora object.
     */
    public Hora getDuration() {
        return Hora.timeLapse(this.inicio, this.fim);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        Aula other = (Aula) obj;

        return this.dia == other.dia && this.inicio.equals(other.inicio) && this.fim.equals(other.fim);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + (this.dia != null ? this.dia.hashCode() : 0);
        hash = 31 * hash + (this.inicio != null ? this.inicio.hashCode() : 0);
        hash = 31 * hash + (this.fim != null ? this.fim.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getDia() + " " + getInicio() + " " + getFim();
    }

    @Override
    public int compareTo(Aula arg0) {
        if (arg0 == null) {
            throw new NullPointerException("Erro: O parametro nao pode ser null.");
        }

        if (this.equals(arg0)) {
            return 0;
        }

        if (getDia().ordinal() < arg0.getDia().ordinal()) {
            return -1;
        }

        if (getDia().ordinal() > arg0.getDia().ordinal()) {
            return 1;
        }

        int ini = this.inicio.compareTo(arg0.inicio);

        if (ini != 0) {
            return ini;
        }

        return 1;
    }

    @Override
    public boolean conflictsWith(Aula obj, OverlapOptions oo) {
        if (obj == null) {
            throw new IllegalArgumentException("Erro: O parametro nao pode ser null.");
        }

        /* Visto o caso anterior, duas aulas não entram em conflito se forem dadas em dias distintos */
        if (this.dia != obj.dia) {
            return false;
        }

        /* Se forem dadas no mesmo dia elas entram em conflito caso comecem ou acabem ao mesmo tempo */
        if (this.inicio.equals(obj.inicio) || this.fim.equals(obj.fim)) {
            if (oo.crossLimits(this, obj)) {
                return true;
            } else {
                return false;
            }
        }

        /* Se a aula "a" não terminar depois da aula "b" começar então não estão em conflito */
        if (this.fim.compareTo(obj.inicio) <= 0) {
            return false;
        }

        /* Se a aula "b" não terminar depois da aula "a" começar então não estão em conflito */
        if (obj.fim.compareTo(this.inicio) <= 0) {
            return false;
        }

        /* Caso nada se verifique para trás é porque estão sobrepostas e, logo, entram em conflito */
        if (oo.crossLimits(this, obj)) {
            return true;
        } else {
            return false;
        }
    }

    public void changeTo(Aula aula) {
        this.dia = aula.getDia();
        this.inicio = aula.getInicio();
        this.fim = aula.getFim();
    }
    
    public String toXML() {
        String out = "";
        
        out += "\t\t\t<day>" + dia.toString() + "</day>\n";
        out += "\t\t\t<start>" + inicio.getTotalTimeInMins() + "</start>\n";
        out += "\t\t\t<finish>" + fim.getTotalTimeInMins() + "</finish>\n";
        
        return out;
    }
}
