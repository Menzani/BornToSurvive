package it.menzani.bts;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public abstract class SimpleComponent implements Component, Listener, CommandExecutor {
    private final BornToSurvive bornToSurvive;
    private final Set<Command> playerOnlyCommands = new HashSet<>();

    protected SimpleComponent(BornToSurvive bornToSurvive) {
        this.bornToSurvive = bornToSurvive;
    }

    protected BornToSurvive getBornToSurvive() {
        return bornToSurvive;
    }

    @Override
    public void load() {
        bornToSurvive.registerListener(this);
    }

    protected void registerCommand(String name) {
        doRegisterCommand(name);
    }

    protected void registerPlayerCommand(String name) {
        Command command = doRegisterCommand(name);
        playerOnlyCommands.add(command);
    }

    private Command doRegisterCommand(String name) {
        PluginCommand command = bornToSurvive.getCommand(name);
        command.setExecutor(this);
        return command;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String getTag() {
        return '[' + getName() + "] ";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName();
        if (playerOnlyCommands.contains(command)) {
            if (sender instanceof Player) {
                onCommand(commandName, (Player) sender, args);
            } else {
                sender.sendMessage("This command can only be used by in-game players.");
            }
        } else {
            onCommand(commandName, sender, args);
        }
        return true;
    }

    protected void onCommand(String command, CommandSender sender, String[] args) {
    }

    protected void onCommand(String command, Player sender, String[] args) {
    }
}
