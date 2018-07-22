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
import java.io.File;
import java.io.IOException;
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
                parsePlayers((Element) playerGroups.item(0));
                // League
                NodeList leagueGroups = e.getElementsByTagName(XMLSaver.LEAGUE_GROUP);
                //TODO: test size
                parseLeagues((Element) leagueGroups.item(0));

            } catch (IOException | SAXException | ParserConfigurationException ex) {
                LOG.log(Level.SEVERE, "Exception while loading file {0} :: {1}", new Object[]{file, ex});
            }

        }
        return null;
    }

    private static void parsePlayers(Element playerRootElement) {
        NodeList playerElements = playerRootElement.getElementsByTagName(XMLSaver.PLAYER);
        for (int i = 0; i < playerElements.getLength(); i++) {
            PlayerXmlUtils.parsePlayer((Element) playerElements.item(i));
        }
    }

    private static void parseLeagues(Element leagueRootElement) {
        NodeList leagueElements = leagueRootElement.getElementsByTagName(XMLSaver.LEAGUE_TAG);
        for (int i = 0; i < leagueElements.getLength(); i++) {
            LeagueXmlUtils.parseLeague((Element) leagueElements.item(i));
        }
    }
    /*
    private static void parseSessions(Element sessionRootElement) {
        NodeList sessionElements = sessionRootElement.getElementsByTagName(XMLSaver.SESSION);
        for (int i = 0; i < sessionElements.getLength(); i++) {
            parseSingleSession((Element) sessionElements.item(i));
        }
    }

    private static void parseSingleSession(Element element) {
        String dateString = element.getAttribute(XMLSaver.SESSION_DATE);
        LocalDate date = LocalDate.parse(dateString);
        String location = element.getAttribute(XMLSaver.SESSION_LOCATION);
        //
        Session session = SessionFactory.createSession(date, location);
        //
        NodeList confrontations = element.getElementsByTagName(XMLSaver.CONFRONTATION);
        for (int i = 0; i < confrontations.getLength(); i++) {
            final Confrontation confrontation = parseConfrontation((Element) confrontations.item(i), date);
            session.addConfrontation(confrontation);
        }
//        Sessions.addSession(session);
    }

    private static Confrontation parseConfrontation(Element element, LocalDate date) {
        Confrontation confrontation = ConfrontationFactory.createSession(date);
        NodeList games = element.getElementsByTagName(XMLSaver.GAME);
        for (int i = 0; i < games.getLength(); i++) {
            Round round = parsePlayerGame((Element) games.item(i));
            confrontation.addRound(round);
        }
        return confrontation;
    }

    private static Round parsePlayerGame(Element element) {
        int playerID = Integer.parseInt(element.getAttribute(XMLSaver.PLAYER));
        Player player = PlayerFactory.getPlayerFromID(playerID);
        EditablePlayerRound round = new EditablePlayerRound(player);
        NodeList turns = element.getElementsByTagName(XMLSaver.TURN);
        for (int i = 0; i < 9; i++) {
            parseTurn((Element) turns.item(i), round, i + 1);
        }
        NodeList lastTurns = element.getElementsByTagName(XMLSaver.LAST_TURN);
        parseLastTurn((Element) lastTurns.item(0), round);
        return round;
    }

    private static void parseTurn(Element element, EditablePlayerRound round, int number) {
        int ball1 = Integer.parseInt(element.getAttribute(XMLSaver.THROW + "_" + 1));
        int ball2 = Integer.parseInt(element.getAttribute(XMLSaver.THROW + "_" + 2));
        boolean strike = Boolean.parseBoolean(element.getAttribute(XMLSaver.STRIKE));
        boolean spare = Boolean.parseBoolean(element.getAttribute(XMLSaver.SPARE));
        boolean split = Boolean.parseBoolean(element.getAttribute(XMLSaver.SPLIT));
        round.setThrowValue(number, 1, ball1);
        round.setThrowValue(number, 2, ball2);
        round.setTurnIsSplit(number, split);

        //TODO: test consistency with logged strike and spare
    }

    private static void parseLastTurn(Element element, EditablePlayerRound round) {
        int ball1 = Integer.parseInt(element.getAttribute(XMLSaver.THROW + "_" + 1));
        int ball2 = Integer.parseInt(element.getAttribute(XMLSaver.THROW + "_" + 2));
        int ball3 = Integer.parseInt(element.getAttribute(XMLSaver.THROW + "_" + 3));
        boolean strike = Boolean.parseBoolean(element.getAttribute(XMLSaver.STRIKE));
        boolean spare = Boolean.parseBoolean(element.getAttribute(XMLSaver.SPARE));
        boolean split = Boolean.parseBoolean(element.getAttribute(XMLSaver.SPLIT));
        round.setThrowValue(10, 1, ball1);
        round.setThrowValue(10, 2, ball2);
        if (ball3 > 0) {
            round.setThrowValue(10, 3, ball3);
        }
        round.setThrowValue(10, 2, ball2);
        round.setTurnIsSplit(10, split);

//        lastTurnElement.setAttribute(STRIKE, "" + lastTurn.isStrike());
//        lastTurnElement.setAttribute(SPARE, "" + lastTurn.isSpare());
//        lastTurnElement.setAttribute(SPLIT, "" + lastTurn.isSplit());
//        lastTurnElement.setAttribute(STRIKE_2, "" + lastTurn.isSecondBallStrike());
//        lastTurnElement.setAttribute(SPLIT_2, "" + lastTurn.isSecondBallSplit());
//        lastTurnElement.setAttribute(STRIKE_3, "" + lastTurn.isThirdBallStrike());
//        lastTurnElement.setAttribute(SPLIT_3, "" + lastTurn.isThirdBallSplit());
//        lastTurnElement.setAttribute(SPARE_3, "" + lastTurn.isThirdBallSpare());
        //TODO: test consistency with logged strike and spare
    }
     */
}
