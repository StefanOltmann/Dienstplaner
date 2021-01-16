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
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class MainViewController implements Initializable {

    private static final String          STYLE_BACKGROUND_LIGHTGREEN = "-fx-background-color: lightgreen;";

    private EinstellungenViewController  einstellungenViewController;
    private Parent                       einstellungenView;

    private DienstplanViewController     dienstplanViewController;
    private Parent                       dienstplanView;

    private MitarbeiterViewController    mitarbeiterViewController;
    private Parent                       mitarbeiterView;

    private ArbeitsbereichViewController arbeitsbereichViewController;
    private Parent                       arbeitsbereichView;

    @FXML
    private BorderPane                   mainView;

    @FXML
    private Button                       showEinstellungenButton;

    @FXML
    private Button                       showDienstplanButton;

    @FXML
    private Button                       showArbeitsbereichButton;

    @FXML
    private Button                       showMitarbeiterButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /* Keine Aktion notwendig. */
    }

    public void setEinstellungenControllerAndView(EinstellungenViewController einstellungenViewController, Parent einstellungenView) {
        this.einstellungenViewController = einstellungenViewController;
        this.einstellungenView = einstellungenView;
    }

    public void setDienstplanControllerAndView(DienstplanViewController dienstplanViewController, Parent dienstplanView) {
        this.dienstplanViewController = dienstplanViewController;
        this.dienstplanView = dienstplanView;
    }

    public void setArbeitsbereichControllerAndView(ArbeitsbereichViewController arbeitsbereichViewController, Parent arbeitsbereichView) {
        this.arbeitsbereichViewController = arbeitsbereichViewController;
        this.arbeitsbereichView = arbeitsbereichView;
    }

    public void setMitarbeiterControllerAndView(MitarbeiterViewController mitarbeiterViewController, Parent mitarbeiterView) {
        this.mitarbeiterViewController = mitarbeiterViewController;
        this.mitarbeiterView = mitarbeiterView;
    }

    @FXML
    private void onEinstellungenButtonClicked(
            @SuppressWarnings("unused") ActionEvent event) {

        /* Kurzzeitig den Style überschreiben für diesen Button. */
        showEinstellungenButton.setStyle(STYLE_BACKGROUND_LIGHTGREEN);
        showDienstplanButton.setStyle(null);
        showArbeitsbereichButton.setStyle(null);
        showMitarbeiterButton.setStyle(null);

        mainView.setCenter(einstellungenView);

        einstellungenViewController.onShown();
    }

    @FXML
    private void onDienstplanButtonClicked(
            @SuppressWarnings("unused") ActionEvent event) {

        /* Kurzzeitig den Style überschreiben für diesen Button. */
        showEinstellungenButton.setStyle(null);
        showDienstplanButton.setStyle(STYLE_BACKGROUND_LIGHTGREEN);
        showArbeitsbereichButton.setStyle(null);
        showMitarbeiterButton.setStyle(null);

        mainView.setCenter(dienstplanView);

        dienstplanViewController.onShown();
    }

    @FXML
    private void onArbeitsbereichButtonClicked(
            @SuppressWarnings("unused") ActionEvent event) {

        /* Kurzzeitig den Style überschreiben für diesen Button. */
        showEinstellungenButton.setStyle(null);
        showDienstplanButton.setStyle(null);
        showArbeitsbereichButton.setStyle(STYLE_BACKGROUND_LIGHTGREEN);
        showMitarbeiterButton.setStyle(null);

        mainView.setCenter(arbeitsbereichView);

        arbeitsbereichViewController.onShown();
    }

    @FXML
    private void onMitarbeiterButtonClicked(
            @SuppressWarnings("unused") ActionEvent event) {

        /* Kurzzeitig den Style überschreiben für diesen Button. */
        showEinstellungenButton.setStyle(null);
        showDienstplanButton.setStyle(null);
        showArbeitsbereichButton.setStyle(null);
        showMitarbeiterButton.setStyle(STYLE_BACKGROUND_LIGHTGREEN);

        mainView.setCenter(mitarbeiterView);

        mitarbeiterViewController.onShown();
    }
}
