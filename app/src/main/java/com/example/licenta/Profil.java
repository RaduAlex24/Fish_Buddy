package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.licenta.asyncTask.Callback;
import com.example.licenta.database.service.UserService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Profil extends AppCompatActivity {
    private TextInputEditText tietUsername;
    private TextInputLayout tilUsername;
    private TextInputLayout tilEmail;
    private TextInputEditText tietEmail;
    private Button buttonChangePass;
    private Button saveChanges;
    private UserService userService = new UserService();
    private String m_Text = "";
    private String text="Introduceti parola";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        initcomponents();
        tietUsername.addTextChangedListener(watcherVerificareUsername());
        tietEmail.addTextChangedListener(watcherVerificareEmail());
        buttonChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  openDialog1(v);
                AlertDialog.Builder builder = new AlertDialog.Builder(Profil.this);
                builder.setTitle("Introduceti parola noua");
// Set up the input
                final EditText input = new EditText(Profil.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);
                builder.setPositiveButton("Salvati", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                    }
                });
                builder.setNegativeButton("Anulati", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    private void initcomponents() {
        tietUsername = findViewById(R.id.tiet_profil_username);
        tietEmail = findViewById(R.id.tiet_profil_email);
        buttonChangePass = findViewById(R.id.button_profil_parola);
        saveChanges = findViewById(R.id.button_profil_save);
        tilUsername = findViewById(R.id.til_profil_username);
        tilEmail = findViewById(R.id.til_profil_email);
    }

    private boolean validari() {

        return true;
    }

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
                } else {
                    tilUsername.setError(null);
                }
            }
        };
    }

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
                    tilEmail.setError("Emailul trebuie sa aiba cel putin 5 caractere");
                } else if (stringTietEmail.length() > 40) {
                    tilEmail.setError("Emailul trebuie sa aiba maximum 40 de caractere");
                } else if (!(stringTietEmail.contains("@") && stringTietEmail.contains("."))) {
                    tilEmail.setError("Emailul trebuie sa fie de forma xxxx@xxxx.xx");
                } else {
                    tilEmail.setError(null);
                    //userService.getCountByEmail(stringTietEmail, callbackGetCountByEmail());
                }
            }
        };
    }

    private Callback<Integer> callbackGetCountByEmail() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result != 0) {
                    tilEmail.setError("Emailul este deja inregistrat");
                }
            }
        };
    }
}