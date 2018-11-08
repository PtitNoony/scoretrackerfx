/*
 * Copyright (C) 2018 H-K
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.ptitnoony.apps.hearts;

import com.github.ptitnoony.apps.hearts.core.GameFactory;
import com.github.ptitnoony.apps.hearts.core.PlayerFactory;
import com.github.ptitnoony.apps.hearts.core.SessionFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import com.github.ptitnoony.apps.hearts.core.Game;
import com.github.ptitnoony.apps.hearts.core.Player;
import com.github.ptitnoony.apps.hearts.core.Session;

/**
 *
 * @author hamon
 */
public class SessionParser {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    // HACK
    private static Session lastParsedSession = null;

    /**
     * 
     * @return a map of the session parsed, by name
     */
    public static Map<String, Session> getSessions() {
        return SESSIONS;
    }

    /**
     * 
     * @param file a file containing a session data
     * @return the session parsed
     */
    public static Session parseLog(File file) {
        Session session = SessionFactory.createSession(file.getName());
        // todo check extension
        List<String> lines;
        try {
            lines = FileUtils.readLines(file, "UTF-8");
        } catch (IOException ex) {
            Logger.getLogger(SessionParser.class.getName()).log(Level.SEVERE, null, ex);
            lines = new LinkedList<>();
        }
        String line;
        String[] splitLine;
        //
        Map<Integer, Game> games = new HashMap<>();

        // Parse line 1
        line = toCsvLine(lines.get(0));
        System.err.println("> " + line);
        splitLine = line.split(";");
        for (int n = 3; n < splitLine.length; n++) {
            if (!"x".equals(splitLine[n])) {
                int id = Integer.parseInt(splitLine[n]);
                Game game = GameFactory.createGame(session, id);
                games.put(id, game);
            }
        }

        for (int i = 1; i < lines.size(); i++) {
            line = toCsvLine(lines.get(i));
            splitLine = line.split(";");
            String playerName = splitLine[0];
            Player player = PlayerFactory.getPlayerFromName(playerName);
//            HeartsPlayerStats player = new HeartsPlayerStats(playerName);
//            PLAYERS.add(player);
            for (int n = 3; n < splitLine.length; n++) {
                if (!"x".equals(splitLine[n])) {
                    double score = Double.parseDouble(splitLine[n]);
//                    BriqueGame game = new BriqueGame(id);
                    if (games.get(n - 2) != null) {
                        games.get(n - 2).addPlayer(player, score);
                    } else {
                        System.err.println("ERROR " + playerName + " n=" + n);
                    }
                }
            }
        }
//        games.forEach((id,game)->session.addGames(game));
//        session.addGames(games.values().stream().filter(Game::isComplete).collect(Collectors.toSet()));
        session.addGames(games.values().stream().collect(Collectors.toSet()));
        SESSIONS.put(session.getName(), session);
        // HACK
        lastParsedSession = session;
        return session;
    }

    /**
     * 
     * @return the last session parsed
     */
    public static Session getLastParsedSession() {
        return lastParsedSession;
    }

    private static String toCsvLine(String inLine) {
        String res = inLine;
        while (res.contains(";;")) {
            res = res.replaceFirst(";;", ";x;");
        }
        return res;
    }

}
