package com.example.bookmyshow.repository;

import com.example.bookmyshow.model.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingJdbcRepositoryImpl implements BookingJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookingJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Booking booking) {
        String sql = "INSERT INTO bookings (movie_id, number_of_tickets) VALUES (?, ?)";
        jdbcTemplate.update(sql, booking.getMovieId(), booking.getNumberOfTickets());
    }

    @Override
    public Optional<Booking> findById(Long id) {
        String sql = "SELECT * FROM bookings WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
                Booking booking = new Booking();
                booking.setId(rs.getLong("id"));
                booking.setMovieId(rs.getLong("movie_id"));
                booking.setNumberOfTickets(rs.getInt("number_of_tickets"));
                return booking;
            }));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Booking> findAll() {
        String sql = "SELECT * FROM bookings";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Booking booking = new Booking();
            booking.setId(rs.getLong("id"));
            booking.setMovieId(rs.getLong("movie_id"));
            booking.setNumberOfTickets(rs.getInt("number_of_tickets"));
            return booking;
        });
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM bookings WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
