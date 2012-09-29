package core;

import java.io.Serializable;

/**
 * This class is able to do simple tasks with hours.
 *
 * @author Diogo Regateiro
 */
public final class Hora implements Serializable, Comparable<Hora> {

    private static final long serialVersionUID = 3981413164564422241L;
    private int min;

    /**
     * Constructs an object given the hours and minutes.
     *
     * @param h The hour.
     * @param m The minutes.
     * @throws IllegalArgumentException if h < 0 || m < 0 || m > 59
     */
    public Hora(int h, int m) {
        if (!isValid(h, m)) {
            throw new IllegalArgumentException("Erro: Um ou mais parametros de entrada nao validos!");
        }

        this.min = (h * 60) + m;
    }

    /**
     * Constructs an object given the total number of minutes.
     *
     * @param m The minutes.
     * @throws IllegalArgumentException if m < 0
     */
    public Hora(int m) {
        if (m < 0) {
            throw new IllegalArgumentException("Erro: Um ou mais parametros de entrada nao validos!");
        }

        this.min = m;
    }

    /**
     * Returns the hour this object represents.
     *
     * @return The hour.
     */
    public int getHora() {
        return min / 60;
    }

    /**
     * Returns the minutes this object represents.
     *
     * @return The minutes.
     */
    public int getMinutes() {
        return min % 60;
    }

    /**
     * Returns the total amount of time this object represents in minutes.
     *
     * @return The minutes.
     */
    public int getTotalTimeInMins() {
        return this.min;
    }

    /**
     * This function determines the amount of time between two Hora objects
     *
     * @param h1 First Hora
     * @param h2 Second Hora
     * @return Returns a Hora object representing the time between the given
     * Hora objects.
     */
    public static Hora timeLapse(Hora h1, Hora h2) {
        int mins = Math.abs(h1.min - h2.min);
        return new Hora(mins);
    }

    /**
     * @param h The hours
     * @param m The minutes
     * @return true if values are valid, false otherwise
     */
    private boolean isValid(int h, int m) {
        if (h < 0) {
            return false;
        }

        if (m < 0 || m > 59) {
            return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        return this.min == ((Hora) obj).min;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.min;
        return hash;
    }

    @Override
    public String toString() {
        String s = "";

        s += this.min / 60;

        if (s.length() == 1) {
            s = "0" + s;
        }

        s += ":" + this.min % 60;

        if (s.length() == 4) {
            s += "0";
        }

        return s;
    }

    @Override
    public int compareTo(Hora arg0) {
        if (arg0 == null) {
            NullPointerException e = new NullPointerException("Erro: O par�metro de entrada n�o pode ser null!");
            throw e;
        }

        if (this.min < arg0.min) {
            return -1;
        }

        if (this.min > arg0.min) {
            return 1;
        }

        return 0;
    }
}
