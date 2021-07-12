package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.database.service.CommentForumService;
import com.example.licenta.database.service.ForumPostService;
import com.example.licenta.database.service.UserService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import static com.example.licenta.LogInActivity.PASSWORD_SP;
import static com.example.licenta.LogInActivity.SHARED_PREF_FILE_NAME;
import static com.example.licenta.LogInActivity.USERNAME_SP;

public class SignUpActivity extends AppCompatActivity {

    public static final String PASSWORD_KEY = "PASSWORD_KEY";
    public static final String USERNAME_KEY = "USERNAME_KEY";
    private TextView tvTitle;
    private TextInputEditText tietUsername;
    private TextInputEditText tietSurname;
    private TextInputEditText tietName;
    private TextInputEditText tietEmail;
    private TextInputEditText tietPassword;
    private TextInputEditText tietConfirmPassword;
    private TextInputLayout tilUsername;
    private TextInputLayout tilSurname;
    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;
    private Button btnSignup;

    private UserService userService = new UserService();
    private Intent intent;
    private boolean isEditing = false;
    private CurrentUser currentUser = null;
    private SharedPreferences preferences;

    private static final String tagLog = "ActivitateSignUp";

    private ForumPostService forumPostService = new ForumPostService();
    private CommentForumService commentForumService = new CommentForumService();

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

