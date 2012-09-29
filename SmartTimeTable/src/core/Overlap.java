/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.Serializable;

/**
 *
 * @author Regateiro
 */
public class Overlap implements Serializable {
    private static final long serialVersionUID = -2389729358729328376L;
    private Aula aula;
    private Hora overTime;
    private Integer count;
    
    public Overlap(Aula a, Hora overTime, Integer count) {
        this.aula = a;
        this.overTime = overTime;
        this.count = count;
    }
    
    public void incrementCount() {
        this.count++;
    }
    
    public void incrementTime(Hora h) {
        this.overTime = new Hora(this.overTime.getTotalTimeInMins() + h.getTotalTimeInMins());
    }

    public Aula getAula() {
        return aula;
    }

    public Hora getOverTime() {
        return overTime;
    }

    public Integer getCount() {
        return count;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.aula != null ? this.aula.hashCode() : 0);
        hash = 53 * hash + (this.overTime != null ? this.overTime.hashCode() : 0);
        hash = 53 * hash + (this.count != null ? this.count.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Overlap other = (Overlap) obj;
        if (this.aula != other.aula && (this.aula == null || !this.aula.equals(other.aula))) {
            return false;
        }
        if (this.overTime != other.overTime && (this.overTime == null || !this.overTime.equals(other.overTime))) {
            return false;
        }
        if (this.count != other.count && (this.count == null || !this.count.equals(other.count))) {
            return false;
        }
        return true;
    }
}
