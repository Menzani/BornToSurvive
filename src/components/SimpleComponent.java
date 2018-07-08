package it.menzani.bts.components;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.User;
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

public abstract class SimpleComponent extends SimpleComponentListener implements Component {
    private final BornToSurvive bornToSurvive;
    private final Logger logger;
    private final CommandExecutorImpl commandExecutor = new CommandExecutorImpl();

    protected SimpleComponent(BornToSurvive bornToSurvive) {
        this.bornToSurvive = bornToSurvive;

        LoggerGroup loggerGroup = ((LoggerGroup) bornToSurvive.getRootLogger()).clone();
        MessageFormatter formatter = new TagFormatter(getName());
        ((PipelineLogger) loggerGroup.getLogger("bukkit"))
                .getPipeline("")
                .setFormatter(formatter);
        ((PipelineLogger) loggerGroup.getLogger("file"))
                .getPipeline("")
                .setFormatter(new TimestampFormatter(new LevelFormatter(formatter)));
        logger = loggerGroup;
    }

    @Override
    protected BornToSurvive getBornToSurvive() {
        return bornToSurvive;
    }

    @Override
    public void loadPreWorld() {
        register();
    }

    @Override
    public void load() {
    }

    @Override
    public void unload() {
    }

    protected void registerCommand(String name) {
        commandExecutor.registerCommand(name);
    }

    protected void registerPlayerCommand(String name) {
        Command command = commandExecutor.registerCommand(name);
        commandExecutor.playerOnlyCommands.add(command);
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

    private class CommandExecutorImpl implements CommandExecutor {
        private final Set<Command> playerOnlyCommands = new HashSet<>();

        private Command registerCommand(String name) {
            PluginCommand command = bornToSurvive.getCommand(name);
            command.setExecutor(this);
            return command;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            String commandName = command.getName();
            if (playerOnlyCommands.contains(command)) {
                if (sender instanceof Player) {
                    SimpleComponent.this.onCommand(commandName, new User((Player) sender), args);
                } else {
                    sender.sendMessage("This command can only be used by in-game players.");
                }
            } else {
                SimpleComponent.this.onCommand(commandName, sender, args);
            }
            return true;
        }
    }
}
