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
package com.github.ptitnoony.apps.hearts.hmi;

import fr.noony.fxapp.Screen;
import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.SegmentedButton;
import com.github.ptitnoony.apps.hearts.core.Player;
import com.github.ptitnoony.apps.hearts.core.Session;

/**
 *
 * @author hamon
 */
public class MainViewController implements Initializable {

    public static final double TILE_WIDTH = 200;
    public static final double TILE_HEIGHT = 200;

    @FXML
    private BorderPane rootPane;
    @FXML
    private SegmentedButton toolbar;
    @FXML
    private AnchorPane centerPane;

    private final Map<String, Screen> screens = new HashMap<>();

    private ToggleButton homeButton;
    private ToggleButton playerDirectoryScreenToggle;
    private ToggleButton playerScreenToggle;

    private ToggleButton sessionScreenToggle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createNavigationBar();
        loadHomeScreen();
    }

    private void createNavigationBar() {
        SegmentedButton segmentedButton = new SegmentedButton();
        segmentedButton.getStyleClass().add(SegmentedButton.STYLE_CLASS_DARK);
    }

    private void loadHomeScreen() {
        Screen homeScreen = screens.get(HomeScreen.SCREEN_NAME);
        if (homeScreen == null) {
            homeScreen = new HomeScreen();
            homeScreen.addPropertyChangeListener(this::handleHomeScreenEvents);
            homeButton = new ToggleButton(homeScreen.getName());
            homeButton.setOnAction(e -> loadHomeScreen());
            screens.put(HomeScreen.SCREEN_NAME, homeScreen);
        }
        homeButton.setDisable(true);
        homeButton.setSelected(true);
        toolbar.getButtons().setAll(homeButton);
        setAnchorPaneConstant(homeScreen.getMainNode());
    }

    private void loadPlayerDirectoryScreen() {
        Screen playerDirectoryScreen = screens.get(PlayerDirectoryScreen.SCREEN_NAME);
        if (playerDirectoryScreen == null) {
            playerDirectoryScreen = new PlayerDirectoryScreen();
            playerDirectoryScreen.addPropertyChangeListener(this::handlePlayerDirectoryScreenEvents);
            playerDirectoryScreenToggle = new ToggleButton(playerDirectoryScreen.getName());
            playerDirectoryScreenToggle.setOnAction(e -> loadPlayerDirectoryScreen());
            screens.put(playerDirectoryScreen.getName(), playerDirectoryScreen);
        }
        playerDirectoryScreenToggle.setDisable(true);
        homeButton.setDisable(false);
        toolbar.getButtons().setAll(homeButton, playerDirectoryScreenToggle);
        setAnchorPaneConstant(playerDirectoryScreen.getMainNode());
    }

    private void loadPlayerScreen(Player player) {
        Screen playerScreen = screens.get(PlayerScreen.SCREEN_NAME);
        if (playerScreen == null) {
            playerScreen = new PlayerScreen();
            playerScreenToggle = new ToggleButton(playerScreen.getName());
            screens.put(PlayerScreen.SCREEN_NAME, playerScreen);
        }
        homeButton.setDisable(false);
        // split in a method
        if (playerDirectoryScreenToggle != null) {
            playerDirectoryScreenToggle.setDisable(false);
            toolbar.getButtons().setAll(homeButton, playerDirectoryScreenToggle, playerScreenToggle);
        } else {
            toolbar.getButtons().setAll(homeButton, playerScreenToggle);
        }
        playerScreenToggle.setDisable(true);
        ((PlayerScreen) playerScreen).setPlayer(player);
        setAnchorPaneConstant(playerScreen.getMainNode());
    }

    private void loadSessionScreen(Session session) {
        Screen sessionScreen = screens.get(SessionScreen.SCREEN_NAME);
        if (sessionScreen == null) {
            sessionScreen = new SessionScreen();
            sessionScreenToggle = new ToggleButton(sessionScreen.getName());
            screens.put(SessionScreen.SCREEN_NAME, sessionScreen);
        }
        homeButton.setDisable(false);
        sessionScreenToggle.setDisable(true);
        homeButton.setSelected(false);
        toolbar.getButtons().setAll(homeButton, sessionScreenToggle);
        ((SessionScreen) sessionScreen).setSession(session);
        setAnchorPaneConstant(sessionScreen.getMainNode());

    }

    private void handleHomeScreenEvents(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ScreenEvents.GO_TO_PLAYER_DIRECTORY_EVENT:
                loadPlayerDirectoryScreen();
                break;
            case ScreenEvents.GO_TO_SESSION_SCREEN:
                loadSessionScreen((Session) event.getNewValue());
                break;
            case PlayerDirectoryScreen.GO_TO_PLAYER_SCREEN_EVENT:
                loadPlayerScreen((Player) event.getNewValue());
                break;
            default:
                throw new UnsupportedOperationException("handleHomeScreenEvents cannot handle " + event);
        }
    }

    private void handlePlayerDirectoryScreenEvents(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case PlayerDirectoryScreen.GO_TO_PLAYER_SCREEN_EVENT:
                loadPlayerScreen((Player) event.getNewValue());
                break;
            default:
                throw new UnsupportedOperationException("handlePlayerDirectoryScreenEvents cannot handle " + event);
        }
    }

    private void setAnchorPaneConstant(Node node) {
        centerPane.getChildren().setAll(node);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
    }

}
