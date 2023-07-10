package com.zawisza.guitar_app;

public class Images {

    private String collection;
    private String id;
    private boolean changed;

    public Images() {
    }

    public Images(String collection, String id, boolean changed) {
        this.collection = collection;
        this.id = id;
        this.changed = changed;
    }

    public String getCollection() {
        return collection;
    }
    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public boolean isChanged() {
        return changed;
    }
    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
