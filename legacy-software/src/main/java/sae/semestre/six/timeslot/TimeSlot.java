package sae.semestre.six.timeslot;

import java.util.Date;

public class TimeSlot {
    private Date start;
    private Date end;

    public TimeSlot(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "De " + start + " à " + end;
    }
}

