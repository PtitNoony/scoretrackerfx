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

import java.util.List;

/**
 *
 * @author hamon
 */
public interface Game {

    /**
     *
     * @param p the player to add to the game
     * @param score the player's score
     */
    void addPlayer(Player p, double score);

    /**
     *
     * @return the session in which the game is played
     */
    Session getSession();

    /**
     *
     * @return the game id in its session
     */
    int getIdInSession();

    /**
     *
     * @return the game unique ID
     */
    int getUniqueID();

    /**
     *
     * @param player a player
     * @return the player's score in this game
     */
    double getPlayerScore(Player player);

    /**
     *
     * @return if the game is considered complete
     */
    boolean isComplete();

    /**
     *
     * @return the list of players who player in this game
     */
    List<Player> getPlayers();

}
