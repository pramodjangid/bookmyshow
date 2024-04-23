package com.example.bookmyshow.repository;

import com.example.bookmyshow.model.BookingRedis;
import com.example.bookmyshow.model.MovieRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

import java.util.Map;

@Repository
public class BookingRedisRepository {

    @Autowired
    private Jedis jedis;

    public void save(BookingRedis bookingRedis) {
        jedis.hset("bookings:" + bookingRedis.getId(), "id", String.valueOf(bookingRedis.getId()));
        jedis.hset("bookings:" + bookingRedis.getId(), "movieId", String.valueOf(bookingRedis.getMovieId()));
        jedis.hset("bookings:" + bookingRedis.getId(), "numberOfTickets", String.valueOf(bookingRedis.getNumberOfTickets()));
        jedis.sadd("bookings", String.valueOf(bookingRedis.getId()));
    }

    public void deleteById(Long id) {
        jedis.del("bookings:" + id);
        jedis.srem("bookings", String.valueOf(id));
    }

    public BookingRedis findById(Long id) {
        // Retrieve movie data from Redis by id
        Map<String, String> bookingData = jedis.hgetAll("bookings:" + id);
        if (!bookingData.isEmpty()) {
            BookingRedis booking = new BookingRedis();
            booking.setId(Long.parseLong(bookingData.get("id")));
            booking.setMovieId(Long.parseLong(bookingData.get("movieId")));
            booking.setNumberOfTickets(Integer.parseInt(bookingData.get("numberOfTickets")));

            return booking;
        }
        // If no movie with the given id is found, return null or throw an exception as per your requirement
        return null;
    }
}
