package com.example.recipegeneratorapplication;

import static org.junit.Assert.*;

import org.junit.Test;

public class userTest {
    @Test
    public void userTesting() {
        User testUser = new User();

        testUser.setEmail("test@gmail.com");
        testUser.setUsername("test");
        testUser.setPassword("testpass");
        testUser.setVegan(true);
        testUser.setGlutenFree(false);
        testUser.setVegetarian(true);

        // Test User Class input
        assertNotNull(testUser.getEmail());
        assertNotNull(testUser.getUsername());
        assertNotNull(testUser.getPassword());
        assertNotNull(testUser.isGlutenFree());
        assertNotNull(testUser.isVegan());
        assertNotNull(testUser.isVegetarian());
    }
}
