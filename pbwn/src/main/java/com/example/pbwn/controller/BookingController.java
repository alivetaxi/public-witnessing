package com.example.pbwn.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pbwn.common.Location;
import com.example.pbwn.entity.Booking;
import com.example.pbwn.entity.Booking.SlotEnroll;
import com.example.pbwn.service.BookingService;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/booking")
public class BookingController {
    @Autowired
    private BookingService service;

    @PostMapping("/{lid}")
    public SlotEnroll addBooking(@PathVariable String lid, @RequestBody SlotEnroll slotEnroll) {
        Location location = Location.toLocation(lid);
        if (null == location) {
            return new SlotEnroll();
        }
        return service.saveBooking(location, slotEnroll);
    }

    @GetMapping("/{lid}")
    public Booking getBooking(@PathVariable String lid) {
        Location location = Location.toLocation(lid);
        if (null == location) {
            return new Booking();
        }
        return service.getBooking(location);
    }

    @GetMapping("/locations")
    public Map<String, String> getLocations() {
        return Arrays.stream(Location.values()).collect(Collectors.toMap(Location::name, Location::getLabel));
    }
}