package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TextView;
import android.widget.Toast;

import com.example.licenta.database.ConexiuneBD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    private ConexiuneBD conexiuneBD = ConexiuneBD.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Aici
        //Test 2
        //Test 3
        // Test final
        //Test final final


        TextView tv = findViewById(R.id.hello);
        ConexiuneBD conexiuneBD = ConexiuneBD.getInstance();
        Toast.makeText(getApplicationContext(), "Conectat", Toast.LENGTH_SHORT).show();

        // Test functioare bd
        try {
            Statement stmt = null;
            stmt = conexiuneBD.getConexiune().createStatement();
            StringBuffer stringBuffer = new StringBuffer();
            //ResultSet rs = stmt.executeQuery("INSERT INTO testBD VALUES(3,'Radu','Alex')");
            ResultSet rs = stmt.executeQuery("select * from testBD");
            while (rs.next()) {
                stringBuffer.append(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3) + "\n");
            }
            tv.setText(stringBuffer.toString());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        // Este null conexiunea => nu s-a putut realiza conexiunea
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
        }

    }

}