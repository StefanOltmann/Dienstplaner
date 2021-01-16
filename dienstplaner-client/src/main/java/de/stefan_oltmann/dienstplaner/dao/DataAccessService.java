package de.stefan_oltmann.dienstplaner.dao;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import com.google.gson.Gson;

import de.stefan_oltmann.dienstplaner.model.DienstplanDatei;

public class DataAccessService {

    private Gson            gson;

    private File            dienstplanDateiFile;

    private DienstplanDatei dienstplanDatei;

    public DataAccessService() {

        this.gson = new Gson();
        this.dienstplanDateiFile = new File("dienstplan.json");

        if (dienstplanDateiFile.exists()) {

            try (FileReader reader = new FileReader(dienstplanDateiFile)) {

                this.dienstplanDatei = gson.fromJson(reader, DienstplanDatei.class);

            } catch (Throwable e) {
                e.printStackTrace();
                this.dienstplanDatei = new DienstplanDatei();
            }

        } else {

            this.dienstplanDatei = new DienstplanDatei();
        }
    }

    public DienstplanDatei getAktuelleDienstplanDatei() {
        return dienstplanDatei;
    }

    public void speicherDienstplanDatei() {

        try {

            String json = gson.toJson(dienstplanDatei);

            PrintWriter printWriter = new PrintWriter(dienstplanDateiFile);
            printWriter.print(json);
            printWriter.flush();
            printWriter.close();

            System.out.println("JSON erfolgreich gespeichert in " + dienstplanDateiFile);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
