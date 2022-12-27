package net.victo.banplugin.util;

import net.victo.banplugin.BanPlugin;
import net.victo.banplugin.model.Banishment;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Utils {

    /*
     * The standard date formatter used in the entire project.
     * */
    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /*
     * Parse the ban duration input provided by the player and return a map containing
     * the input into separated time units.
     *
     * @param input - The string to be parsed.
     * */
    public static Map<ChronoUnit, Long> parseDuration(String input) throws Exception {
        Map<ChronoUnit, Long> units = new HashMap<>();
        List<String> timeUnits = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (i == input.length() - 1) {
                current.append(ch);
                timeUnits.add(current.toString());
                break;
            }
            if (Character.isLetter(ch) && Character.isDigit(input.charAt(i + 1))) {
                current.append(ch);
                timeUnits.add(current.toString());
                current = new StringBuilder();
                continue;
            }
            current.append(ch);
        }
        for (String unit : timeUnits) {
            try {
                if (unit.endsWith("mo")) {
                    long months = Long.parseLong(unit.substring(0, unit.length() - 2));
                    units.put(ChronoUnit.MONTHS, months);
                    continue;
                }
                long value = Long.parseLong(unit.substring(0, unit.length() - 1));
                switch (unit.charAt(unit.length() - 1)) {
                    case 'w':
                        units.put(ChronoUnit.WEEKS, value);
                        break;
                    case 'd':
                        units.put(ChronoUnit.DAYS, value);
                        break;
                    case 'm':
                        units.put(ChronoUnit.MINUTES, value);
                        break;
                    case 's':
                        units.put(ChronoUnit.SECONDS, value);
                        break;
                    default:
                        throw new NumberFormatException("Not available time unit.");
                }
            } catch (Exception ex) {
                throw ex;
            }

        }
        return units;
    }

    /*
     * Return a map containing the time between 2 dates in a human-readable way.
     *
     * @param date1 - The first date (< date2)
     * @param date2 - The second date (> date1)
     * */
    public static Map<ChronoUnit, Long> getDifference(LocalDateTime date1, LocalDateTime date2) {

        long diffMillis = date2.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                - date1.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        List<ChronoUnit> units = new ArrayList<>(Arrays.asList(ChronoUnit.MONTHS, ChronoUnit.WEEKS, ChronoUnit.DAYS, ChronoUnit.HOURS, ChronoUnit.MINUTES, ChronoUnit.SECONDS));

        Map<ChronoUnit, Long> result = new LinkedHashMap<>();
        long restMillis = diffMillis;

        for (ChronoUnit unit : units) {
            long duration = unit.getDuration().toMillis();
            long delta = restMillis / duration;
            restMillis = restMillis - (delta * duration);
            result.put(unit, delta);
        }

        return result;
    }

    /*
     * Return the message that will be displayed in the banned player kick screen.
     *
     * @param ban - The player banishment
     * */
    public static String getBanMessage(Banishment ban) {
        String banned = Message.Util.of("you_are_banned", BanPlugin.instance());
        String reason = new Message.Builder().fromConfig("reason_info", BanPlugin.instance())
                .addVariable("reason", ban.getReason()).build().toString();

        String endsIn = "";

        if (ban.getExpiration() != null) {
            Map<ChronoUnit, Long> diff = Utils.getDifference(ban.getIssuedOn(), ban.getExpiration());

            endsIn = new Message.Builder().fromConfig("end_in", BanPlugin.instance())
                    .addVariable("months", ChronoUnit.MONTHS)
                    .addVariable("days", ChronoUnit.DAYS)
                    .addVariable("hours", diff.get(ChronoUnit.HOURS))
                    .addVariable("minutes", diff.get(ChronoUnit.MINUTES))
                    .addVariable("seconds", diff.get(ChronoUnit.SECONDS)).toString();
        }

        String message = banned;
        message = !ban.getReason().equals("") ? message + "\n" + reason : message;
        message = !endsIn.equals("") ? message + "\n" + endsIn : message;

        return message;
    }

}
