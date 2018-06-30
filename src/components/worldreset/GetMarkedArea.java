package it.menzani.bts.components.worldreset;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.SQLDatabaseCallable;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

class GetMarkedArea implements SQLDatabaseCallable {
    private final PreparedStatement preparedStatement;
    private final World overworld, nether, theEnd;

    GetMarkedArea(PreparedStatement preparedStatement, BornToSurvive bornToSurvive) {
        this.preparedStatement = preparedStatement;
        overworld = bornToSurvive.getOverworld();
        nether = bornToSurvive.getNether();
        theEnd = bornToSurvive.getTheEnd();
    }

    @Override
    public MarkedArea call(Connection connection, Component component) throws SQLException {
        MarkedArea markedArea = new MarkedArea();
        Set<ChunkLocation> overworldMarks = fetchMarks(overworld);
        Set<ChunkLocation> netherMarks = fetchMarks(nether);
        Set<ChunkLocation> theEndMarks = fetchMarks(theEnd);
        markedArea.marks.put(overworld, overworldMarks);
        markedArea.marks.put(nether, netherMarks);
        markedArea.marks.put(theEnd, theEndMarks);
        markedArea.area.put(overworld, computeArea(overworldMarks));
        markedArea.area.put(nether, computeArea(netherMarks));
        markedArea.area.put(theEnd, computeArea(theEndMarks));
        return markedArea;
    }

    private Set<ChunkLocation> fetchMarks(World world) throws SQLException {
        preparedStatement.setObject(1, world.getUID());
        ResultSet resultSet = preparedStatement.executeQuery();
        Set<ChunkLocation> marks = new HashSet<>();
        while (resultSet.next()) {
            marks.add(new ChunkLocation(resultSet.getInt(1), resultSet.getInt(2)));
        }
        return marks;
    }

    private static Set<ChunkLocation> computeArea(Set<ChunkLocation> marks) {
        Set<ChunkLocation> area = new HashSet<>();
        for (ChunkLocation mark : marks) {
            for (int x = -WorldReset.markRange; x <= WorldReset.markRange; x++) {
                for (int z = -WorldReset.markRange; z <= WorldReset.markRange; z++) {
                    area.add(new ChunkLocation(mark.x + x, mark.z + z));
                }
            }
        }
        return area;
    }

    @Override
    public String getErrorMessage() {
        return "Could not retrieve marked area.";
    }
}
