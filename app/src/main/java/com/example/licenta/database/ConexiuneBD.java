package com.example.licenta.database;

import android.os.StrictMode;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexiuneBD {
    private String url;
    private String driver;
    private String nume;
    private String parola;
    private Connection conexiune;

    private static ConexiuneBD conexiuneBD = null;

    private ConexiuneBD()  {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // Preluare data fisier
        this.url = "jdbc:oracle:thin:@192.168.0.139:1522:XE";
        this.driver = "oracle.jdbc.driver.OracleDriver";
        this.nume = "SYSTEM";
        this.parola = "ALLU1234";
        try {
            this.conexiune = createConnection(this.driver, this.url, this.nume, this.parola);
        }
        catch (SQLException ex){
            ex.printStackTrace();
            this.conexiune = null;
        }
        catch (ClassNotFoundException ex){
            ex.printStackTrace();
            this.conexiune = null;
        }
    }

    public synchronized static ConexiuneBD getInstance() {
        if (conexiuneBD == null) {
            conexiuneBD = new ConexiuneBD();
        }

        return conexiuneBD;
    }

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
}
