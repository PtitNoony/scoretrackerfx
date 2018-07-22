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
public class LeagueFactory {

    public static final String LEAGUE_CREATED = "leagueCreated";

    private static final Map<Integer, League> LEAGUES = new HashMap<>();
    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(LEAGUES);

    private static final int ID_INCR = 3;

    private static int nextUniqueID = 1;

    public static League createLeague() {
        nextUniqueID = LEAGUES.keySet().stream().max(Integer::compare).orElse(1) + ID_INCR;
        League league = new LeagueImpl(nextUniqueID, "NoName");
        LEAGUES.put(nextUniqueID, league);
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(LEAGUE_CREATED, null, league);
        return league;
    }

    public static League createLeague(String leagueName) {
        nextUniqueID = LEAGUES.keySet().stream().max(Integer::compare).orElse(1) + ID_INCR;
        League league = new LeagueImpl(nextUniqueID, leagueName);
        LEAGUES.put(nextUniqueID, league);
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(LEAGUE_CREATED, null, league);
        return league;
    }

    public static League createLeague(int id, String leagueName) {
        if (LEAGUES.containsKey(id)) {
            //TODO: message
            throw new IllegalStateException("Error");
        }
        League league = new LeagueImpl(nextUniqueID, leagueName);
        LEAGUES.put(id, league);
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(LEAGUE_CREATED, null, league);
        return league;
    }

    public static List<League> getLeagues() {
        return Collections.unmodifiableList(LEAGUES.values().stream().collect(Collectors.toList()));
    }

    public static void addListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
    }

    private LeagueFactory() {
        // private utility constructor
    }

    private static class LeagueImpl implements League {

        private final int uniqueID;
        private final List<Session> sessions;
        private final List<Player> players;
        private final PropertyChangeSupport propertyChangeSupport;

        private String name;

        public LeagueImpl(int id, String name) {
            uniqueID = id;
            this.name = name;
            sessions = new LinkedList<>();
            players = new LinkedList<>();
            propertyChangeSupport = new PropertyChangeSupport(LeagueImpl.this);
        }

        @Override
        public int getID() {
            return uniqueID;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public List<Session> getSessions() {
            return Collections.unmodifiableList(sessions);
        }

        @Override
        public List<Player> getPlayers() {
            return Collections.unmodifiableList(players);
        }

        @Override
        public void addSession(Session session) {
            sessions.add(session);
            players.addAll(session.getPlayers().stream().filter(p -> !players.contains(p)).collect(Collectors.toList()));
            propertyChangeSupport.firePropertyChange(SESSION_ADDED, null, session);
        }

        @Override
        public int getNbSessions() {
            return sessions.size();
        }

        @Override
        public int getNbPlayers() {
            return players.size();
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            propertyChangeSupport.addPropertyChangeListener(listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            propertyChangeSupport.removePropertyChangeListener(listener);
        }

        @Override
        public String toString() {
            return name;
        }

    }
}
