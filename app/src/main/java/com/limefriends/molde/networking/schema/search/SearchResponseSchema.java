package com.limefriends.molde.networking.schema.search;


public class SearchResponseSchema {
    private String totalCount;
    private String count;
    private String page;
    private SearchSchemaList pois;

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public SearchSchemaList getSearchSchemaList() {
        return pois;
    }

    public void setSearchSchemaList(SearchSchemaList pois) {
        this.pois = pois;
    }
}
