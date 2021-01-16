/*****************************************************************************
 * Stefans Dienstplaner                                                      *
 *                                                                           *
 * Copyright (C) 2017 Stefan Oltmann                                         *
 *                                                                           *
 * Contact : dienstplaner@stefan-oltmann.de                                  *
 * Homepage: http://www.stefan-oltmann.de/                                   *      
 *                                                                           *
 * This program is free software: you can redistribute it and/or modify      *
 * it under the terms of the GNU Affero General Public License as            *
 * published by the Free Software Foundation, either version 3 of the        *
 * License, or (at your option) any later version.                           *
 *                                                                           *
 * This program is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of            *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the              *
 * GNU Affero General Public License for more details.                       *
 *                                                                           *
 * You should have received a copy of the GNU Affero General Public License  *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.     *
 *****************************************************************************/
package de.stefan_oltmann.dienstplaner.ui; //NOSONAR

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import org.controlsfx.control.ListSelectionView;

import de.stefan_oltmann.dienstplaner.dao.DataAccessService;
import de.stefan_oltmann.dienstplaner.model.Arbeitsbereich;
import de.stefan_oltmann.dienstplaner.model.DienstplanDatei;
import de.stefan_oltmann.dienstplaner.model.Mitarbeiter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

public class MitarbeiterViewController implements Initializable {

    @FXML
    private ListView<Mitarbeiter>             mitarbeiterListView;
    private ObservableList<Mitarbeiter>       mitarbeiterList;

    @FXML
    private TextField                         vornameFeld;

    @FXML
    private TextField                         nachnameFeld;

    @FXML
    private TextField                         emailAdresseFeld;

    @FXML
    private TextField                         telefonNummerFeld;

    @FXML
    private TextField                         personalNummerFeld;

    @FXML
    private Spinner<Integer>                  stundenProWocheFeld;
    private SpinnerValueFactory<Integer>      stundenProWocheValueFactory;

    @FXML
    private Button                            neuButton;

    @FXML
    private Button                            loeschenButton;

    @FXML
    private Button                            speichernButton;

    @FXML
    private ListSelectionView<Arbeitsbereich> listSelectionView;

    private ObservableList<Arbeitsbereich>    verfuegbareArbeitsbereicheListe;
    private ObservableList<Arbeitsbereich>    zugewieseneArbeitsbereicheListe;

    private DataAccessService                 dataAccessService;

    private Mitarbeiter                       aktuellerMitarbeiter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.mitarbeiterList = FXCollections.observableArrayList();
        this.mitarbeiterListView.setItems(mitarbeiterList);
        this.mitarbeiterListView.getSelectionModel().selectedItemProperty()
                .addListener(new SelectedMitarbeiterChangeListener());

        this.verfuegbareArbeitsbereicheListe = FXCollections.observableArrayList();
        this.zugewieseneArbeitsbereicheListe = FXCollections.observableArrayList();

        this.listSelectionView.setSourceItems(verfuegbareArbeitsbereicheListe);
        this.listSelectionView.setTargetItems(zugewieseneArbeitsbereicheListe);

