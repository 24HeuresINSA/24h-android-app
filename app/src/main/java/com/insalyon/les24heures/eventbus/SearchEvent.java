package com.insalyon.les24heures.eventbus;

/**
 * Created by remi on 18/01/15.
 */
public class SearchEvent {
    private String query;

    public SearchEvent(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
