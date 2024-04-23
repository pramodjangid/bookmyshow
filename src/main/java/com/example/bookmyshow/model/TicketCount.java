package com.example.bookmyshow.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("ticket_count")
public class TicketCount {

    private long totalTicketCancelled;

    private long totalTicketBooked;
}
