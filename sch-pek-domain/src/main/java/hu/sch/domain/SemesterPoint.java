package hu.sch.domain;

import java.io.Serializable;

public class SemesterPoint implements Serializable, Comparable<SemesterPoint> {

    final private Semester semester;
    final private int point;

    public SemesterPoint(Semester semester, int point) {
        this.semester = semester;
        this.point = point;
    }

    public Semester getSemester() {
        return semester;
    }

    public int getPoint() {
        return point;
    }

    @Override
    public int compareTo(SemesterPoint o) {
        return semester.compareTo(o.semester);
    }
}