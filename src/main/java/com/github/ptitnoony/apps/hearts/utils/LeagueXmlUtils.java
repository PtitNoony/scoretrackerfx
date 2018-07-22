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
import com.github.ptitnoony.apps.hearts.core.LeagueFactory;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.github.ptitnoony.apps.hearts.core.Session;
import static com.github.ptitnoony.apps.hearts.utils.XMLSaver.LEAGUE_TAG;
import static com.github.ptitnoony.apps.hearts.utils.XMLSaver.NAME;
import static com.github.ptitnoony.apps.hearts.utils.XMLSaver.ID;
import static com.github.ptitnoony.apps.hearts.utils.XMLSaver.SESSION_GROUP;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author hamon
 */
public class LeagueXmlUtils {

    private static final Logger LOG = Logger.getGlobal();

    private LeagueXmlUtils() {
        // private utility class
    }

    public static League parseLeague(Element leagueElement) {
        String leagueName = leagueElement.getAttribute(NAME);
        League league;
        if (leagueElement.hasAttribute(ID)) {
            int id = Integer.parseInt(leagueElement.getAttribute(ID));
            league = LeagueFactory.createLeague(id, leagueName);
        } else {
            league = LeagueFactory.createLeague(leagueName);
        }
        // parse sessions
        NodeList sessionGroups = leagueElement.getElementsByTagName(XMLSaver.SESSION_GROUP);
        //TODO: test size
        Element sessionGroupElement = (Element) sessionGroups.item(0);
        NodeList sessionElements = sessionGroupElement.getElementsByTagName(XMLSaver.SESSION_TAG);
        for (int i = 0; i < sessionElements.getLength(); i++) {
            final Session session = SessionXmlUtils.parseSession((Element) sessionElements.item(i));
            league.addSession(session);
        }
        return league;
    }

    public static Node getLeagueXMLElement(Document doc, League league) {
        Element leagueElement = doc.createElement(LEAGUE_TAG);
        leagueElement.setAttribute(ID, Integer.toString(league.getID()));
        leagueElement.setAttribute(NAME, league.getName());
        Element sessionGroup = doc.createElement(SESSION_GROUP);
        leagueElement.appendChild(sessionGroup);
        league.getSessions().forEach(session -> sessionGroup.appendChild(SessionXmlUtils.getSessionXMLElement(doc, session)));
        return leagueElement;
    }

}
