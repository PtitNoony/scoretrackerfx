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

import com.github.ptitnoony.apps.hearts.core.LeagueFactory;
import com.github.ptitnoony.apps.hearts.core.PlayerFactory;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author hamon
 */
public class XMLSaver {

    public static final String PLAYER_GROUP = "PLAYERS";
    public static final String PLAYER = "player";
    public static final String ID = "id";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String NAME = "name";

    public static final String SCORES_GROUP = "SCORES";
    public static final String LEAGUE_GROUP = "LEAGUES";
    public static final String LEAGUE_TAG = "league";

    public static final String SESSION_GROUP = "SESSIONS";
    public static final String SESSION_TAG = "session";

    public static final String GAME_GROUP = "GAMES";
    public static final String GAME_TAG = "game";
    public static final String ID_IN_SESSION_TAG = "idInSession";

    public static final String GAME_SCORE_TAG = "score";
    public static final String PLAYER_ID_TAG = "playerID";
    public static final String SCORE_VALUE_TAG = "value";

    private static final Logger LOG = Logger.getGlobal();

    private XMLSaver() {
        // private utility class
    }

    public static boolean save(File destFile) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement(SCORES_GROUP);
            doc.appendChild(rootElement);
            // save players
            Element playerGroupElement = doc.createElement(PLAYER_GROUP);
            rootElement.appendChild(playerGroupElement);
            PlayerFactory.getCreatedPlayers().forEach(player -> playerGroupElement.appendChild(PlayerXmlUtils.getPlayerXMLElement(doc, player)));
            // save leagues
            Element leagueGroupElement = doc.createElement(LEAGUE_GROUP);
            rootElement.appendChild(leagueGroupElement);
            LeagueFactory.getLeagues().forEach(league -> leagueGroupElement.appendChild(LeagueXmlUtils.getLeagueXMLElement(doc, league)));
            //
            rootElement.normalize();
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(destFile);
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException ex) {
            LOG.log(Level.SEVERE, " Exception while exporting atc geography :: {0}", ex);
            return false;
        }
        return true;
    }

}
