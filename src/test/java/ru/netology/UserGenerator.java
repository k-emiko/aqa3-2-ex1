package ru.netology;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Locale;

@Data
public class UserGenerator {
    public static String generateLogin(String locale) {
        Faker faker = new Faker(Locale.forLanguageTag(locale));
        return faker.name().username();
    }

    public static String generatePassword(String locale) {
        Faker faker = new Faker(Locale.forLanguageTag(locale));
        return faker.gameOfThrones().dragon();
    }

    public static class Registration {
        public static User generateUser(String locale) {
            return new User(generateLogin(locale), generatePassword(locale));
        }
    }
    @Value
    @AllArgsConstructor
    public static class User {
        private String login;
        private String password;
    }
}
