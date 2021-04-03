package net.cloud.autosell.utils;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderReplacer {

    private Map<String, String> placeholders;
    public PlaceholderReplacer() {
        placeholders = new HashMap<>();
    }

    public PlaceholderReplacer addPlaceholder(String key, String value) {
        placeholders.put(key, value);
        return this;
    }

    public String parse(String args) {
        for(String key : placeholders.keySet()) {
            String value = placeholders.get(key);
            args = args.replace(key, value);
        }
        return args;
    }

}
