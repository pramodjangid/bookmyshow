package com.example.bookmyshow.repository;

import com.example.bookmyshow.model.MovieRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Set;

@Repository
public class MovieRedisRepository {

    private final Jedis jedis;

    @Autowired
    public MovieRedisRepository(Jedis jedis) {
        this.jedis = jedis;
    }

    public MovieRedis findByTitle(String title) {
        // Get all keys in the "movies" set
        Set<String> movieKeys = jedis.smembers("movies");

        // Iterate over each movie key
        for (String movieKey : movieKeys) {
            // Get the movie's attributes from the hash
            Map<String, String> movieAttributes = jedis.hgetAll("movies:"+movieKey);

            // Check if the title matches
            if (movieAttributes.containsKey("title") && movieAttributes.get("title").equals(title)) {
                // Create a MovieRedis object from the attributes and return it
                MovieRedis movie = new MovieRedis();
                movie.setId(Long.parseLong(movieAttributes.get("id")));
                movie.setTitle(movieAttributes.get("title"));
                movie.setAvailableTickets(Integer.parseInt(movieAttributes.get("availableTickets")));
                return movie;
            }
        }

        // If no movie with the given title is found, throw an exception
        return null;
    }

    public void save(MovieRedis movie) {
        // Save movie data to Redis
        jedis.hset("movies:" + movie.getId(), "id", String.valueOf(movie.getId()));
        jedis.hset("movies:" + movie.getId(), "title", movie.getTitle());
        jedis.hset("movies:" + movie.getId(), "availableTickets", String.valueOf(movie.getAvailableTickets()));
        jedis.sadd("movies", String.valueOf(movie.getId()));
    }

    public MovieRedis findById(Long id) {
        // Retrieve movie data from Redis by id
        Map<String, String> movieData = jedis.hgetAll("movies:" + id);
        if (!movieData.isEmpty()) {
            MovieRedis movie = new MovieRedis();
            movie.setId(Long.parseLong(movieData.get("id")));
            movie.setTitle(movieData.get("title"));
            movie.setAvailableTickets(Integer.parseInt(movieData.get("availableTickets")));
            return movie;
        }
        // If no movie with the given id is found, return null or throw an exception as per your requirement
        return null;
    }
}
