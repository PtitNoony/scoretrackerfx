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
package com.github.ptitnoony.apps.hearts.utils;

import com.github.ptitnoony.apps.hearts.core.Game;
import com.github.ptitnoony.apps.hearts.core.GameFactory;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.github.ptitnoony.apps.hearts.core.Player;
import com.github.ptitnoony.apps.hearts.core.PlayerFactory;
import com.github.ptitnoony.apps.hearts.core.Session;
import com.github.ptitnoony.apps.hearts.core.SessionFactory;
import static com.github.ptitnoony.apps.hearts.utils.XMLSaver.GAME_GROUP;
import static com.github.ptitnoony.apps.hearts.utils.XMLSaver.GAME_SCORE_TAG;
import static com.github.ptitnoony.apps.hearts.utils.XMLSaver.GAME_TAG;
import static com.github.ptitnoony.apps.hearts.utils.XMLSaver.PLAYER_ID_TAG;
import static com.github.ptitnoony.apps.hearts.utils.XMLSaver.SCORE_VALUE_TAG;
import static com.github.ptitnoony.apps.hearts.utils.XMLSaver.SESSION_TAG;
import static com.github.ptitnoony.apps.hearts.utils.XMLSaver.ID;
import static com.github.ptitnoony.apps.hearts.utils.XMLSaver.ID_IN_SESSION_TAG;
import static com.github.ptitnoony.apps.hearts.utils.XMLSaver.NAME;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author hamon
 */
public class SessionXmlUtils {

    private static final Logger LOG = Logger.getGlobal();

    private SessionXmlUtils() {
        // private utility class
    }

    public static Session parseSession(Element sessionElement) {
        Session session;
        String name = sessionElement.getAttribute(NAME);
        if (sessionElement.hasAttribute(ID)) {
            int id = Integer.parseInt(sessionElement.getAttribute(ID));
            session = SessionFactory.createSession(id, name);
        } else {
            session = SessionFactory.createSession(name);
        }
        // parse Games
        NodeList gamesGroups = sessionElement.getElementsByTagName(XMLSaver.GAME_GROUP);
        //TODO: test size
        Element gamesGroupElement = (Element) gamesGroups.item(0);
        NodeList gamesElements = gamesGroupElement.getElementsByTagName(XMLSaver.GAME_TAG);
        for (int i = 0; i < gamesElements.getLength(); i++) {
            Game game = parseGame((Element) gamesElements.item(i), session);
            session.addGame(game);
        }

        return session;
    }

    public static Node getSessionXMLElement(Document doc, Session session) {
        Element sessionElement = doc.createElement(SESSION_TAG);
        sessionElement.setAttribute(ID, Integer.toString(session.getID()));
        sessionElement.setAttribute(NAME, session.getName());
        Element gameGroup = doc.createElement(GAME_GROUP);
        sessionElement.appendChild(gameGroup);
        session.getGames().forEach(game -> gameGroup.appendChild(getGameXMLElement(doc, game)));
        return sessionElement;
    }

    private static Node getGameXMLElement(Document doc, Game game) {
        Element gameElement = doc.createElement(GAME_TAG);
        gameElement.setAttribute(ID, Integer.toString(game.getUniqueID()));
        gameElement.setAttribute(ID_IN_SESSION_TAG, Integer.toString(game.getIdInSession()));
        game.getPlayers().forEach(player -> gameElement.appendChild(getPlayerGameXMLElement(doc, game, player)));
        return gameElement;
    }

    private static Node getPlayerGameXMLElement(Document doc, Game game, Player player) {
        Element scoreElement = doc.createElement(GAME_SCORE_TAG);
        scoreElement.setAttribute(PLAYER_ID_TAG, Integer.toString(player.getID()));
        scoreElement.setAttribute(SCORE_VALUE_TAG, Double.toString(game.getPlayerScore(player)));
        return scoreElement;
    }

    private static Game parseGame(Element gameElement, Session session) {
        int id = Integer.parseInt(gameElement.getAttribute(ID));
        //TODO do not ignore ?
        int idInSession = Integer.parseInt(gameElement.getAttribute(ID_IN_SESSION_TAG));
        Game game = GameFactory.createGame(session, idInSession);
        // parse scores
        NodeList scoreElements = gameElement.getElementsByTagName(GAME_SCORE_TAG);
        for (int i = 0; i < scoreElements.getLength(); i++) {
            Element scoreElement = (Element) scoreElements.item(i);
            double score = Double.parseDouble(scoreElement.getAttribute(SCORE_VALUE_TAG));
            int playerID = Integer.parseInt(scoreElement.getAttribute(PLAYER_ID_TAG));
            Player player = PlayerFactory.getPlayerFromID(playerID);
            if (player != null) {
                game.addPlayer(player, score);
            }
            session.addGame(game);
        }
        return game;
    }

}
