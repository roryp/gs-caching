package com.example.caching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

    private final BookRepository bookRepository;
    private final CacheManager cacheManager;

    public AppRunner(BookRepository bookRepository, CacheManager cacheManager) {
        this.bookRepository = bookRepository;
        this.cacheManager = cacheManager;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info(".... Fetching books");
        logger.info("isbn-1234 -->" + getByIsbn("isbn-1234"));
        logger.info("isbn-4567 -->" + getByIsbn("isbn-4567"));
        logger.info("isbn-1234 -->" + getByIsbn("isbn-1234"));
        logger.info("isbn-4567 -->" + getByIsbn("isbn-4567"));
        logger.info("isbn-1234 -->" + getByIsbn("isbn-1234"));
        logger.info("isbn-1234 -->" + getByIsbn("isbn-1234"));

		Cache cache = cacheManager.getCache("books");

        // Evict the cache
        cache.evict("isbn-1234");
		System.out.println("cache: " + cache);

		//invalidate the cache
		cache.invalidate();
		System.out.println("cache: " + cache);

    }

    private Book getByIsbn(String isbn) {
        Book book = cacheManager.getCache("books").get(isbn, Book.class);
        if (book == null) {
            book = bookRepository.getByIsbn(isbn);
            cacheManager.getCache("books").put(isbn, book);
        }
        return book;
    }
}