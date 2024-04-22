package com.example.bookmyshow.repository;

import com.example.bookmyshow.model.BookingRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRedisRepository extends CrudRepository<BookingRedis, Long> {

}
