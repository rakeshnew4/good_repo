package com.example.test_app.utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class random_ip_select {


        public static String get_number(List existingIntegers) {
            int randomInteger;
            int max = 230;
            int min = 111;
            Random random = new Random();

            do {
                randomInteger = random.nextInt(max - min + 1) + min; // Generates a random integer between 111 and 199 (inclusive)

            } while (existingIntegers.contains(randomInteger));

            System.out.println("Random integer: " + randomInteger);
            return Integer.toString(randomInteger);
        }

}
