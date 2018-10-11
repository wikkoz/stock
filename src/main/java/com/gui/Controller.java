package com.gui;

import com.domain.api.RatesService;
import com.domain.model.Trend;
import com.gui.util.DateUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.controlsfx.control.CheckListView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
public class Controller implements Initializable {
    private final RatesService ratesService;

    public Controller(RatesService ratesService) {
        this.ratesService = ratesService;
    }

    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    @FXML
    private LineChart<Date, Number> chart1;

    @FXML
    private CheckListView<String> checkListView;

    @FXML
    private DatePicker datepickerFrom;

    @FXML
    private DatePicker datepickerTo;

    @FXML
    private ListView<Label> trends;

    @FXML
    private ListView<Label> predicitons;

    private ObservableList<? extends String> currencies = FXCollections.emptyObservableList();

    @FXML
    public void updateDatepicker() {
        executorService.submit(this::updateFullModel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datepickerFrom.setValue(LocalDate.now().minusDays(30));
        datepickerTo.setValue(LocalDate.now());
        ratesService.getRates("EUR", LocalDate.now().minusDays(30), LocalDate.now());
        checkListView.setItems(FXCollections.observableArrayList(ratesService.getCurrencies()));
        checkListView.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c ->
                executorService.submit(() -> updateCheckedItems().onChanged(c)));
    }

    private ListChangeListener<String> updateCheckedItems() {
        return c -> {
            c.next();
            currencies = c.getList();
            if (c.wasAdded()) {
                String currency = c.getAddedSubList().get(0);
                updateCharSeries(currency);
                addTrend(currency);
            } else {
                Platform.runLater(() -> {
                    chart1.getData().removeIf(series -> series.getName().equals(c.getRemoved().get(0)));
                    trends.getItems().removeIf(trend -> trend.getText().contains(c.getRemoved().get(0)));
                });
            }
            updatePredictions();
        };
    }

    private void updateFullModel() {
        Platform.runLater(() -> {
            chart1.getData().clear();
            predicitons.getItems().clear();
            trends.getItems().clear();
        });
        updateFullChart();
        updatePredictions();
        updateTrends();
    }

    private void updateFullChart() {
        for (String currency : currencies) {
            updateCharSeries(currency);
        }
    }

    private void updateCharSeries(String currency) {
        XYChart.Series<Date, Number> series1 = new XYChart.Series<>();
        series1.setName(currency);
        ratesService.getRates(currency, datepickerFrom.getValue(), datepickerTo.getValue())
                .forEach(rate -> series1.getData().add(new XYChart.Data<>(DateUtil.fromLocalDate(rate.getEffectiveDate()), rate.getValue())));
        Platform.runLater(() -> chart1.getData().add(series1));
    }

    private void updateTrends() {
        List<Label> trendsList = currencies.stream()
                .map(c -> ratesService.getTrend(c, datepickerFrom.getValue(), datepickerTo.getValue()))
                .map(trend -> new Label(String.format("%s %.4f%%", trend.getCurrency(), trend.getTrend().doubleValue())))
                .collect(Collectors.toList());
        Platform.runLater(() -> trends.setItems(FXCollections.observableArrayList(trendsList)));
    }

    private void addTrend(String currency) {
        List<Label> trendsList = trends.getItems();
        Trend trend = ratesService.getTrend(currency, datepickerFrom.getValue(), datepickerTo.getValue());

        Platform.runLater(() -> trendsList.add(new Label(String.format("%s %.4f%%", trend.getCurrency(), trend.getTrend().doubleValue()))));
    }

    private void updatePredictions() {
        List<Label> predictionsList = currencies.stream()
                .map(currency -> ratesService.getPredictions(currency)
                        .stream()
                        .map(prediction -> String.format("%s: %.4f", prediction.getEffectiveDate(), prediction.getValue().doubleValue()))
                        .reduce(currency, (a, b) -> a + " " + b))
                .map(Label::new)
                .collect(Collectors.toList());
        Platform.runLater(() -> predicitons.setItems(FXCollections.observableArrayList(predictionsList)));
    }
}
