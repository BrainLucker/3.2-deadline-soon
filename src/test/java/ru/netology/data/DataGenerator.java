package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;
import lombok.val;

public class DataGenerator {
    private DataGenerator() {
    }

    public static String generateId() {
        val faker = new Faker();
        return faker.number().digits(36);
    }

    public static String generateLogin() {
        val faker = new Faker();
        return faker.name().username();
    }

    public static String generatePassword() {
        return "qwerty123";
    }

    public static class Registration {
        private Registration() {
        }

        public static UserInfo generateActiveUser() {
            return new UserInfo(generateId(), generateLogin(), generatePassword(), "active");
        }
    }

    @Value
    public static class UserInfo {
        String id;
        String login;
        String password;
        String status;
    }
}