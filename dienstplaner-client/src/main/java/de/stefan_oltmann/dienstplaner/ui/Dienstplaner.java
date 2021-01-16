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

import java.io.IOException;

import de.stefan_oltmann.dienstplaner.dao.DataAccessService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Dienstplaner extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

        primaryStage.setTitle("Dienstplaner");

        DataAccessService dataAccessService = new DataAccessService();

        FXMLLoader mainViewLoader = new FXMLLoader();
        Parent mainView = mainViewLoader.load(getClass().getResourceAsStream("MainView.fxml"));
        MainViewController mainViewController = mainViewLoader.getController();

        FXMLLoader dienstplanViewLoader = new FXMLLoader();
        Parent dienstplanView = dienstplanViewLoader.load(getClass().getResourceAsStream("DienstplanView.fxml"));
        DienstplanViewController dienstplanViewController = dienstplanViewLoader.getController();

        FXMLLoader einstellungenViewLoader = new FXMLLoader();
        Parent einstellungenView = einstellungenViewLoader.load(getClass().getResourceAsStream("EinstellungenView.fxml"));
        EinstellungenViewController einstellungenViewController = einstellungenViewLoader.getController();

        FXMLLoader arbeitsbereichViewLoader = new FXMLLoader();
        Parent arbeitsbereichView = arbeitsbereichViewLoader.load(getClass().getResourceAsStream("ArbeitsbereichView.fxml"));
        ArbeitsbereichViewController arbeitsbereichViewController = arbeitsbereichViewLoader.getController();

        FXMLLoader mitarbeiterViewLoader = new FXMLLoader();
        Parent mitarbeiterView = mitarbeiterViewLoader.load(getClass().getResourceAsStream("MitarbeiterView.fxml"));
        MitarbeiterViewController mitarbeiterViewController = mitarbeiterViewLoader.getController();

        dienstplanViewController.setDataAccessService(dataAccessService);
        mitarbeiterViewController.setDataAccessService(dataAccessService);
        arbeitsbereichViewController.setDataAccessService(dataAccessService);

        mainViewController.setEinstellungenControllerAndView(einstellungenViewController, einstellungenView);
        mainViewController.setDienstplanControllerAndView(dienstplanViewController, dienstplanView);
        mainViewController.setArbeitsbereichControllerAndView(arbeitsbereichViewController, arbeitsbereichView);
        mainViewController.setMitarbeiterControllerAndView(mitarbeiterViewController, mitarbeiterView);

        Scene scene = new Scene(mainView);
        scene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
        primaryStage.setScene(scene);

        /*
         * Die Anwendung soll auf 1024x768 gut aussehen. Dabei muss immer noch
         * etwas für die Windows- und Titel-Leiste abgezogen werden.
         */
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);

        primaryStage.show();
    }

    public static void main(String[] args) {

        /* Google Roboto als Custom Font laden, weil sie schöner ist. */
        Font.loadFont(Dienstplaner.class.getResource("fonts/Roboto-Regular.ttf").toExternalForm(), 10);

        launch(args);
    }
}