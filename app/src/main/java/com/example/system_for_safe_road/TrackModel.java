package com.example.system_for_safe_road;

public class TrackModel {
    String address, predicted_time, arrival_time, late_not;

    public TrackModel(String address, String predicted_time, String arrival_time, String late_not) {
        this.address = address;
        this.predicted_time = predicted_time;
        this.arrival_time = arrival_time;
        this.late_not = late_not;
    }

    public String getAddress() {
        return address;
    }

    public String getPredicted_time() {
        return predicted_time;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public String getLate_not() {
        return late_not;
    }
}
