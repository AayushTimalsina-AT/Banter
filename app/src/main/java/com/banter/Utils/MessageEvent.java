package com.banter.Utils;

public class MessageEvent {
    String searchText;

    public MessageEvent(String searchText) {
        this.searchText = searchText;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
