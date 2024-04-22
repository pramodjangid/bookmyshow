package com.example.bookmyshow.controller;

import com.example.bookmyshow.model.Booking;
import com.example.bookmyshow.model.Movie;
import com.example.bookmyshow.service.MovieBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/movies")
public class MovieBookingController {

    @Autowired
    private final MovieBookingService movieBookingService;


    public MovieBookingController(MovieBookingService movieBookingService) {
        this.movieBookingService = movieBookingService;
    }

    @PostMapping("/add")
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie) {
        movieBookingService.addMovie(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(movie);
    }



    @PostMapping("/book")
    public ResponseEntity<Booking> bookTicket(@RequestBody Booking booking) {
        movieBookingService.bookTicket(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }


    @GetMapping("/search")
    public ResponseEntity<Set<String>> searchMovieAndAvailableTickets(@RequestParam String keyword) {
        Set<String> movieNamesWithTickets = movieBookingService.searchMovieAndAvailableTickets(keyword);
        return ResponseEntity.ok().body(movieNamesWithTickets);
    }


    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        movieBookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint to switch between JDBC and Redis
    @PutMapping("/switch-database")
    public ResponseEntity<Void> switchDatabase(@RequestParam boolean useJdbc) {
        movieBookingService.switchDatabase(useJdbc);
        return ResponseEntity.ok().build();
    }
}
