package com.example.bookmyshow.repository;

import com.example.bookmyshow.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingJdbcRepository {
    void save(Booking booking);
    Optional<Booking> findById(Long id);
    List<Booking> findAll();
    void delete(Long id);
}
