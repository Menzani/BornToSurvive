package it.menzani.bts.components.playername;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.ChatColors;
import it.menzani.bts.User;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.persistence.sql.wrapper.Value;
import it.menzani.bts.persistence.sql.wrapper.WrappedSQLDatabase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;

public class PlayerName extends SimpleComponent {
    private PreparedStatement setNameStatement, resetNameStatement, getNameStatement;

    public PlayerName(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @Override
    public void loadPreWorld() {
        WrappedSQLDatabase database = getBornToSurvive().getDatabase();

        Value<PreparedStatement[]> preparedStatements = database.submit(new PrepareStatements(), this);
        if (preparedStatements == null) return;
        setNameStatement = preparedStatements.get()[0];
        resetNameStatement = preparedStatements.get()[1];
        getNameStatement = preparedStatements.get()[2];

        boolean error = database.execute(new CreateTable(), this);
        if (error) return;

        super.loadPreWorld();
    }

    @Override
    public void load() {
        registerPlayerCommand("nick");
    }

    @Override
    protected void onCommand(String command, User sender, String[] args) {
        if (args.length == 0) {
            Value<Integer> updateCount = getBornToSurvive().getDatabase().submit(new ResetName(resetNameStatement, sender.getUniqueId()), this);
            if (updateCount == null) return;

            switch (updateCount.get()) {
                case 0:
                    sender.sendMessageFormat("A nickname was not set.");
                    break;
                case 1:
                    resetDisplayName(sender);
                    sender.sendMessageFormat("Your nickname has been reset to match your name.");
                    break;
            }
        } else {
            String name = ChatColors.translate(args[0]);
            if (!ChatColor.stripColor(name).equals(sender.getName())) {
                sender.sendMessageFormat("You may only specify your name formatted with codes:");
                sender.sendMessage(ChatColors.REFERENCE);
                return;
            }
            if (name.length() > 36) {
                sender.sendMessageFormat("The nickname specified is too long.");
                return;
            }

            Value<?> updateCount = getBornToSurvive().getDatabase().submit(new SetName(setNameStatement, sender.getUniqueId(), name), this);
            if (updateCount == null) return;

            setDisplayName(sender, name);
            sender.sendMessageFormat("Your nickname has been updated to {1}.", sender);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Value<String> name = getBornToSurvive().getDatabase().submit(new GetName(getNameStatement, player.getUniqueId()), this);
        if (name == null) return;

        if (name.isNull()) {
            resetDisplayName(player);
        } else {
            setDisplayName(player, name.get());
        }
    }

    private static void setDisplayName(Player player, String name) {
        player.setDisplayName(name);
        player.setPlayerListName(name);
    }

    private static void resetDisplayName(Player player) {
        player.setDisplayName(ChatColor.YELLOW + player.getName());
        player.setPlayerListName(player.getName());
    }
}
