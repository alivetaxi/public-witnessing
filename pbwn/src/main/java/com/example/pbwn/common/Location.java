package com.example.pbwn.common;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public enum Location {
    A("九份", Map.of(
            DayOfWeek.FRIDAY, List.of(TimeSlot.MORNING, TimeSlot.AFTERNOON),
            DayOfWeek.SATURDAY, List.of(TimeSlot.MORNING, TimeSlot.AFTERNOON))),
    B("站前廣場", Map.of(
            DayOfWeek.WEDNESDAY, List.of(TimeSlot.MORNING),
            DayOfWeek.FRIDAY, List.of(TimeSlot.MORNING, TimeSlot.AFTERNOON),
            DayOfWeek.SATURDAY, List.of(TimeSlot.MORNING, TimeSlot.AFTERNOON))),
    C("基隆南車站", Map.of(
            DayOfWeek.WEDNESDAY, List.of(TimeSlot.EVENING),
            DayOfWeek.SATURDAY, List.of(TimeSlot.AFTERNOON))),
    D("瑞芳圖書館", Map.of(
            DayOfWeek.WEDNESDAY, List.of(TimeSlot.MORNING),
            DayOfWeek.FRIDAY, List.of(TimeSlot.MORNING, TimeSlot.AFTERNOON),
            DayOfWeek.SATURDAY, List.of(TimeSlot.MORNING, TimeSlot.AFTERNOON))),
    E("循環站", Map.of(
            DayOfWeek.WEDNESDAY, List.of(TimeSlot.EVENING)));

    private String label;
    private Map<DayOfWeek, List<TimeSlot>> timeSlots;

    private Location(String label, Map<DayOfWeek, List<TimeSlot>> timeSlots) {
        this.label = label;
        this.timeSlots = timeSlots;
    }

    public String getLabel() {
        return this.label;
    }

    public Map<DayOfWeek, List<TimeSlot>> getTimeSlots() {
        return this.timeSlots;
    }

    public static Location toLocation(String lid) {
        if (StringUtils.isBlank(lid)) {
            return null;
        }

        for (Location location : Location.values()) {
            if (location.toString().equals(lid)) {
                return location;
            }
        }

        return null;
    }
}
