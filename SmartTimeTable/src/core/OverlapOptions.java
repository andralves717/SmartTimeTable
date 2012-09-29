/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Regateiro
 */
public class OverlapOptions implements Serializable {
    private static final long serialVersionUID = -3874692394807234687L;
    private OverlapMode overlapmode;
    private Hora overlaptime_lim;
    private Integer overlapcount_lim;
    private Hora overlappingTime;
    private Integer overlappingCount;
    private Map<Aula, Overlap> overlaps;

    public OverlapOptions(OverlapMode overlapMode, Integer count, Hora time) {
        this.overlapmode = overlapMode;        
        this.overlapcount_lim = count;
        this.overlaptime_lim = time;
        this.overlappingCount = 0;
        this.overlappingTime = new Hora(0);
        this.overlaps = new HashMap<Aula, Overlap>();
    }

    public OverlapMode getOverlapmode() {
        return overlapmode;
    }

    public Hora getOverlaptime() {
        return overlaptime_lim;
    }

    public Integer getOverlapcount() {
        return overlapcount_lim;
    }
    
    public boolean crossLimits(Aula a, Aula b) {
        if(overlapmode == OverlapMode.NONE) {
            return true;
        }
        
        Integer limCount;
        Hora limTime, temp, overtime;
        
        if(!overlaps.containsKey(a)) {
            overlaps.put(a, new Overlap(a, new Hora(0), 0));
        }
        
        if ((limCount = getOverlapcount()) != null) {
            if (overlappingCount < limCount) {
                overlappingCount++;
                overlaps.get(a).incrementCount();
            } else {
                overlappingCount -= overlaps.get(a).getCount();
                overlaps.remove(a);
                return true;
            }
        }
        
        if((limTime = getOverlaptime()) != null) {
            if(a.getInicio().compareTo(b.getInicio()) < 0) {
                overtime = Hora.timeLapse(a.getFim(), b.getInicio());
                temp = new Hora(overlappingTime.getTotalTimeInMins() + overtime.getTotalTimeInMins());
            } else {
                overtime = Hora.timeLapse(a.getInicio(), b.getFim());
                temp = new Hora(overlappingTime.getTotalTimeInMins() + overtime.getTotalTimeInMins());
            }
            
            if(temp.compareTo(limTime) <= 0) {
                overlappingTime = temp;
                overlaps.get(a).incrementTime(overtime);
            } else {
                // Se isto falhou é preciso decrementar o count se este não for null nas opções
                if (getOverlapcount() != null) {
                    overlappingCount -= overlaps.get(a).getCount();
                }
                overlappingTime = new Hora(overlappingTime.getTotalTimeInMins() - overlaps.get(a).getOverTime().getTotalTimeInMins());
                overlaps.remove(a);
                return true;
            }
        }
        
        return false;
    }
    
    public void removeFromLim(Turma t) {
        for(Aula a : t.toArray()) {
            if(overlaps.containsKey(a)) {
                overlappingCount -= overlaps.get(a).getCount();
                overlappingTime = new Hora(overlappingTime.getTotalTimeInMins() - overlaps.get(a).getOverTime().getTotalTimeInMins());
                overlaps.remove(a);
            }
        }
    }
    
    public void resetLim() {
        this.overlappingCount = 0;
        this.overlappingTime = new Hora(0);
        this.overlaps.clear();
    }
    
    public String toXML() {
        String out = "";
        
        out += "\t<overlapping>\n";
        out += "\t\t<mode>" + overlapmode.toString() + "</mode>\n";
        out += "\t\t<count>" + overlapcount_lim + "</count>\n";
        out += "\t\t<time>";
        if(overlaptime_lim == null) {
            out += "null";
        } else {
            out += overlaptime_lim.getTotalTimeInMins();
        }
        out += "</time>\n";
        out += "\t</overlapping>\n";
        
        return out;
    }
}
