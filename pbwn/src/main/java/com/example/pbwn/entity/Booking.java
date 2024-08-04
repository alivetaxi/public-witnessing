package com.example.pbwn.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;

import com.example.pbwn.common.Location;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Booking {
    @Id
    private Location location;
    private Map<String, List<SlotEnroll>> slotEnrolls;
    private LocalDateTime lastSlotUpdate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class SlotEnroll {
        private String date;
        private String day;
        private String slot;
        private List<String> names = new ArrayList<>();

        @JsonIgnore
        public String getKey() {
            return date + slot;
        }
    }
}
