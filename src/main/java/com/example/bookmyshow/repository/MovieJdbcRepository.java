package com.example.bookmyshow.repository;

import com.example.bookmyshow.model.Movie;

import java.util.List;

public interface MovieJdbcRepository {
    void save(Movie movie);
    Movie findById(Long id);
    List<Movie> findAll();
    void delete(Long id);

    List<Movie> searchByKeyword(String keyword);
}