package net.victo.banplugin.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


public class Message {
    private List<String> lines;
    private Map<String, Object> variables;

    public Message(List<String> lines, Map<String, Object> variables) {
        this.variables = variables;
        this.lines = lines.stream().map(this::replaceVariables).collect(Collectors.toList());
    }

    public List<String> getLines() {
        return lines;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void send(CommandSender player) {
        lines.forEach(line -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', line)));
    }

    @Override
    public String toString() {
        List<String> replaced = lines.stream()
                .map(this::replaceVariables)
                .map(str -> ChatColor.translateAlternateColorCodes('&', str))
                .collect(Collectors.toList());
        return String.join("\n", replaced);
    }

    public String replaceVariables(String line) {
        AtomicReference<String> currentLine = new AtomicReference<>(line);
        variables.entrySet().forEach(entry -> {
            if (line.contains("{" + entry.getKey() + "}")) {
                currentLine.set(currentLine.get().replace("{" + entry.getKey() + "}", entry.getValue().toString()));
            }
        });
        return currentLine.get();
    }

    public static class Util {
        public static String of(String path,Plugin plugin) {
            FileConfiguration config = plugin.getConfig();
            return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("message." + path)));
        }

        public static List<String> ofLines(String path, Plugin plugin) {
            FileConfiguration config = plugin.getConfig();
            return config.getStringList("message." + path);
        }
    }

    public static class Builder {
        List<String> lines = new ArrayList<>();
        private Map<String, Object> variables = new HashMap<>();

        public Builder addLine(String line) {
            lines.add(line);
            return this;
        }

        public Builder addLines(String... lines) {
            this.lines.addAll(Arrays.asList(lines));
            return this;
        }

        public Builder addLines(List<String> lines) {
            this.lines.addAll(lines);
            return this;
        }

        public Builder fromConfig(String path, Plugin plugin) {
            FileConfiguration config = plugin.getConfig();
            path = "message." + path;
            if(config.get(path) instanceof List) {
                addLines(config.getStringList(path));
            } else {
                addLine(config.getString(path));
            }
            return this;
        }

        public Builder addVariable(String var, Object value) {
            variables.put(var, value);
            return this;
        }

        public Message build() {
            return new Message(lines, variables);
        }
    }
}
