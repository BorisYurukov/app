package com.community.weare.Models;

public class Page {
    int index;
    int size;
    String searchParam1;
    String searchParam2;
    boolean hasNext;

    public Page() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSearchParam1() {
        return searchParam1;
    }

    public void setSearchParam1(String searchParam1) {
        this.searchParam1 = searchParam1;
    }

    public String getSearchParam2() {
        return searchParam2;
    }

    public void setSearchParam2(String searchParam2) {
        this.searchParam2 = searchParam2;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public void setNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
}
