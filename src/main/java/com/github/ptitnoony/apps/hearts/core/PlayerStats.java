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
import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author hamon
 */
public interface PlayerStats {

    Player getPlayer();

    void addGame(Game game, double score);

    List<Session> getSessionsPlayed();

    List<Game> getGames();

    List<Game> getGamesBySession(Session aSession);

    int getNbSessionsPlayed();

    int getNbGamesPlayed();

    double getCarrerRatio();

    double getBestRatio();

    double getSessionRatio(Session session);

    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

    List<Pair<Integer, Double>> getPlayerRatioHistory();

    List<Pair<Integer, Double>> getPlayerRatioHistory(Session aSession);

    /**
     * Recalculates all stats, can be used to counter errors while adding games
     * not chronologically
     */
    void recalculateStats();

    Player getCareerNemesis();

    Player getSessionNemesis(Session session);

    Player getCareerBestPointSupplier();

    Player getSessionBestPointSupplier(Session session);
}
