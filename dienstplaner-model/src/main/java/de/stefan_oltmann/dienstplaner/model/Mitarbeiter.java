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
package de.stefan_oltmann.dienstplaner.model; //NOSONAR

public class Mitarbeiter {

    public static int DEFAULT_STUNDEN_PRO_WOCHE = 40;

    /** ID für die Persistenz. */
    private int       id;

    private String    vorname;
    private String    nachname;
    private String    emailAdresse;
    private String    telefonNummer;
    private String    personalNummer;

    private int       stundenProWoche           = DEFAULT_STUNDEN_PRO_WOCHE;

    public Mitarbeiter() {
        /* Standard-Konstruktor für Serialisierung */
    }

    public Mitarbeiter(String vorname, String nachname, String personalNummer) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.personalNummer = personalNummer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getEmailAdresse() {
        return emailAdresse;
    }

    public void setEmailAdresse(String emailAdresse) {
        this.emailAdresse = emailAdresse;
    }

    public String getTelefonNummer() {
        return telefonNummer;
    }

    public void setTelefonNummer(String telefonNummer) {
        this.telefonNummer = telefonNummer;
    }

    public String getPersonalNummer() {
        return personalNummer;
    }

    public void setPersonalNummer(String personalNummer) {
        this.personalNummer = personalNummer;
    }

    public int getStundenProWoche() {
        return stundenProWoche;
    }

    public void setStundenProWoche(int stundenProWoche) {
        this.stundenProWoche = stundenProWoche;
    }

    @Override
    public String toString() {
        // TODO FIXME Für die Liste, später dafür CellRenderer einsetzen
        return (vorname != null ? vorname : "<Vorname>") + " " + (nachname != null ? nachname : "<Nachname>");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Mitarbeiter other = (Mitarbeiter) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
