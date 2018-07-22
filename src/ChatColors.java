package it.menzani.bts;

import org.bukkit.ChatColor;

public class ChatColors {
    private static final char altColorChar = '&';
    public static final String[] REFERENCE = new String[2];

    static {
        StringBuilder builder = new StringBuilder();

        builder.append(ChatColor.BLACK);
        builder.append(altColorChar);
        builder.append("0 ");

        builder.append(ChatColor.DARK_BLUE);
        builder.append(altColorChar);
        builder.append("1 ");

        builder.append(ChatColor.DARK_GREEN);
        builder.append(altColorChar);
        builder.append("2 ");

        builder.append(ChatColor.DARK_AQUA);
        builder.append(altColorChar);
        builder.append("3 ");

        builder.append(ChatColor.DARK_RED);
        builder.append(altColorChar);
        builder.append("4 ");

        builder.append(ChatColor.DARK_PURPLE);
        builder.append(altColorChar);
        builder.append("5 ");

        builder.append(ChatColor.GOLD);
        builder.append(altColorChar);
        builder.append("6 ");

        builder.append(ChatColor.GRAY);
        builder.append(altColorChar);
        builder.append("7 ");

        builder.append(ChatColor.DARK_GRAY);
        builder.append(altColorChar);
        builder.append("8 ");

        builder.append(ChatColor.BLUE);
        builder.append(altColorChar);
        builder.append("9 ");

        builder.append(ChatColor.GREEN);
        builder.append(altColorChar);
        builder.append("a ");

        builder.append(ChatColor.AQUA);
        builder.append(altColorChar);
        builder.append("b ");

        builder.append(ChatColor.RED);
        builder.append(altColorChar);
        builder.append("c ");

        builder.append(ChatColor.LIGHT_PURPLE);
        builder.append(altColorChar);
        builder.append("d ");

        builder.append(ChatColor.YELLOW);
        builder.append(altColorChar);
        builder.append("e ");

        builder.append(ChatColor.WHITE);
        builder.append(altColorChar);
        builder.append("f ");

        REFERENCE[0] = builder.toString();
        builder = new StringBuilder();

        builder.append(ChatColor.STRIKETHROUGH);
        builder.append(altColorChar);
        builder.append('m');
        builder.append(ChatColor.RESET);
        builder.append(' ');

        builder.append(ChatColor.UNDERLINE);
        builder.append(altColorChar);
        builder.append('n');
        builder.append(ChatColor.RESET);
        builder.append(' ');

        builder.append(ChatColor.BOLD);
        builder.append(altColorChar);
        builder.append("l ");

        builder.append(ChatColor.RESET);
        builder.append(altColorChar);
        builder.append('k');
        builder.append(ChatColor.MAGIC);
        builder.append(altColorChar);
        builder.append("k ");

        builder.append(ChatColor.RESET);
        builder.append(ChatColor.ITALIC);
        builder.append(altColorChar);
        builder.append("o ");

        REFERENCE[1] = builder.toString();
    }

    public static String translate(String string) {
        return ChatColor.translateAlternateColorCodes(altColorChar, string);
    }
}
