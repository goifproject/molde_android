package com.limefriends.molde.networking.schema.search;

import java.util.List;

public class SearchSchemaList {

    private List<SearchSchema> poi = null;

    public List<SearchSchema> getSearchSchema() {
        return poi;
    }

    public void setSearchSchema(List<SearchSchema> poi) {
        this.poi = poi;
    }
}
