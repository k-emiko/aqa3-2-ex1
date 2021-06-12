package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.util.Locale;

@Data
public class UserGenerator {
    public static String generateLogin(String locale) {
        Faker faker = new Faker(Locale.forLanguageTag(locale));
        return faker.name().username();
    }

    public static String generatePassDb() {
        return "$2a$10$LDGJoVOIJSDzMps1O4ft4OT56GNYAaPzDnndEqCUHRdnxHYubpvbC";
    }

    public static String generatePassUi() {
        return "qwerty123";
    }

    public static String generateId(String locale) {
        Faker faker = new Faker(Locale.forLanguageTag(locale));
        return faker.idNumber().valid();
    }

    public static class Registration {
        public static User generateUser(String locale) {
            return new User(generateLogin(locale), generatePassUi(), generatePassDb(), generateId(locale));
        }
    }

    @Value
    @AllArgsConstructor
    public static class User {
        String login;
        String passwordUi;
        String passwordDb;
        String id;
    }
}
