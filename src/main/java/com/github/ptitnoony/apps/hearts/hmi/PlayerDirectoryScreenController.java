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

import com.github.ptitnoony.apps.hearts.core.PlayerFactory;
import static com.github.ptitnoony.apps.hearts.hmi.PlayerDirectoryScreen.GO_TO_PLAYER_SCREEN_EVENT;
import com.github.ptitnoony.apps.hearts.utils.UIUtils;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import com.github.ptitnoony.apps.hearts.core.Player;
import com.github.ptitnoony.apps.hearts.core.PlayerStats;

/**
 *
 * @author hamon
 */
public class PlayerDirectoryScreenController implements Initializable {

    @FXML
    private VBox vBox;

    private FlowGridPane flowPane;

    private final Map<Player, Tile> playerTiles = new HashMap<>();
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(PlayerDirectoryScreenController.this);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        flowPane = new FlowGridPane(5, 3);
        flowPane.setHgap(5);
        flowPane.setVgap(5);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setCenterShape(true);
        flowPane.setPadding(new Insets(5));
        //
        vBox.getChildren().add(flowPane);
        //
        PlayerFactory.getCreatedPlayers().forEach(this::createPlayerTile);
        PlayerFactory.addListener(this::handlePlayerFactoryChange);
    }

    protected void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    private void createPlayerTile(Player player) {
        Tile tile = TileBuilder.create()
                .skinType(Tile.SkinType.TEXT)
                .prefSize(MainViewController.TILE_WIDTH, MainViewController.TILE_HEIGHT)
                .title("")
                .text(UIUtils.formatNumber(player.getPlayerStats().getCarrerRatio()))
                .description(player.getNickName())
                .descriptionAlignment(Pos.TOP_LEFT)
                .textVisible(true)
                .autoScale(false)
                .build();
        tile.setOnMouseClicked((MouseEvent event) -> {
            propertyChangeSupport.firePropertyChange(GO_TO_PLAYER_SCREEN_EVENT, null, player);
        });
        player.getPlayerStats().addPropertyChangeListener(this::handlePlayerChange);
        playerTiles.put(player, tile);
        flowPane.getChildren().add(tile);
    }

    private void handlePlayerFactoryChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case PlayerFactory.PLAYER_CREATED:
                createPlayerTile((Player) event.getNewValue());
                break;
            //todo default
        }
    }

    private void handlePlayerChange(PropertyChangeEvent event) {
        PlayerStats stats = (PlayerStats) event.getNewValue();
        Player player = stats.getPlayer();
        Tile tile = playerTiles.get(player);
        tile.setDescription(player.getNickName());
        tile.setText(UIUtils.formatNumber(player.getPlayerStats().getCarrerRatio()));
    }

}
