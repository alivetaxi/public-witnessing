package com.example.pbwn.service;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.example.pbwn.common.Location;
import com.example.pbwn.common.TimeSlot;
import com.example.pbwn.entity.Booking;
import com.example.pbwn.entity.Booking.SlotEnroll;

import lombok.extern.slf4j.Slf4j;;

@Slf4j
@Service
public class BookingService {
    @Autowired
    private MongoTemplate template;

    private static final String[] WEEKDAY_TEXT = new String[] { "週一", "週二", "週三", "週四", "週五", "週六", "週日" };

    public Booking getBooking(Location location) {
        Booking exist = template.query(Booking.class)
                .matching(query(where("location").is(location)))
                .firstValue();

        LocalDateTime now = LocalDateTime.now();
        List<SlotEnroll> emptySlotEnrolls = getEmptySlotEnrolls(location, now);

        if (null != exist && exist.getLastSlotUpdate().plusDays(1).isAfter(now)) {
            return exist;
        }

        if (null == exist) {
            log.info("new time slot");
            exist = new Booking(
                    location,
                    emptySlotEnrolls.stream().collect(Collectors.groupingBy(SlotEnroll::getDate)),
                    now);
        }

        if (exist.getLastSlotUpdate().plusDays(1).isBefore(now)) {
            log.info("update time slot");
            for (SlotEnroll se : emptySlotEnrolls) {
                if (exist.getSlotEnrolls().containsKey(se.getDate())) {
                    for (SlotEnroll ese : exist.getSlotEnrolls().get(se.getDate())) {
                        if (ese.getKey().equals(se.getKey())) {
                            se.setNames(ese.getNames());
                        }
                    }
                }
            }
            exist.setSlotEnrolls(emptySlotEnrolls.stream().collect(Collectors.groupingBy(SlotEnroll::getDate)));
        }

        return template.save(exist);
    }

    private List<SlotEnroll> getEmptySlotEnrolls(Location location, LocalDateTime now) {
        Set<Integer> months = new HashSet<>();
        List<SlotEnroll> slotEnrolls = new ArrayList<>();
        LocalDateTime from = now.withDayOfMonth(1);

        while (months.size() < 4) {
            if (location.getTimeSlots().containsKey(from.getDayOfWeek())) {
                for (TimeSlot timeSlot : location.getTimeSlots().get(from.getDayOfWeek())) {
                    slotEnrolls.add(new SlotEnroll(from.toString().substring(0, 10),
                            WEEKDAY_TEXT[from.getDayOfWeek().getValue() - 1], timeSlot.getLabel(), null));
                }
            }
            from = from.plusDays(1);
            months.add(from.getMonthValue());
        }

        return slotEnrolls;
    }

    public SlotEnroll saveBooking(Location location, SlotEnroll slotEnroll) {
        Booking booking = getBooking(location);

        for (SlotEnroll se : booking.getSlotEnrolls().get(slotEnroll.getDate())) {
            if (se.getKey().equals(slotEnroll.getKey())) {
                for (String addName : slotEnroll.getNames()) {
                    if (!se.getNames().contains(addName)) {
                        se.getNames().add(addName);
                    }
                }
                break;
            }
        }

        return template.save(booking).getSlotEnrolls().get(slotEnroll.getDate()).stream()
                .filter(se -> se.getKey().equals(slotEnroll.getKey()))
                .findAny().get();
    }
}
