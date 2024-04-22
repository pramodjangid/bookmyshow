package com.example.bookmyshow.service;
import com.example.bookmyshow.model.Booking;
import com.example.bookmyshow.model.BookingRedis;
import com.example.bookmyshow.model.Movie;
import com.example.bookmyshow.model.MovieRedis;
import com.example.bookmyshow.repository.BookingJdbcRepository;
import com.example.bookmyshow.repository.BookingRedisRepository;
import com.example.bookmyshow.repository.MovieJdbcRepository;
import com.example.bookmyshow.repository.MovieRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MovieBookingService {

    private final MovieJdbcRepository movieJdbcRepository;
    private final BookingJdbcRepository bookingJdbcRepository;
    private final MovieRedisRepository movieRedisRepository;
    private final BookingRedisRepository bookingRedisRepository;

    @Value("${use.jdbc}")
    private boolean useJdbc; // Switch for database selection

    @Autowired
    public MovieBookingService(
            MovieJdbcRepository movieJdbcRepository,
            BookingJdbcRepository bookingJdbcRepository,
            MovieRedisRepository movieRedisRepository,
            BookingRedisRepository bookingRedisRepository
    ) {
        this.movieJdbcRepository = movieJdbcRepository;
        this.bookingJdbcRepository = bookingJdbcRepository;
        this.movieRedisRepository = movieRedisRepository;
        this.bookingRedisRepository = bookingRedisRepository;
    }

    public void addMovie(Movie movie) {
        if (useJdbc) {
            List<Movie> existingMovies = movieJdbcRepository.searchByKeyword(movie.getTitle());
            if (!existingMovies.isEmpty()) {
                throw new RuntimeException("Duplicate Entry for this movie.");
            }
            movieJdbcRepository.save(movie);
        } else {
            MovieRedis movieRedis = new MovieRedis();
            movieRedis.setId(movie.getId());
            movieRedis.setTitle(movie.getTitle());
            movieRedis.setAvailableTickets(movie.getAvailableTickets());
            movieRedisRepository.save(movieRedis);
        }
    }

//    public void addMovie(Movie movie) {
//        if (useJdbc) {
//            movieJdbcRepository.save(movie);
//        } else {
//            MovieRedis movieRedis = new MovieRedis();
//            movieRedis.setId(movie.getId());
//            movieRedis.setTitle(movie.getTitle());
//            movieRedis.setAvailableTickets(movie.getAvailableTickets());
//            movieRedisRepository.save(movieRedis);
//        }
//    }



    public void bookTicket(Booking booking) {
        if (useJdbc) {
            Movie movie = movieJdbcRepository.findById(booking.getMovieId());
            if (movie != null && movie.getAvailableTickets() > 0) {
                // Decrease available tickets count
                movie.setAvailableTickets(movie.getAvailableTickets() - booking.getNumberOfTickets());
                movieJdbcRepository.save(movie);
                // Save booking
                bookingJdbcRepository.save(booking);
            } else {
                throw new RuntimeException("No available tickets for this movie.");
            }
        } else {
            Optional<MovieRedis> movieRedisOptional = movieRedisRepository.findById(booking.getMovieId());
            if (movieRedisOptional.isPresent()) {
                MovieRedis movie = movieRedisOptional.get();
                if (movie.getAvailableTickets() > 0) {
                    // Decrease available tickets count
                    movie.setAvailableTickets(movie.getAvailableTickets() - booking.getNumberOfTickets());
                    movieRedisRepository.save(movie);
                    // Save booking
                    BookingRedis bookingRedis = new BookingRedis();
                    bookingRedis.setId(booking.getId());
                    bookingRedis.setMovieId(booking.getMovieId());
                    bookingRedis.setNumberOfTickets(booking.getNumberOfTickets());
                    bookingRedisRepository.save(bookingRedis);
                } else {
                    throw new RuntimeException("No available tickets for this movie.");
                }
            } else {
                throw new RuntimeException("Movie not found.");
            }
        }
    }

    public Set<String> searchMovieAndAvailableTickets(String keyword) {
        Set<String> movieNamesWithTickets = new HashSet<>();
        if (useJdbc) {
            List<Movie> movies = movieJdbcRepository.searchByKeyword(keyword);
            for (Movie movie : movies) {
                if (movie.getAvailableTickets() > 0) {
                    movieNamesWithTickets.add(movie.getTitle() + " - Available Tickets: " + movie.getAvailableTickets());
                }
            }
        } else {
            List<MovieRedis> movies = movieRedisRepository.findByTitleContaining(keyword);
            for (MovieRedis movie : movies) {
                if (movie.getAvailableTickets() > 0) {
                    movieNamesWithTickets.add(movie.getTitle() + " - Available Tickets: " + movie.getAvailableTickets());
                }
            }
        }
        return movieNamesWithTickets;
    }



    public void cancelBooking(Long id) {
        if (useJdbc) {
            bookingJdbcRepository.delete(id);
        } else {
            bookingRedisRepository.deleteById(id);
        }
    }

    // Method to switch between JDBC and Redis
    public void switchDatabase(boolean useJdbc) {
        this.useJdbc = useJdbc;
    }





}
