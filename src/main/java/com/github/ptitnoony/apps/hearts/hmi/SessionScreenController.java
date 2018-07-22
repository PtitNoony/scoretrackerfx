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

import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import static javafx.application.Platform.runLater;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.RangeSlider;
import com.github.ptitnoony.apps.hearts.core.Game;
import com.github.ptitnoony.apps.hearts.core.Player;
import com.github.ptitnoony.apps.hearts.core.PlayerStats;
import com.github.ptitnoony.apps.hearts.core.Session;

/**
 * 
 * @author hamon
 */
public class SessionScreenController implements Initializable {

    @FXML
    private LineChart<Number, Number> ratioChart;
    @FXML
    private NumberAxis ratioChartXAxis;
    @FXML
    private NumberAxis ratioChartYAxis;

    @FXML
    private CheckListView<Player> playerCheckList;

    @FXML
    private RangeSlider gamesRangeSlider;
    @FXML
    private RangeSlider ratioRangeSlider;

    private Session session = null;
    private Map<Player, XYChart.Series> playerDatas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ratioChartYAxis.setAutoRanging(false);
        ratioChartYAxis.setLowerBound(-1);
        ratioChartYAxis.setUpperBound(3);
        ratioChartYAxis.setTickUnit(0.5);
        ratioChartXAxis.setAutoRanging(false);
        ratioChartXAxis.setLowerBound(0);

        //
        gamesRangeSlider.setMinorTickCount(1);
        gamesRangeSlider.setMin(1);
        gamesRangeSlider.setLowValue(1);
        gamesRangeSlider.lowValueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            ratioChartXAxis.setLowerBound(newValue.intValue());
        });
        gamesRangeSlider.highValueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            ratioChartXAxis.setUpperBound(gamesRangeSlider.getHighValue());
        });

        //
        ratioRangeSlider.setMajorTickUnit(0.5);
        ratioRangeSlider.setMinorTickCount(5);
        ratioRangeSlider.setMin(-1);
        ratioRangeSlider.setLowValue(-1);
        ratioRangeSlider.setMax(3);
        ratioRangeSlider.setHighValue(3);
        ratioRangeSlider.lowValueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            ratioChartYAxis.setLowerBound(newValue.intValue());
        });
        ratioRangeSlider.highValueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            ratioChartYAxis.setUpperBound(newValue.intValue());
        });
        playerCheckList.getCheckModel().getCheckedItems().addListener((ListChangeListener.Change<? extends Player> c) -> {
            c.next();
            if (c.wasAdded()) {
                Player selectedPlayer = c.getAddedSubList().get(0);
                if (!ratioChart.getData().contains(playerDatas.get(selectedPlayer))) {
                    ratioChart.getData().add(playerDatas.get(selectedPlayer));
                }
            } else if (c.wasRemoved()) {
                Player selectedPlayer = c.getRemoved().get(0);
                if (ratioChart.getData().contains(playerDatas.get(selectedPlayer))) {
                    ratioChart.getData().remove(playerDatas.get(selectedPlayer));
                }
            }
        });
    }

    protected void addPropertyChangeListener(PropertyChangeListener listener) {

    }

    protected void setSession(Session aSession) {
        session = aSession;
        ratioChart.getData().clear();
        if (session != null) {
            runLater(this::refreshUI);
        }
    }

    private void refreshUI() {
        List<Game> games = session.getGames();
        List<Player> players = session.getPlayers();
        int maxGame = games.stream().mapToInt(Game::getIdInSession).max().orElse(0);
        playerDatas = new HashMap<>();
        //
        ratioChartXAxis.setUpperBound(maxGame);
        //
        gamesRangeSlider.setMax(maxGame);
        gamesRangeSlider.setHighValue(maxGame);
        //
        players.forEach(p -> {
            XYChart.Series playerData = new XYChart.Series();
            playerData.setName(p.getNickName());
            PlayerStats stats = p.getPlayerStats();

            stats.getPlayerRatioHistory(session).stream()
                    .sorted((p1,p2)->Integer.compare(p1.getKey(), p2.getKey()))
                    .forEachOrdered(pair -> playerData.getData().add(new XYChart.Data(pair.getKey(), pair.getValue())));
            playerDatas.put(p, playerData);
            ratioChart.getData().add(playerData);
        });
        //
        playerCheckList.getItems().setAll(players);
        playerCheckList.getCheckModel().checkAll();
    }
}
