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

import com.github.ptitnoony.apps.hearts.utils.UIUtils;
import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import static javafx.application.Platform.runLater;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import com.github.ptitnoony.apps.hearts.core.Game;
import com.github.ptitnoony.apps.hearts.core.Player;
import com.github.ptitnoony.apps.hearts.core.Session;
import com.github.ptitnoony.apps.hearts.core.PlayerStats;
import javafx.scene.chart.LineChart;

/**
 *
 * @author hamon
 */
public class PlayerScreenController implements Initializable {

    private static enum MODE {
        CARRER, BY_SESSION
    }

    @FXML
    private RadioButton carrerRB;
    @FXML
    private RadioButton sessionRB;
    @FXML
    private ComboBox<Session> sessionBox;

    @FXML
    private Label playerLabel;
    @FXML
    private Label ratioLabel;
    @FXML
    private Label rankingLabel;
    @FXML
    private Label nbGamesLabel;
    @FXML
    private Label nemesisLabel;
    @FXML
    private Label bestSupplierLabel;

    @FXML
    private Accordion accordion;
    @FXML
    private TitledPane gameEvolutionPane;

    @FXML
    private TableView<Game> gamesTable;
    @FXML
    private TableColumn<Game, String> gameIdColumn;
    @FXML
    private TableColumn<Game, String> gameIdSessionInSession;
    @FXML
    private TableColumn<Game, String> pointsColumn;
    @FXML
    private TableColumn<Game, String> ratiosColumn;

    @FXML
    private ScatterChart<String, Number> gamesChart;
    @FXML
    private LineChart<String, Number> ratioChart;
    @FXML
    private PieChart pieChart;

    private ToggleGroup modeGroup;

