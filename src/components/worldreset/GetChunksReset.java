package it.menzani.bts.components.worldreset;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.SQLDatabaseCallable;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

class GetChunksReset implements SQLDatabaseCallable {
    private final PreparedStatement preparedStatement;
    private final World overworld, nether, theEnd;

    GetChunksReset(PreparedStatement preparedStatement, BornToSurvive bornToSurvive) {
        this.preparedStatement = preparedStatement;
        overworld = bornToSurvive.getOverworld();
        nether = bornToSurvive.getNether();
        theEnd = bornToSurvive.getTheEnd();
    }

    @Override
    public Map<World, String> call(Connection connection, Component component) throws SQLException {
        Map<World, String> chunksResetCompact = new HashMap<>();
        chunksResetCompact.put(overworld, fetchElement(overworld));
        chunksResetCompact.put(nether, fetchElement(nether));
        chunksResetCompact.put(theEnd, fetchElement(theEnd));
        return chunksResetCompact;
    }

    private String fetchElement(World world) throws SQLException {
        preparedStatement.setObject(1, world.getUID());
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            boolean validRow = resultSet.next();
            if (validRow) {
                return resultSet.getString(1);
            }
        }
        return "";
    }

    @Override
    public String getErrorMessage() {
        return "Could not retrieve chunks reset.";
    }
}
