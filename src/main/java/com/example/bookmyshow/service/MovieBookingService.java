package com.example.bookmyshow.service;
import com.example.bookmyshow.model.Booking;
import com.example.bookmyshow.model.BookingRedis;
import com.example.bookmyshow.model.Movie;
import com.example.bookmyshow.model.MovieRedis;
import com.example.bookmyshow.repository.BookingJdbcRepository;
import com.example.bookmyshow.repository.BookingRedisRepository;
import com.example.bookmyshow.repository.MovieJdbcRepository;
import com.example.bookmyshow.repository.MovieRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class MovieBookingService {

    private final MovieJdbcRepository movieJdbcRepository;
    private final BookingJdbcRepository bookingJdbcRepository;
    private final MovieRedisRepository movieRedisRepository;
    private final BookingRedisRepository bookingRedisRepository;

    private final TicketEventProducer ticketEventProducer;

    @Value("${use.jdbc}")
    private boolean useJdbc; // Switch for database selection

    @Autowired
    public MovieBookingService(
            MovieJdbcRepository movieJdbcRepository,
            BookingJdbcRepository bookingJdbcRepository,
            MovieRedisRepository movieRedisRepository,
            BookingRedisRepository bookingRedisRepository,
            TicketEventProducer ticketEventProducer
    ) {
        this.movieJdbcRepository = movieJdbcRepository;
        this.bookingJdbcRepository = bookingJdbcRepository;
        this.movieRedisRepository = movieRedisRepository;
        this.bookingRedisRepository = bookingRedisRepository;
        this.ticketEventProducer=ticketEventProducer;
    }

    public void addMovie(Movie movie) {
        if (useJdbc) {
            List<Movie> existingMovies = movieJdbcRepository.searchByKeyword(movie.getTitle());
            if (!existingMovies.isEmpty()) {
                throw new RuntimeException("Duplicate Entry for this movie.");
            }
            movieJdbcRepository.save(movie);
        } else {
            MovieRedis existingMovie = movieRedisRepository.findByTitle(movie.getTitle());
            if (existingMovie != null) {
                throw new RuntimeException("Duplicate Entry for this movie.");
            }
            MovieRedis movieRedis = new MovieRedis();
            movieRedis.setId(movie.getId());
            movieRedis.setTitle(movie.getTitle());
            movieRedis.setAvailableTickets(movie.getAvailableTickets());
            movieRedisRepository.save(movieRedis);
        }
    }


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
            MovieRedis movie = movieRedisRepository.findById(booking.getMovieId());
            if (movie != null && movie.getAvailableTickets() > 0) {
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
            ticketEventProducer.sendTicketBookedEvent(String.valueOf(booking.getNumberOfTickets()));
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
            MovieRedis movies = movieRedisRepository.findByTitle(keyword);

                if (movies!= null && movies.getAvailableTickets() > 0) {
                    movieNamesWithTickets.add(movies.getTitle() + " - Available Tickets: " + movies.getAvailableTickets());
                }
                else{
                    throw new RuntimeException("No Movie Found");
                }

        }
        return movieNamesWithTickets;
    }



    public void cancelBooking(Long id) {
        if (useJdbc) {
            Optional<Booking> bookingOptional = bookingJdbcRepository.findById(id);
            if (bookingOptional.isEmpty()) {
                throw new RuntimeException("Booking not found.");
            }
            Booking booking = bookingOptional.get();
            // Retrieve the movie associated with the booking
            Movie movie = movieJdbcRepository.findById(booking.getMovieId());
            if (movie == null) {
                throw new RuntimeException("Movie not found.");
            }
            // Update available tickets
            movie.setAvailableTickets(movie.getAvailableTickets() + booking.getNumberOfTickets());
            movieJdbcRepository.save(movie);
            // Delete the booking
            bookingJdbcRepository.delete(id);

            ticketEventProducer.sendTicketCancelledEvent(String.valueOf(booking.getNumberOfTickets()));


        } else {
            BookingRedis bookingRedis = bookingRedisRepository.findById(id);
            if (bookingRedis==null) {
                throw new RuntimeException("Booking not found.");
            }

            // Retrieve the movie associated with the booking
            MovieRedis movieRedis = movieRedisRepository.findById(bookingRedis.getMovieId());
            if (movieRedis==null) {
                throw new RuntimeException("Movie not found.");
            }

            // Update available tickets
            movieRedis.setAvailableTickets(movieRedis.getAvailableTickets() + bookingRedis.getNumberOfTickets());
            movieRedisRepository.save(movieRedis);
            // Delete the booking
            bookingRedisRepository.deleteById(id);

            ticketEventProducer.sendTicketCancelledEvent(String.valueOf(bookingRedis.getNumberOfTickets()));
        }


    }




    // Method to switch between JDBC and Redis
    public void switchDatabase(boolean useJdbc) {
        this.useJdbc = useJdbc;
    }

}
