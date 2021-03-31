package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.clase.user.User;
import com.example.licenta.database.service.UserService;
import com.google.android.material.textfield.TextInputEditText;

public class LogInActivity extends AppCompatActivity {

    private TextInputEditText tietUsername;
    private TextInputEditText tietPassword;
    private Button btnLogin;
    private TextView tvSignup;

    private UserService userService = new UserService();

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

        // Verificare username si password
        tietUsername.addTextChangedListener(watcherVerificareUsername());
        tietPassword.addTextChangedListener(watcherVerificarePassword());
    }


    // Functii
    // Functie initializare componente
    private void initComponents() {
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
                if (validareFinala()) {
                    String username = tietUsername.getText().toString().replace(" ", "");
                    String password = tietPassword.getText().toString().replace(" ", "");

                    userService.getUserByUsernameAndPassword(username, password, callbackGetUserByUsernameAndPassword());
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_crietrii_autentificare_neindeplinite),
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    // Callback pt get user by username si password
    private Callback<User> callbackGetUserByUsernameAndPassword() {
        return new Callback<User>() {
            @Override
            public void runResultOnUiThread(User result) {
                if (result != null) {
                    // Initializare instanta unica de current user
                    CurrentUser.initInstance(result);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_autentificare_esuata_logIn),
                            Toast.LENGTH_SHORT).show();
                }
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


    // Validare finala
    private boolean validareFinala() {
        if (tietUsername.getError() != null || tietPassword.getError() != null) {
            return false;
        }
        if (tietUsername.getText().toString().replace(" ", "").length() == 0 ||
                tietPassword.getText().toString().replace(" ", "").length() == 0) {
            return false;
        }

        return true;
    }





    // Functie verificare username si password
    private TextWatcher watcherVerificareUsername() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tietUsername.getText().toString().replace(" ", "").length() < 6) {
                    tietUsername.setError(getString(R.string.error_invalid_username));
                } else if (tietUsername.getText().toString().replace(" ", "").length() > 15) {
                    tietUsername.setError(getString(R.string.error_username_over_max_char));
                }
            }
        };
    }

    private TextWatcher watcherVerificarePassword() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tietPassword.getText().toString().replace(" ", "").length() < 6) {
                    tietPassword.setError(getString(R.string.error_invalid_password));
                } else if (tietPassword.getText().toString().replace(" ", "").length() > 20) {
                    tietPassword.setError(getString(R.string.error_password_over_max_char));
                }
            }
        };
    }

}