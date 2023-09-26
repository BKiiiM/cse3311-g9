package com.example.recipegeneratorapplication;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class emailTest {
    @Test
    public void emailTesting() {
        // Using Reg Ex, tokenize a string to fit the format _____@_____.com
        String input = "test@test.com";
        String regex = "^(\\w+)@(\\w+)\\.com$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        assertTrue(matcher.matches());
    }
}
