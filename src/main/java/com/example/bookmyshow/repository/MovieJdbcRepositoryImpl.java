package com.example.bookmyshow.repository;


import com.example.bookmyshow.model.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class MovieJdbcRepositoryImpl implements MovieJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MovieJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Movie movie) {
        String sql = "INSERT INTO movies (title, available_tickets) VALUES (?, ?)";
        jdbcTemplate.update(sql, movie.getTitle(), movie.getAvailableTickets());
    }

    @Override
    public Movie findById(Long id) {
        String sql = "SELECT * FROM movies WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
            Movie movie = new Movie();
            movie.setId(rs.getLong("id"));
            movie.setTitle(rs.getString("title"));
            movie.setAvailableTickets(rs.getInt("available_tickets"));
            return movie;
        });
    }

    @Override
    public List<Movie> findAll() {
        String sql = "SELECT * FROM movies";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Movie movie = new Movie();
            movie.setId(rs.getLong("id"));
            movie.setTitle(rs.getString("title"));
            movie.setAvailableTickets(rs.getInt("available_tickets"));
            return movie;
        });
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM movies WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Movie> searchByKeyword(String keyword) {
        String sql = "SELECT * FROM movies WHERE title LIKE ?";
        String searchKeyword = "%" + keyword + "%";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Movie movie = new Movie();
            movie.setId(rs.getLong("id"));
            movie.setTitle(rs.getString("title"));
            movie.setAvailableTickets(rs.getInt("available_tickets"));
            return movie;
        }, searchKeyword);
    }
}
