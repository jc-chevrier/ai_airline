package fr.ul.miage.ai_airline.tool;

import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Formateur de date.
 */
public class DateFormatter {
    /**
     * Formatter une date.
     *
     * @param date
     * @return
     */
    public static String formatAtISO8601(@NotNull Date date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                                               .withZone(ZoneId.of("Europe/Paris"));
        return dateTimeFormatter.format(new Date().toInstant());
    }
}
