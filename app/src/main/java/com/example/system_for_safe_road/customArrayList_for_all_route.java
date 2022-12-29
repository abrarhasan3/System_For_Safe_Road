package com.example.system_for_safe_road;

import com.google.android.gms.maps.model.LatLng;

public class customArrayList_for_all_route {
    public LatLng getStart() {
        return start;
    }

    public LatLng getEnd() {
        return end;
    }

    public long getIndex() {
        return index;
    }

    public long getNext() {
        return next;
    }

    public long getPrev() {
        return prev;
    }

    LatLng start;
    LatLng end;
    long index;
    long next;
    long prev;

    public customArrayList_for_all_route(LatLng start, LatLng end, long index, long next, long prev) {
        this.start = start;
        this.end = end;
        this.index = index;
        this.next = next;
        this.prev = prev;
    }


}
