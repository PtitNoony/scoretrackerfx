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

import com.github.ptitnoony.apps.hearts.core.League;
import com.github.ptitnoony.apps.hearts.core.LeagueFactory;
import static com.github.ptitnoony.apps.hearts.core.LeagueFactory.createLeague;
import com.github.ptitnoony.apps.hearts.core.Player;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author hamon
 */
public class AppState {

    public static final String LEAGUE_CHANGED = "leagueChanged";
    public static final String PLAYER_CHANGED = AppState.class.getName() + "playerChanged";

    private static final AppState APP_STATE = new AppState();
    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(APP_STATE);

//    private static League currentLeague = createLeague();
    private static League currentLeague = null;
    private static Player currentPlayer = null;

    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
    }

    public static void setLeague(League league) {
        currentLeague = league;
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(LEAGUE_CHANGED, null, currentLeague);
    }

    public static League getCurrentLeague() {
        return currentLeague;
    }

    public static void setCurrentPlayer(Player player) {
        currentPlayer = player;
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(PLAYER_CHANGED, null, currentLeague);
    }

    public static Player getCurrentPlayer() {
        return currentPlayer;
    }

    private AppState() {
        // private utility constructor
        LeagueFactory.addListener(AppState::handleLeagueFactoryEvents);
    }

    private static void handleLeagueFactoryEvents(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case LeagueFactory.LEAGUE_CREATED:
                League league = (League) event.getNewValue();
                if (league != null) {
                    setLeague(league);
                }
                break;
            default:
                throw new UnsupportedOperationException(AppState.class.getName() + " ::Unsupported ppty change :: " + event.getPropertyName());
        }
    }
}
