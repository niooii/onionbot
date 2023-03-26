package org.niooii.listeners;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

class Main {
    public static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        final String format = "%s %s %s %s:%s:%s EST";
        long epochMillis = ZonedDateTime.parse(
                        String.format(format, sc.next(), sc.next(), sc.next(), sc.next(), sc.next(), sc.next()),
                        DateTimeFormatter.ofPattern(
                                "MMM d uuuu HH:mm:ss z",
                                Locale.ENGLISH
                        )
                )
                .toInstant()
                .toEpochMilli()/1000;
        System.out.println(epochMillis);
    }
}