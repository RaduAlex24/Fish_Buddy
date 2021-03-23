package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class LogInActivity extends AppCompatActivity {

    private TextInputEditText tietUsername;
    private TextInputEditText tietPassword;
    private Button btnLogin;
    private TextView tvSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Initializare componente
        initComponents();

        // Atasare functie buton login
        btnLogin.setOnClickListener(onClickLogIn());

        // Atasare functie textview signup
        tvSignup.setOnClickListener(onClickSignUp());
    }



    // Functii
    private void initComponents(){
        tietUsername = findViewById(R.id.tiet_username_login);
        tietPassword = findViewById(R.id.tiet_password_login);
        btnLogin = findViewById(R.id.btn_login);
        tvSignup = findViewById(R.id.tv_signup_login);
    }


    // Functie buton login
    private View.OnClickListener onClickLogIn() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        };
    }


    // Functie textview sigup
    private View.OnClickListener onClickSignUp() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        };
    }

}