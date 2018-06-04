package it.menzani.bts.chat;

import org.bukkit.ChatColor;

public final class Message {
    private Message() {
    }

    private static final String PREFIX = ChatColor.BLUE + "BTS> " + ChatColor.GRAY;

    public static String of(String base, String... important) {
        for (int i = 0; i < important.length; i++) {
            base = base.replace('{' + Integer.toString(i + 1) + '}', ChatColor.GREEN + important[i] + ChatColor.GRAY);
        }
        return PREFIX + base;
    }
}
