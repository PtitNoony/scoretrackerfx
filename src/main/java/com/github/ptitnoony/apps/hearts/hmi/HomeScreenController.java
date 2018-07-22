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

import com.github.ptitnoony.apps.hearts.AppState;
import com.github.ptitnoony.apps.hearts.SessionParser;
import com.github.ptitnoony.apps.hearts.core.PlayerFactory;
import static com.github.ptitnoony.apps.hearts.hmi.ScreenEvents.GO_TO_PLAYER_DIRECTORY_EVENT;
import static com.github.ptitnoony.apps.hearts.hmi.ScreenEvents.GO_TO_PLAYER_SCREEN;
import static com.github.ptitnoony.apps.hearts.hmi.ScreenEvents.GO_TO_SESSION_SCREEN;
import com.github.ptitnoony.apps.hearts.utils.UIUtils;
import com.github.ptitnoony.apps.hearts.utils.XMLSaver;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import com.github.ptitnoony.apps.hearts.core.League;
import com.github.ptitnoony.apps.hearts.core.LeagueFactory;
import com.github.ptitnoony.apps.hearts.core.Player;
import com.github.ptitnoony.apps.hearts.core.Session;
import com.github.ptitnoony.apps.hearts.utils.XMLLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

/**
 *
 * @author hamon
 */
public class HomeScreenController implements Initializable {

    private static final double TILE_WIDTH = 150;

    @FXML
    private AnchorPane homeScreenPane;
    @FXML
    private AnchorPane leftPane;
    
    @FXML
    private ComboBox<League> leagueComboBox;

    // Session Part
    @FXML
    private TableView<Session> sessionTable;
    @FXML
    private TableColumn<Session, String> sessionIdColumn;
    @FXML
    private TableColumn<Session, String> sessionPlayersColumn;
    @FXML
    private TableColumn<Session, String> sessionGamesColumn;
    @FXML
    private TableColumn<Session, Button> viewSessionColumn;

    // Players Part
    @FXML
    private TableView<Player> playerTable;
    @FXML
    private TableColumn<Player, String> nickNameColumn;
    @FXML
    private TableColumn<Player, String> avgRationColumn;
    @FXML
    private TableColumn<Player, Integer> nbGamesColumn;
    @FXML
    private TableColumn<Player, Integer> nbSessionsColumn;
    @FXML
    private TableColumn<Player, String> bestRatioColumn;
    @FXML
    private TableColumn<Player, Button> viewPlayerColumn;

    private FlowGridPane leftFlowPane;

    private final List<Tile> homeTiles = new LinkedList<>();
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(HomeScreenController.this);
    private final FileChooser fileChooser = new FileChooser();

    private Tile sessionsTile;
    private Tile playersTile;

