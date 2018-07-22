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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author hamon
 */
public class SessionFactory {

    public static final String SESSION_CREATED = "sessionCreated";

    private static final Map<Integer, Session> SESSIONS = new HashMap<>();
    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(SESSIONS);

    private static final int ID_INCR = 7;

    private static int nextUniqueID = 1;

    private SessionFactory() {
        // private utility constructor
    }

    public static void addListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
    }

    /**
     * Get the sessions already created. This method is time consuming as it
     * creates a new unmodifiable list.
     *
     * @return list of existing sessions
     */
    public static List<Session> getCreatedSessions() {
        // doc that time consuming method
        return SESSIONS.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
    }

    public static Session getPlayerFromName(String sessionName) {
        for (Session p : SESSIONS.values()) {
            if (p.getName().equals(sessionName)) {
                return p;
            }
        }
//        return createSession(sessionName);
        return null;
    }

    public static Session createSession(String sessionName) {
        nextUniqueID = SESSIONS.keySet().stream().max(Integer::compare).orElse(1) + ID_INCR;
        final Session session = new SessionImpl(nextUniqueID, sessionName);
        SESSIONS.put(nextUniqueID, session);
        incrementUniqueID();
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(SESSION_CREATED, null, session);
        return session;
    }

    public static Session createSession(int id, String sessionName) {
        if (SESSIONS.containsKey(id)) {
            //TODO: message
            throw new IllegalStateException("Error");
        }
        final Session session = new SessionImpl(id, sessionName);
        SESSIONS.put(id, session);
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(SESSION_CREATED, null, session);
        return session;
    }

    /**
     * Get a created session using its unique id
     *
     * @param sessionID the session unique id
     * @return the corresponding created session if exists, null otherwise
     */
    public static Session getSessionFromID(int sessionID) {
        return SESSIONS.get(sessionID);
    }

    private static void incrementUniqueID() {
        nextUniqueID++;
        while (SESSIONS.containsKey(nextUniqueID)) {
            nextUniqueID += ID_INCR;
        }
    }

    private static class SessionImpl implements Session {

        private final int sessionID;
        private final String sessionName;
        private final List<Game> games;
        private final List<Player> players;

        public SessionImpl(int id, String name) {
            sessionID = id;
            sessionName = name;
            games = new LinkedList<>();
            players = new LinkedList<>();
        }

        @Override
        public void addGame(Game game) {
            games.add(game);
            game.getPlayers().forEach(p -> {
                if (!players.contains(p)) {
                    players.add(p);
                }
            });
        }

        @Override
        public void addGames(Collection<Game> gamesToAdd) {
            games.addAll(gamesToAdd);
            gamesToAdd.forEach(game -> game.getPlayers().forEach(p -> {
                if (!players.contains(p)) {
                    players.add(p);
                }
            }));
        }

        @Override
        public List<Game> getGames() {
            return Collections.unmodifiableList(games);
        }

        @Override
        public String getName() {
            return sessionName;
        }

        @Override
        public int getID() {
            return sessionID;
        }

        @Override
        public List<Player> getPlayers() {
            return Collections.unmodifiableList(players);
        }

        @Override
        public int getNbPlayers() {
            return players.size();
        }

        @Override
        public String toString() {
            return sessionName;
        }

    }

}
