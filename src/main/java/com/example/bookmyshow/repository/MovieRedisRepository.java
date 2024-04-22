package com.example.bookmyshow.repository;

import com.example.bookmyshow.model.Movie;
import com.example.bookmyshow.model.MovieRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRedisRepository extends CrudRepository<MovieRedis, Long> {

    List<MovieRedis> searchByKeyword(String keyword);

    List<MovieRedis> findByTitleContaining(String keyword);
}
