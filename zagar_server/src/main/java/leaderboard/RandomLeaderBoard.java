package leaderboard;

import main.ApplicationContext;
import matchmaker.MatchMaker;
import model.GameSession;
import model.Player;
import network.ClientConnections;
import network.packets.PacketLeaderBoard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

/**
 * Created by s on 29.11.16.
 */

public class RandomLeaderBoard implements Leaderboard {
    private final static @NotNull Logger log = LogManager.getLogger(RandomLeaderBoard.class);
    private static String [] leaders={"Ivan", "Anton", "Serge", "Roma"};

    @Override
    public void sendleaders() {
        for (GameSession gameSession : ApplicationContext.instance().get(MatchMaker.class).getActiveGameSessions()) {
            int i=new Random().nextInt(4);
            int j=new Random().nextInt(4);
            String s=leaders[i]; leaders[i]=leaders[j]; leaders[j]=s;

            for (Map.Entry<Player, Session> connection : ApplicationContext.instance().get(ClientConnections.class).getConnections()) {
                if (gameSession.getPlayers().contains(connection.getKey())) {
                    try {
                        new PacketLeaderBoard(leaders).write(connection.getValue());
                    } catch (IOException e) {
                        log.error(e);
                    }
                }
            }
        }
    }
}

