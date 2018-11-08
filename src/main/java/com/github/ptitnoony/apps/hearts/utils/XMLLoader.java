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

import com.github.ptitnoony.apps.hearts.core.League;
import com.github.ptitnoony.apps.hearts.core.Player;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author hamon
 */
public final class XMLLoader {

    private static final Logger LOG = Logger.getGlobal();

    private XMLLoader() {
        //private utility constructor
    }

    public static League loadFile(File file) {
        if (file != null) {
            //
            Document document;
            DocumentBuilderFactory builderFactory;
            builderFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                InputSource source = new InputSource(file.getAbsolutePath());
                document = builder.parse(source);
                Element e = document.getDocumentElement();
                // Players
                NodeList playerGroups = e.getElementsByTagName(XMLSaver.PLAYER_GROUP);
                //TODO: test size
                List<Player> players = parsePlayers((Element) playerGroups.item(0));
                // League
                NodeList leagueGroups = e.getElementsByTagName(XMLSaver.LEAGUE_GROUP);
                //TODO: test size
                parseLeagues((Element) leagueGroups.item(0));
                //
                players.forEach(Player::recalculateStats);
                //
            } catch (IOException | SAXException | ParserConfigurationException ex) {
                LOG.log(Level.SEVERE, "Exception while loading file {0} :: {1}", new Object[]{file, ex});
            }

        }
        return null;
    }

    private static List<Player> parsePlayers(Element playerRootElement) {
        List<Player> players = new LinkedList<>();
        NodeList playerElements = playerRootElement.getElementsByTagName(XMLSaver.PLAYER);
        for (int i = 0; i < playerElements.getLength(); i++) {
            Player p = PlayerXmlUtils.parsePlayer((Element) playerElements.item(i));
            players.add(p);
        }
        return players;
    }

    private static void parseLeagues(Element leagueRootElement) {
        NodeList leagueElements = leagueRootElement.getElementsByTagName(XMLSaver.LEAGUE_TAG);
        for (int i = 0; i < leagueElements.getLength(); i++) {
            LeagueXmlUtils.parseLeague((Element) leagueElements.item(i));
        }
    }

}
