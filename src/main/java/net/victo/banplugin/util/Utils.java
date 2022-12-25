package net.victo.banplugin.util;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Utils {

    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static LocalDateTime plusTime(String input, LocalDateTime time) throws NumberFormatException {
        List<String> timeUnits = new ArrayList<>();
        String current = "";
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (i == input.length() - 1) {
                current += ch;
                timeUnits.add(current);
                break;
            }
            if (Character.isLetter(ch) && Character.isDigit(input.charAt(i + 1))) {
                current += ch;
                timeUnits.add(current);
                current = "";
                continue;
            }
            current += ch;
        }
        for (String unit : timeUnits) {
            try {
                if (unit.endsWith("mo")) {
                    int months = Integer.parseInt(unit.substring(0, unit.length() - 2));
                    time = time.plusMonths(months);
                    continue;
                }
                int value = Integer.parseInt(unit.substring(0, unit.length() - 1));
                switch (unit.charAt(unit.length() - 1)) {
                    case 'w':
                        time = time.plusWeeks(value);
                        break;
                    case 'd':
                        time = time.plusDays(value);
                        break;
                    case 'm':
                        time = time.plusMinutes(value);
                        break;
                    case 's':
                        time = time.plusSeconds(value);
                        break;
                    default:
                        throw new NumberFormatException("Not available time unit.");
                }
            } catch (NumberFormatException ex) {
                throw ex;
            }

        }
        return time;
    }

}