    private MODE mode;
    private Player player;
    private PlayerStats stats;
    private Session sessionSelected = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gameIdColumn.setPrefWidth(75);
        gameIdColumn.setCellValueFactory((TableColumn.CellDataFeatures<Game, String> param)
                -> new ReadOnlyStringWrapper("" + param.getValue().getUniqueID()));
        //
        gameIdSessionInSession.setPrefWidth(150);
        gameIdSessionInSession.setCellValueFactory((TableColumn.CellDataFeatures<Game, String> param)
                -> new ReadOnlyStringWrapper("" + param.getValue().getIdInSession()));
        //
        pointsColumn.setPrefWidth(75);
        pointsColumn.setCellValueFactory((TableColumn.CellDataFeatures<Game, String> param)
                -> new ReadOnlyStringWrapper("" + param.getValue().getPlayerScore(player)));
        //
        ratiosColumn.setPrefWidth(75);
        ratiosColumn.setCellValueFactory((TableColumn.CellDataFeatures<Game, String> param)
                -> new ReadOnlyStringWrapper(stats != null ? "" + "TODO" : ""));
        //
        gamesChart.getXAxis().setTickLabelsVisible(false);
        gamesChart.getXAxis().setTickMarkVisible(false);
        gamesChart.getXAxis().setAutoRanging(true);
        gamesChart.getYAxis().setAutoRanging(true);
        gamesChart.setLegendVisible(false);
        //
        ratioChart.getXAxis().setTickLabelsVisible(false);
        ratioChart.getXAxis().setTickMarkVisible(false);
        //
        pieChart.setLegendVisible(false);
        //
        modeGroup = new ToggleGroup();
        modeGroup.getToggles().add(carrerRB);
        modeGroup.getToggles().add(sessionRB);
        carrerRB.setOnAction(e -> {
            setMode(MODE.CARRER);
        });
        sessionRB.setOnAction(e -> {
            setMode(MODE.BY_SESSION);
        });
        sessionBox.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            sessionSelected = sessionBox.getSelectionModel().getSelectedItem();
            updateStatsUI();
        });
        setMode(MODE.CARRER);
    }

    protected void setPlayer(Player aPlayer) {
        if (stats != null) {
            stats.removePropertyChangeListener(this::handlePlayerUpdate);
        }
        player = aPlayer;
        stats = player.getPlayerStats();
        stats.addPropertyChangeListener(this::handlePlayerUpdate);
        playerLabel.setText(player.getNickName());
        ObservableList<Session> sessions = FXCollections.observableArrayList(stats.getSessionsPlayed());
        sessionBox.setItems(sessions);
        if (!sessionBox.getItems().isEmpty()) {
            sessionBox.getSelectionModel().selectFirst();
        }
        updateStatsUI();
        //
        accordion.setExpandedPane(gameEvolutionPane);
    }

    private void setMode(MODE aMode) {
        mode = aMode;
        updateStatsUI();
    }

    private void handlePlayerUpdate(PropertyChangeEvent event) {
        updateStatsUI();
    }

    private void updateStatsUI() {
        runLater(() -> {
            if (stats != null) {
                switch (mode) {
                    case BY_SESSION:
                        sessionRB.setSelected(true);
                        sessionBox.setDisable(false);
                        displayStatsForSession();
                        break;
                    case CARRER:
                        carrerRB.setSelected(true);
                        sessionBox.setDisable(true);
                        displayStatsForCarrer();
                        break;
                }
            } else {
                clearStatsLabels();
            }
        });
    }

    private void displayStatsForCarrer() {
        ratioLabel.setText(UIUtils.formatNumber(stats.getCarrerRatio()));
        List<Game> games = stats.getGames();
        nbGamesLabel.setText(Integer.toString(stats.getNbGamesPlayed()));
        rankingLabel.setText("TODO");
        nemesisLabel.setText("TODO");
        displayStatsForGames(games);
        displayCarrerRatioHistory();
    }

    private void displayStatsForSession() {
        Session session = sessionBox.getSelectionModel().getSelectedItem();
        if (session != null) {
            List<Game> sessionGames = stats.getGamesBySession(session);
            ratioLabel.setText(UIUtils.formatNumber(stats.getSessionRatio(session)));
            nbGamesLabel.setText(Integer.toString(sessionGames.size()));
            rankingLabel.setText("TODO");
            nemesisLabel.setText("TODO");
            displayStatsForGames(sessionGames);
            displaySessionRatioHistory();
        } else {
            ratioLabel.setText("TODO");
            nbGamesLabel.setText("TODO");
            rankingLabel.setText("TODO");
            nemesisLabel.setText("TODO");
        }
    }

    private void clearStatsLabels() {
        ratioLabel.setText("");
        nbGamesLabel.setText("");
        rankingLabel.setText("");
        nemesisLabel.setText("");
    }

    private void displayStatsForGames(List<Game> games) {
        gamesTable.getItems().setAll(games);
        //
        gamesChart.getData().clear();
        XYChart.Series gamesSeries = new XYChart.Series();
        gamesSeries.setName("Games");
        games.forEach(game -> gamesSeries.getData().add(new XYChart.Data(Integer.toString(game.getIdInSession()), game.getPlayerScore(player))));
        gamesChart.getData().setAll(gamesSeries);
        //
        ObservableList<PieChart.Data> pieChartData
                = FXCollections.observableArrayList(
                        new PieChart.Data("3", games.stream().filter(g -> g.getPlayerScore(player) == 3).count()),
                        new PieChart.Data("1", games.stream().filter(g -> g.getPlayerScore(player) == 1).count()),
                        new PieChart.Data("0", games.stream().filter(g -> g.getPlayerScore(player) == 0).count()),
                        new PieChart.Data("-1", games.stream().filter(g -> g.getPlayerScore(player) == -1).count()),
                        new PieChart.Data("4", games.stream().filter(g -> g.getPlayerScore(player) == 4).count())
                );
        pieChart.setData(pieChartData);
    }

    private void displayCarrerRatioHistory() {
        ratioChart.getData().clear();
        XYChart.Series playerData = new XYChart.Series();
        playerData.setName(player.getNickName());
        stats.getPlayerRatioHistory().stream()
                .sorted((p1, p2) -> Integer.compare(p1.getKey(), p2.getKey()))
                .forEachOrdered(pair -> playerData.getData().add(new XYChart.Data("" + pair.getKey(), pair.getValue()))); // !!!
        ratioChart.getData().add(playerData);
    }

    private void displaySessionRatioHistory() {
        ratioChart.getData().clear();
        if (sessionSelected != null) {
            XYChart.Series playerData = new XYChart.Series();
            playerData.setName(player.getNickName());
            stats.getPlayerRatioHistory(sessionSelected).stream()
                    .sorted((p1, p2) -> Integer.compare(p1.getKey(), p2.getKey()))
                    .forEachOrdered(pair -> playerData.getData().add(new XYChart.Data("" + pair.getKey(), pair.getValue()))); // !!!
            ratioChart.getData().add(playerData);
        }
    }
}
