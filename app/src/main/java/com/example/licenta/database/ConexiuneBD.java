package com.example.licenta.database;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.example.licenta.config.Config;

public class ConexiuneBD {
    private String url;
    private String driver;
    private String nume;
    private String parola;
    private Connection conexiune;

    // Instanta unica
    private static ConexiuneBD conexiuneBD = null;

    // Log unic pentru debug
    private static final String tagLog = "ClasaConexiuBD";

    // Constructir
    private ConexiuneBD() {
        // Thread Policy
        this.threadPolicy();

        // Preluare data fisier
        this.url = Config.url;
        this.driver = Config.driver;
        this.nume = Config.nume;
        this.parola = Config.parola;
        try {
            this.conexiune = createConnection(this.driver, this.url, this.nume, this.parola);
            Log.d(tagLog, "Conexiune unica baza de date");
        } catch (SQLException ex) {
            ex.printStackTrace();
            Log.e(tagLog, "Eroare conectare baza de date");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            Log.e(tagLog, "Eroare conectare baza de date");
        }
    }

    // Get instance
    public synchronized static ConexiuneBD getInstance() {
        if (conexiuneBD == null) {
            conexiuneBD = new ConexiuneBD();
        }

        return conexiuneBD;
    }


    // Getteri si setteri
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }

    public Connection getConexiune() {
        return conexiune;
    }

    public void setConexiune(Connection conexiune) {
        this.conexiune = conexiune;
    }


    // Metode
    private Connection createConnection(String driver, String url, String nume, String parola) throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        return DriverManager.getConnection(url, nume, parola);
    }

    private void threadPolicy() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }
}
