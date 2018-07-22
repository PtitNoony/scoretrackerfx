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
package com.github.ptitnoony.apps.hearts.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author hamon
 */
public class GameFactory {

    private static final Map<Integer, Game> GAMES = new HashMap<>();

    private static final int ID_INCR = 7;

    private static int nextUniqueID = 1;

    private GameFactory() {
        // private utility constructor
    }

    /**
     * Get the games already created. This method is time consuming as it
     * creates a new unmodifiable list.
     *
     * @return list of existing games
     */
    public static List<Game> getCreatedGames() {
        // doc that time consuming method
        return GAMES.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
    }

    public static Game createGame(Session session, int idInSession) {
        while (GAMES.containsKey(nextUniqueID)) {
            nextUniqueID++;
        }
        final Game game = new GameImpl(nextUniqueID, session, idInSession);
        GAMES.put(nextUniqueID, game);
        incrementUniqueID();
        return game;
    }

    /**
     * Get a created game using its unique id
     *
     * @param gameID the game unique id
     * @return the corresponding created game if exists, null otherwise
     */
    public static Game getGameFromUniqueID(int gameID) {
        return GAMES.get(gameID);
    }

    private static void incrementUniqueID() {
        nextUniqueID++;
        while (GAMES.containsKey(nextUniqueID)) {
            nextUniqueID += ID_INCR;
        }
    }

    private static class GameImpl implements Game {

        private final int uniqId;
        private final Session session;
        private final int idInSession;
        private final Map<Player, Double> scores;
        private boolean complete;

        public GameImpl(int uniqueID, Session aSession, int gameId) {
            uniqId = uniqueID;
            session = aSession;
            idInSession = gameId;
            scores = new HashMap<>();
            complete = false;
        }

        @Override
        public void addPlayer(Player p, double score) {
            if (!complete) {
                scores.put(p, score);
                p.getPlayerStats().addGame(this, score);
                complete = scores.size() == 4;
            }
        }

        @Override
        public int getUniqueID() {
            return uniqId;
        }

        @Override
        public Session getSession() {
            return session;
        }

        @Override
        public int getIdInSession() {
            return idInSession;
        }

        @Override
        public double getPlayerScore(Player player) {
            return scores.get(player);
        }

        @Override
        public boolean isComplete() {
            return complete;
        }

        @Override
        public List<Player> getPlayers() {
            return scores.keySet().stream().collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return "GAME :: " + scores.keySet().stream().map(p -> p.getNickName()).collect(Collectors.joining(" - "));
        }

    }
}
