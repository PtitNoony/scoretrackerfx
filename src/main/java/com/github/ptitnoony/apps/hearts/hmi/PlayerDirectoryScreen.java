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

import eu.hansolo.tilesfx.Tile;
import fr.noony.fxapp.Screen;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author hamon
 */
public class PlayerDirectoryScreen implements Screen {

    public static final String GO_TO_PLAYER_SCREEN_EVENT = "goToPlayerScreen";
    public static final String SCREEN_NAME = "PlayerDirectory";
    public static final String FXML_NAME = "PlayerDirectoryScreen.fxml";

    private final PropertyChangeSupport propertyChangeSupport;
    private final List<Tile> playerTiles = new LinkedList<>();

    private AnchorPane rootPane;
    private PlayerDirectoryScreenController controller;

    public PlayerDirectoryScreen() {
        propertyChangeSupport = new PropertyChangeSupport(PlayerDirectoryScreen.this);
        createScreenLayout();
    }

    private void createScreenLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_NAME));
            rootPane = loader.load();
            controller = loader.getController();
            controller.addPropertyChangeListener(e -> propertyChangeSupport.firePropertyChange(e));
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public Node getMenuItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node getMainNode() {
        return rootPane;
    }

    @Override
    public String getName() {
        return SCREEN_NAME;
    }

    @Override
    public void refresh() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
