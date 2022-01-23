package fr.ul.miage.ai_airline.tool;

import fr.ul.miage.ai_airline.configuration.Configuration;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Convertisseur de date.
 */
public class DateConverter {
    //Format des dates ISO 8601 utilisé dans la solution.
    public final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                                                               .withZone(ZoneId.of(Configuration.GLOBAl_CONFIGURATION
                                                                                                                .getProperty("timezone")));

    /**
     * Transformer une date en chaine de caractères
     * au format ISO 8601.
     *
     * @param date
     * @return
     */
    public static String dateToString(@NotNull Date date) {
        return dateTimeFormatter.format(new Date().toInstant());
    }

    /**
     * Transformer une chaine de caractère au format
     * ISO 8601 en date.
     *
     * @param string
     * @return
     */
    public static Date stringToDate(@NotNull String string) {
        return Date.from(dateTimeFormatter.parse(string, Instant::from));
    }
}
