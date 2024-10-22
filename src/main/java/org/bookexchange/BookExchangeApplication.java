package org.bookexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
public class BookExchangeApplication {

    public static void main(String[] args) {

        SpringApplication.run(BookExchangeApplication.class, args);
    }

}
