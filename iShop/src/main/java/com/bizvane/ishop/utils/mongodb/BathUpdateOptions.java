package com.bizvane.ishop.utils.mongodb;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


/**
 * Created by yanyadong on 2017/11/13.
 */
public class BathUpdateOptions {

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public boolean isUpsert() {
        return upsert;
    }

    public void setUpsert(boolean upsert) {
        this.upsert = upsert;
    }

    public BathUpdateOptions(Query query, Update update, boolean upsert, boolean multi) {
        this.query = query;
        this.update = update;
        this.upsert = upsert;
        this.multi = multi;
    }

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

    private Query query;
    private Update update;
    private boolean upsert = false;
    private boolean multi = false;
}
