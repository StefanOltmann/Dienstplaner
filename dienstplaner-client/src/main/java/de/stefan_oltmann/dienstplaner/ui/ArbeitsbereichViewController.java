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
import javafx.scene.control.TextField;

public class ArbeitsbereichViewController implements Initializable {

    @FXML
    private ListView<Arbeitsbereich>       arbeitsbereicheListView;
    private ObservableList<Arbeitsbereich> arbeitsbereicheList;

    @FXML
    private TextField                      nameFeld;

    @FXML
    private Button                         neuButton;

    @FXML
    private Button                         loeschenButton;

    @FXML
    private Button                         speichernButton;

    @FXML
    private ListSelectionView<Mitarbeiter> listSelectionView;

    private ObservableList<Mitarbeiter>    verfuegbareMitarbeiterListe;
    private ObservableList<Mitarbeiter>    zugewieseneMitarbeiterListe;

    private DataAccessService              dataAccessService;

    private Arbeitsbereich                 aktuellerArbeitsbereich;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.arbeitsbereicheList = FXCollections.observableArrayList();
        this.arbeitsbereicheListView.setItems(arbeitsbereicheList);
        this.arbeitsbereicheListView.getSelectionModel().selectedItemProperty()
                .addListener(new SelectedArbeitsbereichChangeListener());

        this.verfuegbareMitarbeiterListe = FXCollections.observableArrayList();
        this.zugewieseneMitarbeiterListe = FXCollections.observableArrayList();

        this.listSelectionView.setSourceItems(verfuegbareMitarbeiterListe);
        this.listSelectionView.setTargetItems(zugewieseneMitarbeiterListe);

        ((javafx.scene.control.Label) this.listSelectionView.getSourceHeader()).setText("Verfügbare Mitarbeiter");
        ((javafx.scene.control.Label) this.listSelectionView.getTargetHeader()).setText("Zugewiesene Mitarbeiter");

        /* Initial ist das Formular disabled. */
        setEintragEditierenEnabled(false);
    }

    public void setDataAccessService(DataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
        aktualisiereArbeitsbereichListe();
    }

    private void aktualisiereArbeitsbereichListe() {

        DienstplanDatei dienstplanDatei = dataAccessService.getAktuelleDienstplanDatei();

        Set<Arbeitsbereich> alleArbeitsbereiche = dienstplanDatei.findAllArbeitsbereich();

        arbeitsbereicheList.setAll(alleArbeitsbereiche);
    }

    private void setEintragEditierenEnabled(boolean enabled) {

        /* Eingabe-Felder */
        nameFeld.setDisable(!enabled);

        listSelectionView.setDisable(!enabled);

        /* Aktions-Buttons */
        loeschenButton.setDisable(!enabled);
        speichernButton.setDisable(!enabled);
    }

    private void onSelectedArbeitsbereichChanged(Arbeitsbereich arbeitsbereich) {

        DienstplanDatei dienstplanDatei = dataAccessService.getAktuelleDienstplanDatei();

        this.aktuellerArbeitsbereich = arbeitsbereich;

        setEintragEditierenEnabled(arbeitsbereich != null);

        if (arbeitsbereich != null) {

            nameFeld.setText(arbeitsbereich.getName());

            {
                Set<Mitarbeiter> zugewieseneMitarbeiter = dienstplanDatei.findAllMitarbeiterFor(arbeitsbereich);
                Set<Mitarbeiter> verfuegbareMitarbeiter = new HashSet<>(dienstplanDatei.findAllMitarbeiter());

                verfuegbareMitarbeiter.removeAll(zugewieseneMitarbeiter);

                verfuegbareMitarbeiterListe.setAll(verfuegbareMitarbeiter);
                zugewieseneMitarbeiterListe.setAll(zugewieseneMitarbeiter);
            }

        } else {

            nameFeld.clear();

            verfuegbareMitarbeiterListe.clear();
            zugewieseneMitarbeiterListe.clear();
        }
    }

    @FXML
    private void onArbeitsbereichErstellen(
            @SuppressWarnings("unused") ActionEvent event) {

        aktuellerArbeitsbereich = new Arbeitsbereich();

        onSelectedArbeitsbereichChanged(aktuellerArbeitsbereich);

        /*
         * Den Neu-Button deaktivieren, damit klar ist, dass man jetzt auf
         * "Speichern" klicken soll.
         */
        neuButton.setDisable(true);
    }

    @FXML
    private void onArbeitsbereichSpeichern(
            @SuppressWarnings("unused") ActionEvent event) {

        try {

            DienstplanDatei dienstplanDatei = dataAccessService.getAktuelleDienstplanDatei();

            aktuellerArbeitsbereich.setName(nameFeld.getText());

            dienstplanDatei.saveArbeitsbereich(aktuellerArbeitsbereich);

            dienstplanDatei.saveArbeitsbereichMitarbeiterZuordnung(
                    aktuellerArbeitsbereich, zugewieseneMitarbeiterListe);

            aktualisiereArbeitsbereichListe();

            /* Jetzt dürfen wieder neue Einträge erzeugt werden. */
            neuButton.setDisable(false);

            dataAccessService.speicherDienstplanDatei();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onArbeitsbereichLoeschen(
            @SuppressWarnings("unused") ActionEvent event) {

        DienstplanDatei dienstplanDatei = dataAccessService.getAktuelleDienstplanDatei();

        dienstplanDatei.deleteArbeitsbereich(aktuellerArbeitsbereich);

        aktualisiereArbeitsbereichListe();

        dataAccessService.speicherDienstplanDatei();
    }

    public void onShown() {
        /* Wird aufgerufen, wenn die View angezeigt wird. */
    }

    private class SelectedArbeitsbereichChangeListener implements ChangeListener<Arbeitsbereich> {

        @Override
        public void changed(ObservableValue<? extends Arbeitsbereich> observable, Arbeitsbereich oldValue,
                Arbeitsbereich newValue) {

            onSelectedArbeitsbereichChanged(newValue);
        }
    }
}
