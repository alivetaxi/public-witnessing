package com.example.pbwn.common;

public enum TimeSlot {
    MORNING("上午09:00 - 上午11:00"),
    AFTERNOON("下午03:00 - 下午05:00"),
    EVENING("下午07:00 - 下午09:00");

    private String label;

    private TimeSlot(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
