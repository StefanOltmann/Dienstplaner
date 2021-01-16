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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DienstplanDatei {

    private int                                             currentMitarbeiterId             = 1;
    private int                                             currentArbeitsbereichId          = 1;

    private Set<Mitarbeiter>                                mitarbeiterSet                   = new HashSet<>();
    private transient Set<Mitarbeiter>                      mitarbeiterSetUnmodifiable;

    private Set<Arbeitsbereich>                             arbeitsbereichSet                = new HashSet<>();
    private transient Set<Arbeitsbereich>                   arbeitsbereichSetUnmodifiable;

    private transient Map<Mitarbeiter, Set<Arbeitsbereich>> arbeitsbereicheProMitarbeiterMap = new HashMap<>();
    private transient Map<Arbeitsbereich, Set<Mitarbeiter>> mitarbeiterProArbeitsbereichMap  = new HashMap<>();

    /*
     * Mitarbeiter
     */

    private int nextMitarbeiterId() {
        return currentMitarbeiterId++;
    }

    public Set<Mitarbeiter> findAllMitarbeiter() {

        /*
         * Erst beim ersten Aufruf initalisieren, da GSon bei der Initalisierung
         * etwas Statisches reinsetzt, was sich danach nicht mehr ändert.
         */
        if (mitarbeiterSetUnmodifiable == null)
            mitarbeiterSetUnmodifiable = Collections.unmodifiableSet(mitarbeiterSet);

        return mitarbeiterSetUnmodifiable;
    }

    public void saveMitarbeiter(Mitarbeiter mitarbeiter) {

        /*
         * TODO Anhand der ID erkennen, ob der Mitarbeiter irgendwie noch
         * persistiert werden muss.
         */

        if (mitarbeiter.getId() == 0) {

            mitarbeiter.setId(nextMitarbeiterId());

            mitarbeiterSet.add(mitarbeiter);
        }
    }

    public void deleteMitarbeiter(Mitarbeiter mitarbeiter) {

        /* Ein Mitarbeiter mit ID = 0 wurde nicht gespeichert. */
        if (mitarbeiter.getId() == 0)
            return;

        mitarbeiterSet.remove(mitarbeiter);

        /*
         * Aus verbundenen Listen entfernen
         */

        arbeitsbereicheProMitarbeiterMap.remove(mitarbeiter);

        for (Set<Mitarbeiter> mitarbeiterListe : mitarbeiterProArbeitsbereichMap.values())
            mitarbeiterListe.remove(mitarbeiter);
    }

    /*
     * Arbeitsbereich
     */

    private int nextArbeitsbereichId() {
        return currentArbeitsbereichId++;
    }

    public Set<Arbeitsbereich> findAllArbeitsbereich() {

        /*
         * Erst beim ersten Aufruf initalisieren, da GSon bei der Initalisierung
         * etwas Statisches reinsetzt, was sich danach nicht mehr ändert.
         */
        if (arbeitsbereichSetUnmodifiable == null)
            arbeitsbereichSetUnmodifiable = Collections.unmodifiableSet(arbeitsbereichSet);

        return arbeitsbereichSetUnmodifiable;
    }

    public void saveArbeitsbereich(Arbeitsbereich arbeitsbereich) {

        /*
         * TODO Anhand der ID erkennen, ob der Arbeitsbereich irgendwie noch
         * persistiert werden muss.
         */

        if (arbeitsbereich.getId() == 0) {

            arbeitsbereich.setId(nextArbeitsbereichId());

            arbeitsbereichSet.add(arbeitsbereich);
        }
    }

    public void deleteArbeitsbereich(Arbeitsbereich arbeitsbereich) {

        /* Ein Mitarbeiter mit ID = 0 wurde nicht gespeichert. */
        if (arbeitsbereich.getId() == 0)
            return;

        arbeitsbereichSet.remove(arbeitsbereich);

        /*
         * Aus verbundenen Listen entfernen
         */

        mitarbeiterProArbeitsbereichMap.remove(arbeitsbereich);

        for (Set<Arbeitsbereich> arbeitsbereiche : arbeitsbereicheProMitarbeiterMap.values())
            arbeitsbereiche.remove(arbeitsbereich);
    }

    /*
     * Mitarbeiter-Arbeitsbereich-Zuordnungen
     */

    public Set<Arbeitsbereich> findAllArbeitsbereicheFor(Mitarbeiter mitarbeiter) {

        Set<Arbeitsbereich> arbeitsbereichSet = arbeitsbereicheProMitarbeiterMap.get(mitarbeiter);

        if (arbeitsbereichSet == null)
            return Collections.emptySet();

        return Collections.unmodifiableSet(arbeitsbereichSet);
    }

    public Set<Mitarbeiter> findAllMitarbeiterFor(Arbeitsbereich arbeitsbereich) {

        Set<Mitarbeiter> mitarbeiterSet = mitarbeiterProArbeitsbereichMap.get(arbeitsbereich);

        if (mitarbeiterSet == null)
            return Collections.emptySet();

        return Collections.unmodifiableSet(mitarbeiterSet);
    }

    public void saveMitarbeiterArbeitsbereichZuordnung(
            Mitarbeiter mitarbeiter, Collection<Arbeitsbereich> neueArbeitsbereiche) {

        /*
         * In beide Maps einfügen. Der Aufwand zwei Maps für beide Richtungen zu
         * führen ist bei Modifikation zwar dadurch größer, aber dafür wird
         * deutlich öfter abgefragt als modifiziert.
         */

        /*
         * Vereinigungsmenge von entfernten und neu hinzugefügten
         * Arbeitsbereichen.
         */
        Set<Arbeitsbereich> alleArbeitsbereiche = new HashSet<>();
        alleArbeitsbereiche.addAll(neueArbeitsbereiche);

        {
            Set<Arbeitsbereich> arbeitsbereicheDiesesMitarbeiters = arbeitsbereicheProMitarbeiterMap.get(mitarbeiter);

            /* Erzeugen, falls es noch NULL ist. */
            if (arbeitsbereicheDiesesMitarbeiters == null) {
                arbeitsbereicheDiesesMitarbeiters = new HashSet<>();
                arbeitsbereicheProMitarbeiterMap.put(mitarbeiter, arbeitsbereicheDiesesMitarbeiters);
            }

            alleArbeitsbereiche.addAll(arbeitsbereicheDiesesMitarbeiters);

            arbeitsbereicheDiesesMitarbeiters.clear();
            arbeitsbereicheDiesesMitarbeiters.addAll(neueArbeitsbereiche);
        }

        for (Arbeitsbereich arbeitsbereich : alleArbeitsbereiche) {

            Set<Mitarbeiter> mitarbeiterDiesesArbeitsbereichs = mitarbeiterProArbeitsbereichMap.get(arbeitsbereich);

            /* Erzeugen, falls es noch NULL ist. */
            if (mitarbeiterDiesesArbeitsbereichs == null) {
                mitarbeiterDiesesArbeitsbereichs = new HashSet<>();
                mitarbeiterProArbeitsbereichMap.put(arbeitsbereich, mitarbeiterDiesesArbeitsbereichs);
            }

            /*
             * Wenn der Arbeitsbereich in der neuen Liste nicht mehr vorhanden
             * ist, muss er entfernt werden.
             */
            boolean entfernen = !neueArbeitsbereiche.contains(arbeitsbereich);

            if (entfernen)
                mitarbeiterDiesesArbeitsbereichs.remove(mitarbeiter);
            else
                /* Wir adden auf ein Set -> contains()-Prüfung unnötig. */
                mitarbeiterDiesesArbeitsbereichs.add(mitarbeiter);
        }
    }

    public void saveArbeitsbereichMitarbeiterZuordnung(
            Arbeitsbereich arbeitsbereich, Collection<Mitarbeiter> neueMitarbeiter) {

        /*
         * In beide Maps einfügen. Der Aufwand zwei Maps für beide Richtungen zu
         * führen ist bei Modifikation zwar dadurch größer, aber dafür wird
         * deutlich öfter abgefragt als modifiziert.
         */

        /*
         * Vereinigungsmenge von entfernten und neu hinzugefügten
         * Arbeitsbereichen.
         */
        Set<Mitarbeiter> alleMitarbeiter = new HashSet<>();
        alleMitarbeiter.addAll(neueMitarbeiter);

        {
            Set<Mitarbeiter> mitarbeiterDiesesArbeitsbereichs = mitarbeiterProArbeitsbereichMap.get(arbeitsbereich);

            /* Erzeugen, falls es noch NULL ist. */
            if (mitarbeiterDiesesArbeitsbereichs == null) {
                mitarbeiterDiesesArbeitsbereichs = new HashSet<>();
                mitarbeiterProArbeitsbereichMap.put(arbeitsbereich, mitarbeiterDiesesArbeitsbereichs);
            }

            alleMitarbeiter.addAll(mitarbeiterDiesesArbeitsbereichs);

            mitarbeiterDiesesArbeitsbereichs.clear();
            mitarbeiterDiesesArbeitsbereichs.addAll(neueMitarbeiter);
        }

        for (Mitarbeiter mitarbeiter : alleMitarbeiter) {

            Set<Arbeitsbereich> arbeitsbereicheDiesesMitarbeiters = arbeitsbereicheProMitarbeiterMap.get(mitarbeiter);

            /* Erzeugen, falls es noch NULL ist. */
            if (arbeitsbereicheDiesesMitarbeiters == null) {
                arbeitsbereicheDiesesMitarbeiters = new HashSet<>();
                arbeitsbereicheProMitarbeiterMap.put(mitarbeiter, arbeitsbereicheDiesesMitarbeiters);
            }

            /*
             * Wenn der Mitarbeiter in der neuen Liste nicht mehr vorhanden ist,
             * muss er entfernt werden.
             */
            boolean entfernen = !neueMitarbeiter.contains(mitarbeiter);

            if (entfernen)
                arbeitsbereicheDiesesMitarbeiters.remove(arbeitsbereich);
            else
                /* Wir adden auf ein Set -> contains()-Prüfung unnötig. */
                arbeitsbereicheDiesesMitarbeiters.add(arbeitsbereich);
        }
    }
}