    private League league;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        AppState.addPropertyChangeListener(this::handleLeagueChange);
        league = AppState.getCurrentLeague();
        if (league != null) {
            league.addPropertyChangeListener(this::handleLeagueUpdate);
        }
        initLayout();
    }

    @FXML
    protected void handleCreateNewLeague(ActionEvent event) {
        LeagueFactory.createLeague();
    }

    @FXML
    protected void handleImportSession(ActionEvent event) {
        fileChooser.setTitle("Import session");
        File sessionFile = fileChooser.showOpenDialog(homeScreenPane.getScene().getWindow());
        if (sessionFile != null) {
            AppState.getCurrentLeague().addSession(SessionParser.parseLog(sessionFile));
        }
    }

    @FXML
    protected void handleLoadLeague(ActionEvent event) {
        fileChooser.setTitle("Load League");
        File leagueFile = fileChooser.showOpenDialog(homeScreenPane.getScene().getWindow());
        if (leagueFile != null) {
            AppState.setLeague(XMLLoader.loadFile(leagueFile));
        }
    }

    @FXML
    protected void handleSaveLeague(ActionEvent event) {
        File destFile = fileChooser.showSaveDialog(homeScreenPane.getScene().getWindow());
        if (destFile != null) {
            XMLSaver.save(destFile);
        }
    }

    protected void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    private void initLayout() {
        initLeftSidePane();
        initSessionsTab();
        initPlayersTab();
    }

    private void initLeftSidePane() {
        sessionsTile = TileBuilder.create()
                .skinType(Tile.SkinType.TEXT)
                .prefSize(MainViewController.TILE_WIDTH, MainViewController.TILE_HEIGHT)
                .maxSize(TILE_WIDTH, TILE_WIDTH)
                .title("")
                .text("")
                .description("Sessions")
                .descriptionAlignment(Pos.TOP_LEFT)
                .textVisible(true)
                .textColor(Color.BLACK)
                .autoScale(false)
                .backgroundColor(Color.DARKGREY)
                .build();
        playersTile = TileBuilder.create()
                .skinType(Tile.SkinType.TEXT)
                .prefSize(TILE_WIDTH, TILE_WIDTH)
                .title("")
                .text("" + PlayerFactory.getCreatedPlayers().size() + " players")
                .description("Player Directory")
                .descriptionAlignment(Pos.TOP_LEFT)
                .textVisible(true)
                .autoScale(false)
                .maxSize(TILE_WIDTH, TILE_WIDTH)
                .textColor(Color.BLACK)
                .backgroundColor(Color.DARKGREY)
                .build();
        playersTile.setOnMouseClicked((MouseEvent event) -> {
            propertyChangeSupport.firePropertyChange(GO_TO_PLAYER_DIRECTORY_EVENT, null, null);
        });
//        sessionsTile.setOnMouseClicked((MouseEvent event) -> {
//            propertyChangeSupport.firePropertyChange(GO_TO_COMPARISON_SCREEN_EVENT, null, null);
//        });
        homeTiles.add(sessionsTile);
        homeTiles.add(playersTile);

        leftFlowPane = new FlowGridPane(2, 1, homeTiles.toArray(new Tile[homeTiles.size()]));
        leftFlowPane.setHgap(5);
        leftFlowPane.setVgap(5);
        leftFlowPane.setAlignment(Pos.CENTER);
        leftFlowPane.setCenterShape(true);
        leftFlowPane.setPadding(new Insets(5));
        //
        leftPane.getChildren().add(leftFlowPane);
        AnchorPane.setBottomAnchor(leftFlowPane, 8.0);
        AnchorPane.setLeftAnchor(leftFlowPane, 8.0);
        AnchorPane.setRightAnchor(leftFlowPane, 8.0);
        AnchorPane.setTopAnchor(leftFlowPane, 8.0);
    }

    private void initSessionsTab() {
        sessionIdColumn.setPrefWidth(250);
        sessionIdColumn.setCellValueFactory((TableColumn.CellDataFeatures<Session, String> param) -> new ReadOnlyStringWrapper("" + param.getValue().getName()));
        //
        sessionPlayersColumn.setPrefWidth(100);
        sessionPlayersColumn.setCellValueFactory((TableColumn.CellDataFeatures<Session, String> param) -> new ReadOnlyStringWrapper("" + param.getValue().getNbPlayers()));
        //
        sessionGamesColumn.setPrefWidth(100);
        sessionGamesColumn.setCellValueFactory((TableColumn.CellDataFeatures<Session, String> param) -> new ReadOnlyStringWrapper("" + param.getValue().getGames().size()));
        //
        viewSessionColumn.setPrefWidth(150);
        viewSessionColumn.setCellFactory(ActionButtonTableCell.<Session>forTableColumn("View session", (Session s) -> {
            propertyChangeSupport.firePropertyChange(GO_TO_SESSION_SCREEN, null, s);
            return s;
        }));
    }

    private void initPlayersTab() {
        nickNameColumn.setPrefWidth(250);
        nickNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<Player, String> param) -> new ReadOnlyStringWrapper("" + param.getValue().getNickName()));
        //
        avgRationColumn.setPrefWidth(75);
        avgRationColumn.setCellValueFactory((TableColumn.CellDataFeatures<Player, String> param) -> new ReadOnlyStringWrapper(UIUtils.formatNumber(param.getValue().getPlayerStats().getCarrerRatio())));
        avgRationColumn.setComparator((s1, s2) -> Double.compare(UIUtils.parseFormatedNumber(s1), UIUtils.parseFormatedNumber(s2)));

        //
        nbGamesColumn.setPrefWidth(75);
        nbGamesColumn.setCellValueFactory((TableColumn.CellDataFeatures<Player, Integer> param) -> new ReadOnlyObjectWrapper<>(param.getValue().getPlayerStats().getNbGamesPlayed()));
        //
        nbSessionsColumn.setPrefWidth(75);
        nbSessionsColumn.setCellValueFactory((TableColumn.CellDataFeatures<Player, Integer> param) -> new ReadOnlyObjectWrapper(param.getValue().getPlayerStats().getNbSessionsPlayed()));
        //
        bestRatioColumn.setPrefWidth(75);
        bestRatioColumn.setCellValueFactory((TableColumn.CellDataFeatures<Player, String> param) -> new ReadOnlyStringWrapper(UIUtils.formatNumber(param.getValue().getPlayerStats().getBestRatio())));
        bestRatioColumn.setComparator((s1, s2) -> Double.compare(UIUtils.parseFormatedNumber(s1), UIUtils.parseFormatedNumber(s2)));
        //
        viewPlayerColumn.setPrefWidth(150);
        viewPlayerColumn.setCellFactory(ActionButtonTableCell.<Player>forTableColumn("View player", (Player p) -> {
            propertyChangeSupport.firePropertyChange(GO_TO_PLAYER_SCREEN, null, p);
            return p;
        }));
    }

    private void handleLeagueChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case AppState.LEAGUE_CHANGED:
                if (league != null) {
                    league.removePropertyChangeListener(this::handleLeagueUpdate);
                }
                ObservableList<League> obsList = FXCollections.observableList(LeagueFactory.getLeagues());
                leagueComboBox.setItems(obsList);
                league = (League) event.getNewValue();
                if (league != null) {
                    league.addPropertyChangeListener(this::handleLeagueUpdate);
                    leagueComboBox.getSelectionModel().select(league);
                }
                break;
        }
        // log event
        refreshHMI();

    }

    private void handleLeagueUpdate(PropertyChangeEvent event) {
        // log event
        refreshHMI();
    }

    private void refreshHMI() {
        if (league != null) {
            sessionsTile.setText(Integer.toString(league.getNbSessions()));
            playersTile.setText("" + PlayerFactory.getCreatedPlayers().size() + " players");
            List<Session> sessions = league.getSessions();
            List<Player> players = league.getPlayers();
            sessionTable.getItems().setAll(sessions);
            playerTable.getItems().setAll(players);
        }
    }
}
