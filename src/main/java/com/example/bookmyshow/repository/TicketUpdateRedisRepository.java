package com.example.bookmyshow.repository;

import com.example.bookmyshow.model.TicketCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

@Repository
public class TicketUpdateRedisRepository {


    private final Jedis jedis;

    public TicketUpdateRedisRepository(Jedis jedis) {
        this.jedis = jedis;
    }


    public long totalBookedTicket(long ticketCount){
        return jedis.hincrBy("ticket_count","totalTicketBooked",ticketCount);
    }

    public long totalCanceledTicket(long ticketCount){
        return jedis.hincrBy("ticket_count","totalTicketCancelled",ticketCount);
    }
}
