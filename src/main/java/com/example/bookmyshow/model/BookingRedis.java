package com.example.bookmyshow.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("bookings")
public class BookingRedis {
    @Id
    private Long id;
    private Long movieId;
    private int numberOfTickets;

}
