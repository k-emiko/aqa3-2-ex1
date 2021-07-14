package ru.netology.data;

public class CodeGenerator {
    public static String generateInvalidCode() {
        return String.valueOf(Math.floor(Math.random() * (999999 - 100000 + 1) + 100000));
    }
}
