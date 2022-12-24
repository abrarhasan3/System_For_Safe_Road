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

    LatLng start;
    LatLng end;
    long index;

    public customArrayList_for_all_route(LatLng start, LatLng end, long index) {
        this.start = start;
        this.end = end;
        this.index = index;
    }
}