        this.stundenProWocheValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                0, Integer.MAX_VALUE, Mitarbeiter.DEFAULT_STUNDEN_PRO_WOCHE);
        this.stundenProWocheFeld.setValueFactory(stundenProWocheValueFactory);

        ((javafx.scene.control.Label) this.listSelectionView.getSourceHeader()).setText("Verfügbare Arbeitsbereiche");
        ((javafx.scene.control.Label) this.listSelectionView.getTargetHeader()).setText("Zugewiesene Arbeitsbereiche");

        /* Initial ist das Formular disabled. */
        setEintragEditierenEnabled(false);
    }

    public void setDataAccessService(DataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
        aktualisiereMitarbeiterListe();
    }

    private void aktualisiereMitarbeiterListe() {

        DienstplanDatei dienstplanDatei = dataAccessService.getAktuelleDienstplanDatei();

        Set<Mitarbeiter> alleMitarbeiter = dienstplanDatei.findAllMitarbeiter();

        mitarbeiterList.setAll(alleMitarbeiter);
    }

    private void onSelectedMitarbeiterChanged(Mitarbeiter mitarbeiter) {

        DienstplanDatei dienstplanDatei = dataAccessService.getAktuelleDienstplanDatei();

        this.aktuellerMitarbeiter = mitarbeiter;

        setEintragEditierenEnabled(mitarbeiter != null);

        if (mitarbeiter != null) {

            vornameFeld.setText(mitarbeiter.getVorname());
            nachnameFeld.setText(mitarbeiter.getNachname());
            emailAdresseFeld.setText(mitarbeiter.getEmailAdresse());
            telefonNummerFeld.setText(mitarbeiter.getTelefonNummer());
            personalNummerFeld.setText(mitarbeiter.getPersonalNummer());
            stundenProWocheValueFactory.setValue(mitarbeiter.getStundenProWoche());

            {
                Set<Arbeitsbereich> zugewieseneArbeitsbereiche = dienstplanDatei.findAllArbeitsbereicheFor(mitarbeiter);
                Set<Arbeitsbereich> verfuegbareArbeitsbereiche = new HashSet<>(dienstplanDatei.findAllArbeitsbereich());

                verfuegbareArbeitsbereiche.removeAll(zugewieseneArbeitsbereiche);

                verfuegbareArbeitsbereicheListe.setAll(verfuegbareArbeitsbereiche);
                zugewieseneArbeitsbereicheListe.setAll(zugewieseneArbeitsbereiche);
            }

        } else {

            vornameFeld.clear();
            nachnameFeld.clear();
            emailAdresseFeld.clear();
            telefonNummerFeld.clear();
            personalNummerFeld.clear();
            stundenProWocheValueFactory.setValue(0);

            verfuegbareArbeitsbereicheListe.clear();
            zugewieseneArbeitsbereicheListe.clear();
        }
    }

    private void setEintragEditierenEnabled(boolean enabled) {

        /* Eingabe-Felder */
        vornameFeld.setDisable(!enabled);
        nachnameFeld.setDisable(!enabled);
        emailAdresseFeld.setDisable(!enabled);
        telefonNummerFeld.setDisable(!enabled);
        personalNummerFeld.setDisable(!enabled);
        stundenProWocheFeld.setDisable(!enabled);

        listSelectionView.setDisable(!enabled);

        /* Aktions-Buttons */
        loeschenButton.setDisable(!enabled);
        speichernButton.setDisable(!enabled);
    }

    @FXML
    private void onNeuerMitarbeiter(
            @SuppressWarnings("unused") ActionEvent event) {

        aktuellerMitarbeiter = new Mitarbeiter();

        onSelectedMitarbeiterChanged(aktuellerMitarbeiter);

        /*
         * Den Neu-Button deaktivieren, damit klar ist, dass man jetzt auf
         * "Speichern" klicken soll.
         */
        neuButton.setDisable(true);
    }

    @FXML
    private void onMitarbeiterSpeichern(
            @SuppressWarnings("unused") ActionEvent event) {

        try {

            DienstplanDatei dienstplanDatei = dataAccessService.getAktuelleDienstplanDatei();

            aktuellerMitarbeiter.setVorname(vornameFeld.getText());
            aktuellerMitarbeiter.setNachname(nachnameFeld.getText());
            aktuellerMitarbeiter.setEmailAdresse(emailAdresseFeld.getText());
            aktuellerMitarbeiter.setTelefonNummer(telefonNummerFeld.getText());
            aktuellerMitarbeiter.setPersonalNummer(personalNummerFeld.getText());
            aktuellerMitarbeiter.setStundenProWoche(stundenProWocheFeld.getValue());

            dienstplanDatei.saveMitarbeiter(aktuellerMitarbeiter);

            dienstplanDatei.saveMitarbeiterArbeitsbereichZuordnung(
                    aktuellerMitarbeiter, zugewieseneArbeitsbereicheListe);

            aktualisiereMitarbeiterListe();

            /* Jetzt dürfen wieder neue Einträge erzeugt werden. */
            neuButton.setDisable(false);

            dataAccessService.speicherDienstplanDatei();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onMitarbeiterLoeschen(
            @SuppressWarnings("unused") ActionEvent event) {

        DienstplanDatei dienstplanDatei = dataAccessService.getAktuelleDienstplanDatei();

        dienstplanDatei.deleteMitarbeiter(aktuellerMitarbeiter);

        aktualisiereMitarbeiterListe();

        dataAccessService.speicherDienstplanDatei();
    }

    public void onShown() {
        /* Wird aufgerufen, wenn die View angezeigt wird. */
    }

    private class SelectedMitarbeiterChangeListener implements ChangeListener<Mitarbeiter> {

        @Override
        public void changed(ObservableValue<? extends Mitarbeiter> observable, Mitarbeiter oldValue,
                Mitarbeiter newValue) {

            onSelectedMitarbeiterChanged(newValue);
        }
    }
}
