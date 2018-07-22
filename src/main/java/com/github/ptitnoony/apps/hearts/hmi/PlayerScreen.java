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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import com.github.ptitnoony.apps.hearts.core.Player;

/**
 *
 * @author hamon
 */
public class PlayerScreen implements Screen {

    public static final String SCREEN_NAME = "PlayerScreen";

    private final PropertyChangeSupport propertyChangeSupport;

    private Parent rootNode;
    private PlayerScreenController controller;

    public PlayerScreen() {
        propertyChangeSupport = new PropertyChangeSupport(PlayerScreen.this);
        createScreenLayout();
    }

    private void createScreenLayout() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayerScreen.fxml"));
            rootNode = loader.load();
            controller = loader.getController();
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setPlayer(Player aPlayer) {
        if (controller != null) {
            controller.setPlayer(aPlayer);
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
        return rootNode;
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
