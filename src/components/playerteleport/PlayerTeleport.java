package it.menzani.bts.components.playerteleport;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.misc.User;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.HashMap;
import java.util.Map;

public class PlayerTeleport extends SimpleComponent {
    private final Map<User, User> requests = new HashMap<>();

    public PlayerTeleport(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @Override
    public void load() {
        super.load();
        registerPlayerCommand("teleport");

        ItemStack ticket = new Ticket().getItemStack();
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(getBornToSurvive(), "ticket"), ticket);
        recipe.addIngredient(Material.ENDER_PEARL);
        recipe.addIngredient(Material.EMERALD);
        getBornToSurvive().getServer().addRecipe(recipe);
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if (event.isRepair()) return;
        Ticket ticket = new Ticket(event.getInventory().getResult());
        if (ticket.check()) {
            ticket.mark();
        }
    }

    @Override
    protected void onCommand(String command, User sender, String[] args) {
        if (args.length == 0) {
            if (!requests.containsKey(sender)) {
                sender.sendMessageFormat("Nobody wants to teleport to you.");
                return;
            }
            User target = requests.get(sender);
            PlayerInventory inventory = target.getInventory();
            Ticket ticketInMainHand = new Ticket(inventory.getItemInMainHand());
            Ticket ticketInOffHand = new Ticket(inventory.getItemInOffHand());
            if (ticketInMainHand.check()) {
                if (ticketInMainHand.validate(sender, target)) return;
                inventory.setItemInMainHand(null);
            } else if (ticketInOffHand.check()) {
                if (ticketInOffHand.validate(sender, target)) return;
                inventory.setItemInOffHand(null);
            } else {
                sender.sendMessageFormat("{1} lost the ticket.", target);
                target.sendMessageFormat("{1} accepted your request, but you lost the ticket.", sender);
                return;
            }
            requests.remove(sender);
            boolean success = target.teleport(sender, TeleportCause.COMMAND);
            if (success) return;
            sender.sendMessageFormat("Try again.");
            target.sendMessageFormat("There was a problem. It was our fault, so we are providing you with a fresh new ticket.");
            Ticket ticket = new Ticket();
            ticket.mark();
            target.getInventory().addItem(ticket.getItemStack());
        } else {
            User target = getBornToSurvive().getUser(args[0], sender);
            if (target == null) return;
            if (target.equals(sender)) {
                sender.sendMessageFormat("Done.");
                return;
            }
            PlayerInventory inventory = sender.getInventory();
            if (!new Ticket(inventory.getItemInMainHand()).check() && !new Ticket(inventory.getItemInOffHand()).check()) {
                sender.sendMessageFormat("You must hold a " + Ticket.NAME + '.');
                return;
            }
            requests.put(target, sender);
            sender.sendMessageFormat("You requested to teleport to {1}.", target);
            target.sendMessageFormat("{1} wants to teleport to you. Type {2} to accept.", sender, "/teleport");
        }
    }
}
