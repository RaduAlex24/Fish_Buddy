package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText tietUsername;
    private TextInputEditText tietSurname;
    private TextInputEditText tietName;
    private TextInputEditText tietEmail;
    private TextInputEditText tietPassword;
    private TextInputEditText tietConfirmPassword;
    private Button btnSignup;

    private boolean existaErori = false;

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
    }


    // Functie click pentru butonul signu up
    private View.OnClickListener onClickSignUp() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignUpActivity.this, "Merge", Toast.LENGTH_SHORT).show();
            }
        };
    }


    // Functii watchers
    // Parola
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
                if (tietPassword.getText().toString().replace(" ", "").length() < 6) {
                    tietPassword.setError("Er tietPassword <");
                    existaErori = true;
                } else if (tietPassword.getText().toString().replace(" ", "").length() > 20) {
                    tietPassword.setError("Er tietPassword >");
                    existaErori = true;
                } else if (tietConfirmPassword.getText().toString().replace(" ", "").length() != 0) {
                    if (!tietPassword.getText().toString().replace(" ", "")
                            .equals(tietConfirmPassword.getText().toString().replace(" ", ""))) {
                        tietPassword.setError("Er nu coinied");
                        tietConfirmPassword.setError("Er nu coinied");
                        existaErori = true;
                    } else {
                        tietPassword.setError(null);
                        tietConfirmPassword.setError(null);
                        existaErori = false;
                    }
                } else {
                    existaErori = false;
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
                if (tietConfirmPassword.getText().toString().replace(" ", "").length() < 6) {
                    tietConfirmPassword.setError("Er tietEmail <");
                    existaErori = true;
                } else if (tietConfirmPassword.getText().toString().replace(" ", "").length() > 20) {
                    tietConfirmPassword.setError("Er tietEmail >");
                    existaErori = true;
                } else if (tietPassword.getText().toString().replace(" ", "").length() != 0) {
                    if (!tietPassword.getText().toString().replace(" ", "")
                            .equals(tietConfirmPassword.getText().toString().replace(" ", ""))) {
                        tietPassword.setError("Er nu coinied");
                        tietConfirmPassword.setError("Er nu coinied");
                        existaErori = true;
                    } else {
                        tietPassword.setError(null);
                        tietConfirmPassword.setError(null);
                        existaErori = false;
                    }
                } else {
                    existaErori = false;
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
                if (tietEmail.getText().toString().replace(" ", "").length() < 5) {
                    tietEmail.setError("Er tietEmail <");
                } else if (tietEmail.getText().toString().replace(" ", "").length() > 40) {
                    tietEmail.setError("Er tietEmail >");
                } else if (!(tietEmail.getText().toString().replace(" ", "").contains("@")
                        && tietEmail.getText().toString().replace(" ", "").contains("."))) {
                    tietEmail.setError("Er tietEmail format");
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
                if (tietName.getText().toString().replace(" ", "").length() < 2) {
                    tietName.setError("Er name <");
                } else if (tietName.getText().toString().replace(" ", "").length() > 40) {
                    tietName.setError("Er name >");
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
                if (tietSurname.getText().toString().replace(" ", "").length() < 2) {
                    tietSurname.setError("Er surname <");
                } else if (tietSurname.getText().toString().replace(" ", "").length() > 20) {
                    tietSurname.setError("Er surname >");
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
                if (tietUsername.getText().toString().replace(" ", "").length() < 6) {
                    tietUsername.setError("Er username <");
                } else if (tietUsername.getText().toString().replace(" ", "").length() > 15) {
                    tietUsername.setError("Er username >");
                }
            }
        };
    }

}