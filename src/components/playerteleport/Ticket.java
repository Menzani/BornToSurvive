package it.menzani.bts.components.playerteleport;

import it.menzani.bts.misc.User;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

class Ticket {
    static final String NAME = ChatColor.RESET + "Teleportation Ticket";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    private final ItemStack stack;
    private ItemMeta meta;

    Ticket() {
        this(new ItemStack(Material.PAPER));
        meta = stack.getItemMeta();
        meta.setDisplayName(NAME);
        stack.setItemMeta(meta);
    }

    Ticket(ItemStack stack) {
        this.stack = stack;
    }

    ItemStack getItemStack() {
        return stack;
    }

    boolean check() {
        if (stack == null || !stack.hasItemMeta()) return false;
        meta = stack.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().equals(NAME);
    }

    void mark() {
        ZonedDateTime expiryDate = now().plusHours(12);
        meta.setLore(List.of("", ChatColor.RESET + "Valid until " + expiryDate.format(FORMATTER)));
        stack.setItemMeta(meta);
    }

    boolean validate(User sender, User target) {
        String validUntil = meta.getLore().get(1).substring(14);
        ZonedDateTime expiryDate = ZonedDateTime.parse(validUntil, FORMATTER);
        if (expiryDate.isBefore(now())) {
            sender.sendMessageFormat("The ticket has expired.");
            target.sendMessageFormat("Your ticket has expired.");
            return true;
        }
        return false;
    }

    private static ZonedDateTime now() {
        return ZonedDateTime.now(ZoneOffset.UTC);
    }
}
