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
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.util.Pair;

/**
 *
 * @author hamon
 */
public class PlayerFactory {

    public static final String PLAYER_CREATED = "playerCreated";
    public static final String PLAYER_CHANGED = "playerChanged";

    private static final Map<Integer, Player> PLAYERS = new HashMap<>();
    private static final Map<Player, PlayerStats> PLAYERS_STATS = new HashMap<>();

    private static final int ID_INCR = 7;
    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(PLAYERS);

    private static int nextUniqueID = 1;

    public static final Player NOBODY = createPlayer(0, "", "", "Mr. Nobody");

    private PlayerFactory() {
        // private utility constructor
    }

    public static void addListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
    }

    /**
     * Get the players already created. This method is time consuming as it
     * creates a new unmodifiable list.
     *
     * @return list of existing players
     */
    public static List<Player> getCreatedPlayers() {
        // doc that time consuming method
        return PLAYERS.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
    }

    public static Player getPlayerFromName(String playerName) {
        for (Player p : PLAYERS.values()) {
            if (p.getFirstName().equals(playerName)) {
                return p;
            }
        }
        return createPlayer(playerName, "", playerName);
    }

    /**
     * Create a player using the given parameters.
     *
     * @param firstName player first name
     * @param lastName player last name
     * @param nickName player nickname
     * @return the created player
     */
    public static Player createPlayer(String firstName, String lastName, String nickName) {
        while (PLAYERS.containsKey(nextUniqueID)) {
            nextUniqueID++;
        }
        final Player player = new PlayerImpl(nextUniqueID, firstName, lastName, nickName);
        PLAYERS.put(nextUniqueID, player);
        incrementUniqueID();
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(PLAYER_CREATED, null, player);
        return player;
    }

    /**
     * Create a player with the given parameters. Will throw an exception if the
     * unique id is already used by another player.
     *
     * @param id the player unique id
     * @param firstName player first name
     * @param lastName player last name
     * @param nickName player nickname
     * @return the created player
     */
    public static Player createPlayer(int id, String firstName, String lastName, String nickName) {
        if (PLAYERS.containsKey(id)) {
            //TODO: message
            throw new IllegalStateException("Error");

        }
        final Player player = new PlayerImpl(id, firstName, lastName, nickName);
        PLAYERS.put(id, player);
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(PLAYER_CREATED, null, player);
        return player;
    }

    /**
     * Get a created player using its unique id
     *
     * @param playerID the player unique id
     * @return the corresponding created player if exists, null otherwise
     */
    public static Player getPlayerFromID(int playerID) {
        return PLAYERS.get(playerID);
    }

    /**
     * Tests if given attributes are suitable for creating a new player.
     *
     * @param firstName player first name
     * @param lastName player last name
     * @param nickName player nick name
     * @return if the player attributes are valid for player creation.
     */
    public static boolean areValidPlayerAttibutes(String firstName, String lastName, String nickName) {
        if (firstName == null || lastName == null || nickName == null) {
            return false;
        }
        //TODO: test existence
        return !firstName.trim().isEmpty() && !lastName.trim().isEmpty() && !nickName.trim().isEmpty();
    }

    private static void incrementUniqueID() {
        nextUniqueID++;
        while (PLAYERS.containsKey(nextUniqueID)) {
            nextUniqueID += ID_INCR;
        }
    }

    private static class PlayerImpl implements Player {

        private final int pUniqueID;
        private final String pFirstName;
        private final String pLastName;
        private final String pNickName;
        private final PlayerStats pStats;

        private PlayerImpl(int uniqueID, String firstName, String lastName, String nickName) {
            pUniqueID = uniqueID;
            pFirstName = firstName;
            pLastName = lastName;
            pNickName = nickName;
            pStats = new PlayerStatsImpl(PlayerImpl.this);
            //
            PLAYERS_STATS.put(PlayerImpl.this, pStats);
        }

        @Override
        public int getID() {
            return pUniqueID;
        }

        @Override
        public String getFirstName() {
            return pFirstName;
        }

        @Override
        public String getLastName() {
            return pLastName;
        }

        @Override
        public String getNickName() {
            return pNickName;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + this.pUniqueID;
            hash = 97 * hash + Objects.hashCode(this.pFirstName);
            hash = 97 * hash + Objects.hashCode(this.pLastName);
            hash = 97 * hash + Objects.hashCode(this.pNickName);
            return hash;
        }

        @Override
        public PlayerStats getPlayerStats() {
            return pStats;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final PlayerImpl other = (PlayerImpl) obj;
            if (this.pUniqueID != other.pUniqueID) {
                return false;
            }
            if (!Objects.equals(this.pFirstName, other.pFirstName)) {
                return false;
            }
            if (!Objects.equals(this.pLastName, other.pLastName)) {
                return false;
            }
            return Objects.equals(this.pNickName, other.pNickName);
        }

        @Override
        public String toString() {
            return pFirstName + " " + pLastName + " (" + pNickName + ")";
        }

        @Override
        public void recalculateStats() {
            pStats.recalculateStats();
        }

    }

    private static class PlayerStatsImpl implements PlayerStats {

        private final Player player;
        private final List<Game> games;
        private final Map<Session, List<Game>> gamesBySessions;
        private final Map<Session, List<Pair<Integer, Double>>> scoresBySessions;
        private final Map<Session, Double> ratioBySession;
        //
        private Player nemesis = NOBODY;
        private Player bestPointSupplier = NOBODY;
        private Map<Session, Player> sessionNemesis;
        private Map<Session, Player> sessionBestPointSupplier;
        //
        private List<Pair<Integer, Double>> ratioHistory;
        private Map<Session, List<Pair<Integer, Double>>> ratioHistoryBySession;
        private Map<Player, Double> otherPlayersScores;
        //
        private final PropertyChangeSupport propertyChangeSupport;
        //
        private int nbSessionsPlayed;
        private int nbGamesPlayed;
        private double carrerRatio;
        private double bestRatio;

        private PlayerStatsImpl(Player aPlayer) {
            player = aPlayer;
            games = new LinkedList<>();
            gamesBySessions = new HashMap<>();
            scoresBySessions = new HashMap<>();
            ratioBySession = new HashMap<>();
            //
            ratioHistory = new LinkedList<>();
            ratioHistoryBySession = new HashMap<>();
            //
            otherPlayersScores = new HashMap<>();
            sessionNemesis = new HashMap<>();
            sessionBestPointSupplier = new HashMap<>();
            //
            propertyChangeSupport = new PropertyChangeSupport(PlayerStatsImpl.this);
            //
            nbSessionsPlayed = 0;
            nbGamesPlayed = 0;
            carrerRatio = 0.0;
            bestRatio = 0.0;
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
        public Player getPlayer() {
            return player;
        }

        @Override
        public void addGame(Game game, double score) {
            if (!game.getPlayers().contains(player)) {
                System.err.println("Tried to add a game not played by a player");
                return;
            }
            // To be optimized
            Session session = game.getSession();
            if (!gamesBySessions.keySet().contains(session)) {
                gamesBySessions.put(session, new LinkedList<>());
                scoresBySessions.put(session, new LinkedList<>());
                ratioHistoryBySession.put(session, new LinkedList<>());
            }
            games.add(game);
            gamesBySessions.get(session).add(game);
            scoresBySessions.get(session).add(new Pair<>(game.getIdInSession(), score));
            //
            List<Pair<Integer, Double>> sessionScores = scoresBySessions.get(session);
            ratioBySession.put(session, sessionScores.stream().mapToDouble(Pair::getValue).sum() / (double) sessionScores.size());
            nbSessionsPlayed = gamesBySessions.size();
            nbGamesPlayed = gamesBySessions.values().stream().mapToInt(l -> l.size()).sum();
            double totalPoints = scoresBySessions.values().stream()
                    .mapToDouble(l -> l.stream().mapToDouble(Pair::getValue).sum()).sum();
            carrerRatio = totalPoints / nbGamesPlayed;
            bestRatio = ratioBySession.values().stream().max(Comparator.naturalOrder()).orElse(0.0);
            propertyChangeSupport.firePropertyChange(PLAYER_CHANGED, null, this);
            //
            List<Pair<Integer, Double>> previousSessionGames = scoresBySessions.get(session).stream()
                    .filter(pair -> pair.getKey() < game.getIdInSession())
                    .collect(Collectors.toList());
            int nbPreviousGames = previousSessionGames.size();
            double totalPreviousGames = previousSessionGames.stream().mapToDouble(Pair::getValue).sum();
            final Pair<Integer, Double> currentRatio = new Pair<>(game.getIdInSession(), (score + totalPreviousGames) / (nbPreviousGames + 1));
            ratioHistoryBySession.get(session).add(currentRatio);
            //
            ratioHistory.add(new Pair<>(game.getUniqueID(), carrerRatio));
            //
            game.getPlayers().stream().filter(p -> player != p)
                    .forEach(p -> {
                        double pScore = 0;
                        if (otherPlayersScores.containsKey(p)) {
                            pScore = otherPlayersScores.get(p);
                            otherPlayersScores.remove(p);
                        }
                        otherPlayersScores.put(p, pScore + game.getPlayerScore(p));
                    });
            //
            findOtherPlayers();
        }

        @Override
        public List<Pair<Integer, Double>> getPlayerRatioHistory() {
            return Collections.unmodifiableList(ratioHistory);
        }

        @Override
        public List<Pair<Integer, Double>> getPlayerRatioHistory(Session aSession) {
            List<Pair<Integer, Double>> history = ratioHistoryBySession.get(aSession);
            if (history != null) {
                return Collections.unmodifiableList(history);
            }
            return history;
        }

        @Override
        public int getNbGamesPlayed() {
            return nbGamesPlayed;
        }

        @Override
        public List<Session> getSessionsPlayed() {
            return new LinkedList<>(gamesBySessions.keySet());
        }

        @Override
        public int getNbSessionsPlayed() {
            return nbSessionsPlayed;
        }

        @Override
        public List<Game> getGamesBySession(Session aSession) {
            if (gamesBySessions.containsKey(aSession)) {
                return new LinkedList<>(gamesBySessions.get(aSession));
            }
            return Collections.EMPTY_LIST;
        }

        @Override
        public List<Game> getGames() {
            List<Game> result = new LinkedList<>();
            gamesBySessions.keySet().stream()
                    .sorted((s1, s2) -> Integer.compare(s1.getID(), s2.getID()))
                    .forEachOrdered(s -> result.addAll(gamesBySessions.get(s)));
            return result;
        }

        @Override
        public double getBestRatio() {
            return bestRatio;
        }

        @Override
        public double getCarrerRatio() {
            return carrerRatio;
        }

        @Override
        public double getSessionRatio(Session session) {
            //TODO: protect
            return ratioBySession.get(session);
        }

        @Override
        public Player getCareerBestPointSupplier() {
            return bestPointSupplier;
        }

        @Override
        public Player getCareerNemesis() {
            return nemesis;
        }

        @Override
        public Player getSessionBestPointSupplier(Session session) {
            return sessionBestPointSupplier.get(session);
        }

        @Override
        public Player getSessionNemesis(Session session) {
            return sessionNemesis.get(session);
        }

        @Override
        public void recalculateStats() {
            games.sort((g1, g2) -> Integer.compare(g1.getUniqueID(), g2.getUniqueID()));
            //
            otherPlayersScores = new HashMap<>();
            nemesis = NOBODY;
            bestPointSupplier = NOBODY;
            //
            // recalculating ratio history
            ratioHistory = new LinkedList<>();
            double totalScore = 0.0;
            for (int i = 0; i < games.size(); i++) {
                Game g = games.get(i);
                totalScore += g.getPlayerScore(player);
                ratioHistory.add(new Pair<>(g.getUniqueID(), totalScore / (double) (1.0 + i)));
                // todo factor code at some point
                g.getPlayers().stream().filter(p -> player != p)
                        .forEach(p -> {
                            double score = 0;
                            if (otherPlayersScores.containsKey(p)) {
                                score = otherPlayersScores.get(p);
                                otherPlayersScores.remove(p);
                            }
                            otherPlayersScores.put(p, score + g.getPlayerScore(p));
                        });

            }
            //
            // recalculating ratio history by session
            ratioHistoryBySession = new HashMap<>();
            gamesBySessions.forEach((session, listGames) -> {
                listGames.sort((g1, g2) -> Integer.compare(g1.getUniqueID(), g2.getUniqueID()));
                List<Pair<Integer, Double>> sessionRatios = new LinkedList<>();
                double sessionScore = 0.0;
                for (int i = 0; i < listGames.size(); i++) {
                    Game g = listGames.get(i);
                    sessionScore += g.getPlayerScore(player);
                    sessionRatios.add(new Pair<>(g.getUniqueID(), sessionScore / (double) (1.0 + i)));
                }
                ratioHistoryBySession.put(session, sessionRatios);
            });
            //
            // TODO be sure there is no need to update ratioBySession
            //
            findOtherPlayers();
        }

        private void findOtherPlayers() {
            Map.Entry<Player, Double> maxEntry = null;
            Map.Entry<Player, Double> minEntry = null;
            for (Map.Entry<Player, Double> entry : otherPlayersScores.entrySet()) {
                if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                    maxEntry = entry;
                }
                if (minEntry == null || entry.getValue().compareTo(minEntry.getValue()) < 0) {
                    minEntry = entry;
                }
            }
            nemesis = maxEntry != null ? maxEntry.getKey() : NOBODY;
            bestPointSupplier = minEntry != null ? minEntry.getKey() : NOBODY;
        }

    }

}
