package net.victo.banplugin.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Utils {

    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static LocalDateTime plusTime(String input, LocalDateTime time) {
        Map<String, Integer> suffixToSeconds = Map.of(
                "mo", 2592000,
                "w", 604800,
                "d", 86400,
                "h", 3600,
                "m", 60,
                "s", 1);
        for (String suffix : Arrays.asList("mo", "d", "w", "d", "h", "m", "s")) {
            if (!input.contains(suffix)) {
                continue;
            }
            int index = input.indexOf(suffix);
            if (index == 0) {
                continue;
            }
            StringBuilder number = new StringBuilder("0");
            for (int i = index - 1; i > 0; i--) {
                if (Character.isDigit(input.charAt(i))) {
                    number.append(input.charAt(i));
                    continue;
                }
                break;
            }
            if(index == input.length() - 1) {
                break;
            }
            input = input.substring(index + 1, input.length());
            time = time.plusSeconds((long) suffixToSeconds.get(suffix) * Integer.parseInt(number.toString()));
        }
        return time;
    }


}
