package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.licenta.asyncTask.Callback;
import com.example.licenta.database.service.UserService;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {

    public static final String PASSWORD_KEY = "PASSWORD_KEY";
    public static final String USERNAME_KEY = "USERNAME_KEY";
    private TextInputEditText tietUsername;
    private TextInputEditText tietSurname;
    private TextInputEditText tietName;
    private TextInputEditText tietEmail;
    private TextInputEditText tietPassword;
    private TextInputEditText tietConfirmPassword;
    private Button btnSignup;

    private UserService userService = new UserService();
    private Intent intent;

    private static final String tagLog = "ActivitateSignUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initializare componente
        initComponents();

        // Atasare functie butonului sign up
        btnSignup.setOnClickListener(onClickSignUp());

        // Functii validare date
        tietUsername.addTextChangedListener(wathcerVerificareUsername());
        tietSurname.addTextChangedListener(watcherVerificareNume());
        tietName.addTextChangedListener(wathcerVerificarePrenume());
        tietEmail.addTextChangedListener(watcherVerificareEmail());
        tietPassword.addTextChangedListener(watcherVerificareParola());
        tietConfirmPassword.addTextChangedListener(watcherVerificareConfirmareParola());

    }


    // Functii
    // Initializare componente
    private void initComponents() {
        tietUsername = findViewById(R.id.tiet_username_signup);
        tietSurname = findViewById(R.id.tiet_surname_signup);
        tietName = findViewById(R.id.tiet_name_signup);
        tietEmail = findViewById(R.id.tiet_email_signup);
        tietPassword = findViewById(R.id.tiet_password_signup);
        tietConfirmPassword = findViewById(R.id.tiet_confirm_password_signup);
        btnSignup = findViewById(R.id.btn_signup);

        intent = getIntent();
    }


    // Functie click pentru butonul signu up
    private View.OnClickListener onClickSignUp() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validareFinala()) {
                    // Inserare in BD
                    String username = extragereStringDinTiet(tietUsername);
                    String password = extragereStringDinTiet(tietPassword);
                    String email = extragereStringDinTiet(tietEmail);
                    String surname = extragereStringDinTiet(tietSurname);
                    String name = extragereStringDinTiet(tietName);

                    userService.insertNewUser(username, password, email, surname, name,
                            callbackInsertUser(username, password));

                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_criterii_neindeplinite_signUp),
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    // Callback insert user
    private Callback<Integer> callbackInsertUser(String username, String password) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result != 1) {
                    Log.d(tagLog, getString(R.string.log_eroare_sign_up));
                } else {
                    intent.putExtra(USERNAME_KEY, username);
                    intent.putExtra(PASSWORD_KEY, password);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        };
    }


    // Validare finala
    private boolean validareFinala() {
        if (!validarePartiala(tietUsername)) {
            return false;
        }
        if (!validarePartiala(tietSurname)) {
            return false;
        }
        if (!validarePartiala(tietName)) {
            return false;
        }
        if (!validarePartiala(tietEmail)) {
            return false;
        }
        if (!validarePartiala(tietPassword)) {
            return false;
        }
        if (!validarePartiala(tietConfirmPassword)) {
            return false;
        }

        return true;
    }

    // Validare partiala
    private boolean validarePartiala(TextInputEditText tiet) {
        if (tiet.getText().toString().replace(" ", "").length() == 0 ||
                tiet.getError() != null) {
            return false;
        }

        return true;
    }


    // Extragere text din tiet
    private String extragereStringDinTiet(TextInputEditText tiet) {
        return tiet.getText().toString().replace(" ", "");
    }


    // Functii watchers
    // Parola
    String stringTietConfirmaPassword;
    String stringTietPassword;

    private TextWatcher watcherVerificareParola() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                stringTietConfirmaPassword = tietConfirmPassword.getText().toString().replace(" ", "");
                stringTietPassword = tietPassword.getText().toString().replace(" ", "");

                if (stringTietPassword.length() < 6) {
                    tietPassword.setError(getString(R.string.error_invalid_password));
                } else if (stringTietPassword.length() > 20) {
                    tietPassword.setError(getString(R.string.error_password_over_max_char));

                } else if (tietConfirmPassword.length() != 0) {
                    if (!stringTietPassword.equals(stringTietConfirmaPassword)) {
                        tietPassword.setError(getString(R.string.error_parole_nu_coincid));
                        tietConfirmPassword.setError(getString(R.string.error_parole_nu_coincid));
                    } else {
                        tietPassword.setError(null);
                        tietConfirmPassword.setError(null);
                    }
                }
            }
        };
    }

    // Confirmare parola
    private TextWatcher watcherVerificareConfirmareParola() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                stringTietConfirmaPassword = tietConfirmPassword.getText().toString().replace(" ", "");
                stringTietPassword = tietPassword.getText().toString().replace(" ", "");

                if (stringTietConfirmaPassword.length() < 6) {
                    tietConfirmPassword.setError(getString(R.string.error_invalid_password));
                } else if (stringTietConfirmaPassword.length() > 20) {
                    tietConfirmPassword.setError(getString(R.string.error_password_over_max_char));

                } else if (tietPassword.length() != 0) {
                    if (!stringTietPassword.equals(stringTietConfirmaPassword)) {
                        tietPassword.setError(getString(R.string.error_parole_nu_coincid));
                        tietConfirmPassword.setError(getString(R.string.error_parole_nu_coincid));
                    } else {
                        tietPassword.setError(null);
                        tietConfirmPassword.setError(null);
                    }
                }
            }
        };
    }

    // Email
    private TextWatcher watcherVerificareEmail() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String stringTietEmail = tietEmail.getText().toString().replace(" ", "");

                if (stringTietEmail.length() < 5) {
                    tietEmail.setError(getString(R.string.error_email_underMinChar));
                } else if (stringTietEmail.length() > 40) {
                    tietEmail.setError(getString(R.string.email_excedesMaxChar));
                } else if (!(stringTietEmail.contains("@") && stringTietEmail.contains("."))) {
                    tietEmail.setError(getString(R.string.error_email_format));
                } else {
                    userService.getCountByEmail(stringTietEmail, callbackGetCountByEmail());
                }
            }
        };
    }

    // Callback pt get count by email
    private Callback<Integer> callbackGetCountByEmail() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result != 0) {
                    tietEmail.setError(getString(R.string.error_email_dejaInregistrat));
                }
            }
        };
    }

    // Prenume
    private TextWatcher wathcerVerificarePrenume() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String stringTietName = tietName.getText().toString().replace(" ", "");

                if (stringTietName.length() < 2) {
                    tietName.setError(getString(R.string.error_nume_subMinChar));
                } else if (stringTietName.length() > 40) {
                    tietName.setError(getString(R.string.error_name_pesteMaxChar));
                }
            }
        };
    }

    // Nume
    private TextWatcher watcherVerificareNume() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String stringTietSurname = tietSurname.getText().toString().replace(" ", "");

                if (stringTietSurname.length() < 2) {
                    tietSurname.setError(getString(R.string.error_surname_minChar));
                } else if (stringTietSurname.length() > 20) {
                    tietSurname.setError(getString(R.string.error_surname_maxChar));
                }
            }
        };
    }

    // Username
    private TextWatcher wathcerVerificareUsername() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String stringTietUsername = tietUsername.getText().toString().replace(" ", "");

                if (stringTietUsername.length() < 6) {
                    tietUsername.setError(getString(R.string.error_invalid_username));
                } else if (stringTietUsername.length() > 15) {
                    tietUsername.setError(getString(R.string.error_username_over_max_char));
                } else {
                    userService.getCountByUsername(stringTietUsername, callbackCountByUsername());
                }
            }
        };
    }

    // Callback count by username
    private Callback<Integer> callbackCountByUsername() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result != 0) {
                    tietUsername.setError(getString(R.string.error_username_existentDeja));
                }
            }
        };
    }

}