        // Verificare editare
        verificareEditare();

    }


    // Functii
    // Initializare componente
    private void initComponents() {
        tvTitle = findViewById(R.id.tv_title_signup);
        tietUsername = findViewById(R.id.tiet_username_signup);
        tietSurname = findViewById(R.id.tiet_surname_signup);
        tietName = findViewById(R.id.tiet_name_signup);
        tietEmail = findViewById(R.id.tiet_email_signup);
        tietPassword = findViewById(R.id.tiet_password_signup);
        tietConfirmPassword = findViewById(R.id.tiet_confirm_password_signup);
        btnSignup = findViewById(R.id.btn_signup);
        tilUsername = findViewById(R.id.til_username_signup);
        tilSurname = findViewById(R.id.til_surname_signup);
        tilName = findViewById(R.id.til_name_signup);
        tilEmail = findViewById(R.id.til_email_signup);
        tilPassword = findViewById(R.id.til_password_signup);
        tilConfirmPassword = findViewById(R.id.til_confirm_password_signup);
        intent = getIntent();
    }


    // Verificare editare
    private void verificareEditare() {
        if (intent.hasExtra(ProfileActivity.CURRENT_USER_KEY)) {
            isEditing = true;
            currentUser = (CurrentUser) intent.getSerializableExtra(ProfileActivity.CURRENT_USER_KEY);
            adaugareUserInCasute(currentUser);
            schimbareTitluSiButon();
        }
    }


    // Adaugare atributele currentuser in casutele corespunatoare
    private void adaugareUserInCasute(CurrentUser currentUser) {
        tietUsername.setText(currentUser.getUsername().split(":")[0]);
        tietSurname.setText(currentUser.getSurname());
        tietName.setText(currentUser.getName());
        tietEmail.setText(currentUser.getEmail());
        tietPassword.setText(currentUser.getPassword());
        tietConfirmPassword.setText(currentUser.getPassword());
    }


    // Schimbare text din titlu si buton
    private void schimbareTitluSiButon() {
        tvTitle.setText(R.string.modificare_cont_title_signUpActivity);
        btnSignup.setText(R.string.modificare_buton_editare_cont);
    }


    // Functie click pentru butonul signu up
    private View.OnClickListener onClickSignUp() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validareFinala()) {
                    // Atribute
                    String username = extragereStringDinTiet(tietUsername);
                    String password = extragereStringDinTiet(tietPassword);
                    String email = extragereStringDinTiet(tietEmail);
                    String surname = extragereStringDinTiet(tietSurname);
                    String name = extragereStringDinTiet(tietName);

                    if (!isEditing) {
                        // Inserare in BD
                        userService.insertNewUser(username, password, email, surname, name,
                                callbackInsertUser(username, password));
                    } else {
                        username += ":" + currentUser.getFishingTitle().toString();

                        // Testare atribute schimbate
                        if (!password.equals(currentUser.getPassword()) || !username.equals(currentUser.getUsername())) {
                            // Schimbare sp
                            actualizareSharedPReferences(username.split(":")[0], password);
                        }
                        if (!username.equals(currentUser.getUsername())) {
                            // Schimbare bd
                            commentForumService.updateCreatorUsernameById(currentUser.getId(), username,
                                    callbackUpdateUsernameComments(username, password, email, surname, name));
                        } else {
                            // Modificare currentuser
                            CurrentUser.changeCurrentUserDetails(username, password, email, surname, name);

                            // Update cont existent
                            userService.updateUserByCurrentuser(CurrentUser.getInstance(), callbackUpdateUser());
                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_criterii_neindeplinite_signUp),
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
    }


    // nu merge???? TO DO
    // Actualiare shared preferences
    private void actualizareSharedPReferences(String username, String password) {
        preferences = getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USERNAME_SP, username);
        editor.putString(PASSWORD_SP, password);
        editor.apply();
    }


    // Callback update creatorusername pentru comentarii
    @NotNull
    private Callback<Integer> callbackUpdateUsernameComments(String username, String password,
                                                             String email, String surname, String name) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                forumPostService.updateCreatorUsernameById(currentUser.getId(), username,
                        callbackUpdateUsernamePosts(username, password, email, surname, name));
            }
        };
    }


    // Callback update creatorusername pentru postari
    @NotNull
    private Callback<Integer> callbackUpdateUsernamePosts(String username, String password,
                                                          String email, String surname, String name) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                // Modificare currentuser
                CurrentUser.changeCurrentUserDetails(username, password, email, surname, name);

                // Update cont existent
                userService.updateUserByCurrentuser(CurrentUser.getInstance(), callbackUpdateUser());
            }
        };
    }


    // Callback update user
    @NotNull
    private Callback<Integer> callbackUpdateUser() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    setResult(RESULT_OK, intent);
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_modificare_cont_succes),
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(tagLog, getString(R.string.log_maiMultDeUnUserModificat));
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
        if (!validarePartiala(tilUsername, tietUsername)) {
            return false;
        }
        if (!validarePartiala(tilSurname, tietSurname)) {
            return false;
        }
        if (!validarePartiala(tilName, tietName)) {
            return false;
        }
        if (!validarePartiala(tilEmail, tietEmail)) {
            return false;
        }
        if (!validarePartiala(tilPassword, tietPassword)) {
            return false;
        }
        if (!validarePartiala(tilConfirmPassword, tietConfirmPassword)) {
            return false;
        }

        return true;
    }

    // Validare partiala
    private boolean validarePartiala(TextInputLayout til, TextInputEditText tiet) {
        if (tiet.getText().toString().replace(" ", "").length() == 0 ||
                til.getError() != null) {
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
                    tilPassword.setError("Parola trebuie sa aiba cel putin 6 caractere");
                } else if (stringTietPassword.length() > 20) {
                    tilPassword.setError("Parola trebuie sa aiba maximul 20 de caractere");
                } else if (stringTietConfirmaPassword.length() != 0) {
                    if (!stringTietPassword.equals(stringTietConfirmaPassword)) {
                        tilPassword.setError("Parolele nu coincid");
                        tilConfirmPassword.setError("Parolele nu coincid");
                    } else {
                        tilPassword.setError(null);
                        tilConfirmPassword.setError(null);
                    }
                } else {
                    tilPassword.setError(null);
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
                    tilConfirmPassword.setError("Parola trebuie sa aiba cel putin 6 caractere");
                } else if (stringTietConfirmaPassword.length() > 20) {
                    tilConfirmPassword.setError("Parola trebuie sa aiba maximul 20 de caractere");
                } else if (stringTietPassword.length() != 0) {
                    if (!stringTietPassword.equals(stringTietConfirmaPassword)) {
                        tilPassword.setError("Parolele nu coincid");
                        tilConfirmPassword.setError("Parolele nu coincid");
                    } else {
                        tilPassword.setError(null);
                        tilConfirmPassword.setError(null);
                    }
                } else {
                    tilConfirmPassword.setError(null);
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
                    tilEmail.setError("Emailul trebuie sa aiba cel putin 5 caractere");
                } else if (stringTietEmail.length() > 40) {
                    tilEmail.setError("Emailul trebuie sa aiba maximum 40 de caractere");
                } else if (!(stringTietEmail.contains("@") && stringTietEmail.contains("."))) {
                    tilEmail.setError("Emailul trebuie sa fie de forma xxxx@xxxx.xx");
                } else {
                    tilEmail.setError(null);
                    userService.getCountByEmail(stringTietEmail, callbackGetCountByEmail(stringTietEmail));
                }
            }
        };
    }

    // Callback pt get count by email
    private Callback<Integer> callbackGetCountByEmail(String stringTietEmail) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result != 0) {
                    if (!(currentUser != null && isEditing && currentUser.getEmail().equals(stringTietEmail))) {
                        tilEmail.setError("Emailul este deja inregistrat");
                    }
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
                    tilName.setError("Prenumele trebuie sa aiba cel putin 2 caractere");
                } else if (stringTietName.length() > 40) {
                    tilName.setError("Prenumele trebuie sa aiba maximum 40 de caractere");
                } else {
                    tilName.setError(null);
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
                    tilSurname.setError("Numele de familie trebuie sa aiba cel putin 2 caractere");
                } else if (stringTietSurname.length() > 20) {
                    tilSurname.setError("Numele de familie trebuie sa aiba maximum 20 caractere");
                } else {
                    tilSurname.setError(null);
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
                    tilUsername.setError("Numele de utilizator trebuie sa aiba cel putin 6 caractere");
                } else if (stringTietUsername.length() > 15) {
                    tilUsername.setError("Numele de utilizator trebuie sa aiba maximum 6 caractere");
                } else {
                    tilUsername.setError(null);
                    userService.getCountByUsername(stringTietUsername, callbackCountByUsername(stringTietUsername));
                }
            }
        };
    }

    // Callback count by username
    private Callback<Integer> callbackCountByUsername(String stringTietUsername) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result != 0) {
                    if (!(currentUser != null && isEditing &&
                            currentUser.getUsername().split(":")[0].equals(stringTietUsername))) {
                        tilUsername.setError("Numele de utilizator este deja folosit");
                    }
                }
            }
        };
    }

}