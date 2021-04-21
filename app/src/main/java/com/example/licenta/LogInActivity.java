package com.example.licenta;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.clase.user.User;
import com.example.licenta.database.service.UserService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LogInActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_SIGN_UP = 200;
    public static final String USERNAME_SP = "USERNAME_SP";
    public static final String PASSWORD_SP = "PASSWORD_SP";
    public static final String REMEMBER_CHECKED = "REMEMBER_CHECKED";
    private TextInputEditText tietUsername;
    private TextInputEditText tietPassword;
    private TextInputLayout tilPassword;
    private TextInputLayout tilUsername;
    private Button btnLogin;
    private TextView tvSignup;
    private CheckBox checkBoxRemember;

    private SharedPreferences preferences;
    public static final String SHARED_PREF_FILE_NAME = "LicentaPesteSharedPreferences";

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

        // Preluare shared preferences
        getLogInInfoFromSharedPreference();

        // Verificare username si password
        tietUsername.addTextChangedListener(watcherVerificareUsername());
        tietPassword.addTextChangedListener(watcherVerificarePassword());

        // Adaugare functie pentru schimbare remember me
        checkBoxRemember.setOnCheckedChangeListener(onCheckedChangeRememberMeListener());
    }


    // Functii
    // Functie initializare componente
    private void initComponents() {
        tietUsername = findViewById(R.id.tiet_username_login);
        tietPassword = findViewById(R.id.tiet_password_login);
        tilPassword=findViewById(R.id.til_password_login);
        tilUsername=findViewById(R.id.til_username_login);
        btnLogin = findViewById(R.id.btn_login);
        tvSignup = findViewById(R.id.tv_signup_login);
        checkBoxRemember = findViewById(R.id.checkbox_rememberMe_login);

        // Initializare shared preferences
        preferences = getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE);
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

                    // Verificare check box remember me
                    if(checkBoxRemember.isChecked()){
                        saveLogInInfoToSharedPreference();
                    }

                    // Deschidere noua activitate
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
                startActivityForResult(intent, REQUEST_CODE_SIGN_UP);
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


    // Checked listener pt remember me
    private CompoundButton.OnCheckedChangeListener onCheckedChangeRememberMeListener() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Scriere in SP
                    saveLogInInfoToSharedPreference();
                } else {
                    // Stergere din SP
                    deleteLogInInfoToSharedPreference();
                }
            }
        };
    }

    // Scriere date log in in SP
    private void saveLogInInfoToSharedPreference() {
        String username = tietUsername.getText().toString().replace(" ", "");
        String password = tietPassword.getText().toString().replace(" ", "");

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USERNAME_SP, username);
        editor.putString(PASSWORD_SP, password);
        editor.putBoolean(REMEMBER_CHECKED, true);
        editor.apply();
    }

    // Stergere date log in din SP
    private void deleteLogInInfoToSharedPreference() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USERNAME_SP, "");
        editor.putString(PASSWORD_SP, "");
        editor.putBoolean(REMEMBER_CHECKED, false);
        editor.apply();
    }

    // Incarcare date log in din SP
    private void getLogInInfoFromSharedPreference() {
        String username = preferences.getString(USERNAME_SP, "");
        String password = preferences.getString(PASSWORD_SP, "");
        boolean rememberChecked = preferences.getBoolean(REMEMBER_CHECKED, false);

        tietUsername.setText(username);
        tietPassword.setText(password);
        if (rememberChecked) {
            checkBoxRemember.setChecked(true);
        }
    }


    // On activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Creare cont nou
        if (requestCode == REQUEST_CODE_SIGN_UP && resultCode == RESULT_OK && data != null) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_cont_creat_succes),
                    Toast.LENGTH_LONG).show();

            String username = (String) data.getSerializableExtra(SignUpActivity.USERNAME_KEY);
            String password = (String) data.getSerializableExtra(SignUpActivity.PASSWORD_KEY);

            tietUsername.setText(username);
            tietPassword.setText(password);
        }
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
                String tietUsernameText = tietUsername.getText().toString().replace(" ", "");

                if (tietUsernameText.length() < 6) {
                    tilUsername.setError("Numele de utilizator trebuie sa aiba cel putin 6 caractere");
                } else if (tietUsernameText.length() > 15) {
                    tilUsername.setError("Numele de utilizator trebuie sa aiba maximum 15 caractere");
                }
                else{
                    tilUsername.setError(null);
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
                String tietPasswordText = tietPassword.getText().toString().replace(" ", "");

                if (tietPasswordText.length() < 6) {
                    tilPassword.setError("Parola trebuie sa aiba minim 6 caractere");
                } else if (tietPasswordText.length() > 20) {
                    tilPassword.setError("Parola trebuie sa aiba maximum 20 caractere");
                }
                else{
                    tilPassword.setError(null);
                }
            }
        };
    }

}