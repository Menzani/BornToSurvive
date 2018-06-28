package it.menzani.bts.components;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.User;
import it.menzani.logger.Pipeline;
import it.menzani.logger.api.Logger;
import it.menzani.logger.api.PipelineLogger;
import it.menzani.logger.impl.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public abstract class SimpleComponent extends SimpleComponentListener implements Component, CommandExecutor {
    private final BornToSurvive bornToSurvive;
    private final Logger logger;
    private final Set<Command> playerOnlyCommands = new HashSet<>();

    protected SimpleComponent(BornToSurvive bornToSurvive) {
        this.bornToSurvive = bornToSurvive;

        LoggerGroup loggerGroup = ((LoggerGroup) bornToSurvive.getRootLogger()).clone();
        MessageFormatter formatter = new TagFormatter(getName());
        firstPipeline(loggerGroup, 0).setFormatter(formatter);
        firstPipeline(loggerGroup, 1).setFormatter(new TimestampFormatter(new LevelFormatter(formatter)));
        logger = loggerGroup;
    }

    private static Pipeline firstPipeline(LoggerGroup loggerGroup, int index) {
        assert index == 0 || index == 1;
        PipelineLogger logger = (PipelineLogger) loggerGroup.getLoggers().get(index);
        return logger.getPipelines().get(0);
    }

    @Override
    protected BornToSurvive getBornToSurvive() {
        return bornToSurvive;
    }

    @Override
    public void load() {
        register();
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
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void register() {
        bornToSurvive.registerListener(this);
    }

    @Override
    public SimpleComponent getComponent() {
        return this;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName();
        if (playerOnlyCommands.contains(command)) {
            if (sender instanceof Player) {
                onCommand(commandName, new User((Player) sender), args);
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

    protected void onCommand(String command, User sender, String[] args) {
    }

    public ComponentTask newWrappedRunnableTask(Runnable runnable) {
        return new SimpleComponentTask(this) {
            public void run() {
                runnable.run();
            }
        };
    }
}
