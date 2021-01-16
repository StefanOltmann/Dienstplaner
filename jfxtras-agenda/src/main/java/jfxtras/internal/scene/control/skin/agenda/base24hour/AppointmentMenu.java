/**
 * AppointmentMenu.java
 *
 * Copyright (c) 2011-2016, JFXtras
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the organization nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jfxtras.internal.scene.control.skin.agenda.base24hour;

import java.time.LocalDateTime;

import de.stefan_oltmann.dienstplaner.model.Arbeitsbereich;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.util.Callback;
import jfxtras.scene.control.ImageViewButton;
import jfxtras.scene.control.LocalDateTimeTextField;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import jfxtras.util.NodeUtil;

//TODO: internationalize the labels and tooltips
class AppointmentMenu extends Rectangle {

    /**
     * 
     * @param pane
     * @param appointment
     * @param layoutHelp
     */
    AppointmentMenu(Pane pane, Appointment appointment, LayoutHelp layoutHelp) {
        this.pane = pane;
        this.appointment = appointment;
        this.layoutHelp = layoutHelp;

        // layout
        setX(NodeUtil.snapXY(layoutHelp.paddingProperty.get()));
        setY(NodeUtil.snapXY(layoutHelp.paddingProperty.get()));
        setWidth(6);
        setHeight(3);

        // style
        getStyleClass().add("MenuIcon");

        // mouse
        layoutHelp.setupMouseOverAsBusy(this);
        setupMouseClick();
    }

    final Pane        pane;
    final Appointment appointment;
    final LayoutHelp  layoutHelp;

    /**
     * 
     */
    private void setupMouseClick() {
        setOnMousePressed((mouseEvent) -> {
            mouseEvent.consume();
        });
        setOnMouseReleased((mouseEvent) -> {
            mouseEvent.consume();
        });
        setOnMouseClicked((mouseEvent) -> {
            mouseEvent.consume();
            showMenu(mouseEvent);
        });
    }

    /**
     * 
     * @param mouseEvent
     */
    void showMenu(MouseEvent mouseEvent) {
        // has the client done his own popup?
        Callback<Appointment, Void> lEditCallback = layoutHelp.skinnable.getEditAppointmentCallback();
        if (lEditCallback != null) {
            lEditCallback.call(appointment);
            return;
        }

        // only if not already showing
        if (popup != null && popup.isShowing()) {
            return;
        }

        // create popup
        popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);
        popup.setOnHidden((windowEvent) -> {
            layoutHelp.skin.setupAppointments();
        });

        // popup contents
        BorderPane lBorderPane = new BorderPane() {
            // As of 1.8.0_40 CSS files are added in the scope of a control, the
            // popup does not fall under the control, so the stylesheet must be
            // reapplied
            // When JFxtras is based on 1.8.0_40+: @Override
            @Override
            public String getUserAgentStylesheet() {
                return layoutHelp.skinnable.getUserAgentStylesheet();
            }
        };
        lBorderPane.getStyleClass().add(layoutHelp.skinnable.getClass().getSimpleName() + "Popup");
        popup.getContent().add(lBorderPane);

        // close icon
        lBorderPane.setRight(createCloseIcon());

        // initial layout
        VBox lVBox = new VBox(layoutHelp.paddingProperty.get());
        lBorderPane.setCenter(lVBox);

        // start and end
        lVBox.getChildren().add(new Text("Zeit:"));
        lVBox.getChildren().add(createStartTextField());
        lVBox.getChildren().add(createEndTextField());

        // summary
        lVBox.getChildren().add(new Text("Arbeitsbereich:"));
        lVBox.getChildren().add(createArbeitsbereichTextField());

        // location
        lVBox.getChildren().add(new Text("Mitarbeiter:"));
        lVBox.getChildren().add(createLocationTextField());

        // actions
        lVBox.getChildren().add(createActions());

        // show it just below the menu icon
        popup.show(pane, NodeUtil.screenX(pane), NodeUtil.screenY(pane));
    }

    private Popup popup;

    /**
     * @return
     */
    private ImageViewButton createCloseIcon() {
        closeIconImageView = new ImageViewButton();
        closeIconImageView.getStyleClass().add("close-icon");
        closeIconImageView.setPickOnBounds(true);
        closeIconImageView.setOnMouseClicked((mouseEvent2) -> {
            popup.hide();
        });
        return closeIconImageView;
    }

    private ImageViewButton closeIconImageView = null;

    /**
     * 
     * @return
     */
    private LocalDateTimeTextField createStartTextField() {
        startTextField = new LocalDateTimeTextField();
        startTextField.setLocale(layoutHelp.skinnable.getLocale());
        startTextField.setLocalDateTime(appointment.getStartLocalDateTime());

        // event handling
        startTextField.localDateTimeProperty().addListener((observable, oldValue, newValue) -> {

            // remember
            LocalDateTime lOldStart = appointment.getStartLocalDateTime();

            // set
            appointment.setStartLocalDateTime(newValue);

            // update end date
            if (appointment.getEndLocalDateTime() != null) {

                // enddate = start + duration
                long lDurationInNano = appointment.getEndLocalDateTime().getNano() - lOldStart.getNano();
                LocalDateTime lEndLocalDateTime = appointment.getStartLocalDateTime().plusNanos(lDurationInNano);
                appointment.setEndLocalDateTime(lEndLocalDateTime);

                // update field
                endTextField.setLocalDateTime(appointment.getEndLocalDateTime());
            }

            // refresh is done upon popup close
            layoutHelp.callAppointmentChangedCallback(appointment);
        });

        return startTextField;
    }

    private LocalDateTimeTextField startTextField = null;

    /**
     * 
     * @return
     */
    private LocalDateTimeTextField createEndTextField() {
        endTextField = new LocalDateTimeTextField();
        endTextField.setLocale(layoutHelp.skinnable.getLocale());
        endTextField.setLocalDateTime(appointment.getEndLocalDateTime());
        endTextField.setVisible(appointment.getEndLocalDateTime() != null);

        endTextField.localDateTimeProperty().addListener((observable, oldValue, newValue) -> {
            appointment.setEndLocalDateTime(newValue);
            // refresh is done upon popup close
            layoutHelp.callAppointmentChangedCallback(appointment);
        });

        return endTextField;
    }

    private LocalDateTimeTextField endTextField = null;

    /**
     * 
     * @return
     */
    private ComboBox<Arbeitsbereich> createArbeitsbereichTextField() {
        summaryTextField = new ComboBox<>();

        summaryTextField.getItems().add(new Arbeitsbereich("test 1"));
        summaryTextField.getItems().add(new Arbeitsbereich("test 2"));
        summaryTextField.getItems().add(new Arbeitsbereich("test 3"));

        summaryTextField.itemsProperty().addListener((observable, oldValue, newValue) -> {
            appointment.setSummary(summaryTextField.getSelectionModel().getSelectedItem().getName());
            // refresh is done upon popup close
            layoutHelp.callAppointmentChangedCallback(appointment);
        });
        return summaryTextField;
    }

    private ComboBox<Arbeitsbereich> summaryTextField = null;

    /**
     * 
     * @return
     */
    private TextField createLocationTextField() {
        locationTextField = new TextField();
        locationTextField.setText(appointment.getLocation() == null ? "" : appointment.getLocation());
        locationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            appointment.setLocation(newValue);
            // refresh is done upon popup close
            layoutHelp.callAppointmentChangedCallback(appointment);
        });
        return locationTextField;
    }

    private TextField locationTextField = null;

    /**
     * 
     * @return
     */
    private HBox createActions() {
        HBox lHBox = new HBox();

        // delete
        {
            deleteImageViewButton = new Button("Löschen");

            deleteImageViewButton.getStyleClass().add("loeschenButton");

            deleteImageViewButton.setOnMouseClicked((mouseEvent) -> {
                popup.hide();
                layoutHelp.skinnable.appointments().remove(appointment);
                // refresh is done via the collection events
            });
            lHBox.getChildren().add(deleteImageViewButton);
        }

        return lHBox;
    }

    private Button deleteImageViewButton = null;
}