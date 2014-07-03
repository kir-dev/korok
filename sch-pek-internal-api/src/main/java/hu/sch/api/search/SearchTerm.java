package hu.sch.api.search;

import javax.validation.constraints.NotNull;

public class SearchTerm {

    @NotNull
    private String term;
    @NotNull
    private Mode mode;
    private int page = 0;
    private int perPage = 25;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getResultsPerPage() {
        return perPage;
    }

    public void setResultsPerPage(int perPage) {
        this.perPage = perPage;
    }

    public static enum Mode {

        USER, GROUP
    }
}
