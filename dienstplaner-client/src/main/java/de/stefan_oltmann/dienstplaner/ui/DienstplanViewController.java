package de.stefan_oltmann.dienstplaner.ui; // NOSONAR

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import de.stefan_oltmann.dienstplaner.dao.DataAccessService;
import de.stefan_oltmann.dienstplaner.model.Arbeitsbereich;
import de.stefan_oltmann.dienstplaner.model.DienstplanDatei;
import de.stefan_oltmann.dienstplaner.model.Mitarbeiter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.LocalDateTimeRange;

public class DienstplanViewController implements Initializable {

    @FXML
    private jfxtras.scene.control.agenda.Agenda agenda;

    @FXML
    private FlowPane                            calendarControlsPane;

    @FXML
    private ListView<Arbeitsbereich>            arbeitsbereicheListView;
    private ObservableList<Arbeitsbereich>      arbeitsbereicheList;

    @FXML
    private ListView<Mitarbeiter>               mitarbeiterListView;
    private ObservableList<Mitarbeiter>         mitarbeiterList;

    private DataAccessService                   dataAccessService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        /* Die Agenda soll analog zu Outlook 30 Minuten genau snappen. */
        this.agenda.setStyle("-fxx-snap-to-minutes: 30;");

        this.arbeitsbereicheList = FXCollections.observableArrayList();
        this.arbeitsbereicheListView.setItems(arbeitsbereicheList);

        this.mitarbeiterList = FXCollections.observableArrayList();
        this.mitarbeiterListView.setItems(mitarbeiterList);

        final Map<String, Agenda.AppointmentGroup> lAppointmentGroupMap = new TreeMap<>();
        for (Agenda.AppointmentGroup lAppointmentGroup : agenda.appointmentGroups()) {
            lAppointmentGroupMap.put(lAppointmentGroup.getDescription(), lAppointmentGroup);
        }

        agenda.newAppointmentCallbackProperty().set(new Callback<Agenda.LocalDateTimeRange, Agenda.Appointment>() {
            @Override
            public Agenda.Appointment call(LocalDateTimeRange dateTimeRange) {

                return new Agenda.AppointmentImplLocal()
                        .withStartLocalDateTime(dateTimeRange.getStartLocalDateTime())
                        .withEndLocalDateTime(dateTimeRange.getEndLocalDateTime())
                        .withSummary("new")
                        .withDescription("new")
                        .withAppointmentGroup(lAppointmentGroupMap.get("group01"));
            }
        });

    }

    public void setDataAccessService(DataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
    }

    public void onShown() {
        /* Wird aufgerufen, wenn die View angezeigt wird. */

        DienstplanDatei dienstplanDatei = dataAccessService.getAktuelleDienstplanDatei();

        mitarbeiterList.setAll(dienstplanDatei.findAllMitarbeiter());
        arbeitsbereicheList.setAll(dienstplanDatei.findAllArbeitsbereich());
    }
}
