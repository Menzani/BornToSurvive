package it.menzani.bts.components.worldreset;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.SQLDatabaseCallable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

class GetMarkedArea implements SQLDatabaseCallable {
    @Override
    public Set<ChunkLocation> call(Connection connection, Component component) throws SQLException {
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT chunkX, chunkZ FROM " + component.getName() + "_mark");
        Set<ChunkLocation> markedArea = new HashSet<>();
        while (resultSet.next()) {
            int centerX = resultSet.getInt(1);
            int centerZ = resultSet.getInt(2);
            for (int x = -WorldReset.markRange; x <= WorldReset.markRange; x++) {
                for (int z = -WorldReset.markRange; z <= WorldReset.markRange; z++) {
                    markedArea.add(new ChunkLocation(centerX + x, centerZ + z));
                }
            }
        }
        return markedArea;
    }

    @Override
    public String getErrorMessage() {
        return "Could not retrieve marked area.";
    }
}
