package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.forum.CategoryForum;
import com.example.licenta.clase.forum.ForumPost;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.database.service.ForumPostService;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class CreateForumPostActivity extends AppCompatActivity {

    public static final String NEW_FORUM_POST_KEY = "NEW_FORUM_POST_KEY";
    private Spinner spinnerCategory;
    private TextInputEditText tiet_title;
    private TextInputEditText tiet_content;
    private Button btn_createForumPost;

    private Intent intent;
    private static final String tagLog = "ActCrearePostareForum";

    private CurrentUser currentUser = CurrentUser.getInstance();
    private ForumPostService forumPostService = new ForumPostService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_forum_post);

        // Initializare Componente
        initComponents();

        // Adaugare categorii spinner
        initCategorySpinner();

        // Functii validare date
        tiet_title.addTextChangedListener(watcherVerificareTitlu());
        tiet_content.addTextChangedListener(watcherVerificareContent());

        // Functie on click creare post forum
        btn_createForumPost.setOnClickListener(onClickCreateNewForumPost());
    }


    // Metode
    // Initializare componente
    private void initComponents() {
        // Preluare controale vizuale
        spinnerCategory = findViewById(R.id.spinner_category_createForumPost);
        tiet_title = findViewById(R.id.tiet_title_createForumPost);
        tiet_content = findViewById(R.id.tiet_content_createForumPost);
        btn_createForumPost = findViewById(R.id.btn_createForumPost);

        // Preluare intent
        intent = getIntent();
    }


    // Adaugare categorii spiner
    private void initCategorySpinner() {
        List<String> categoryList = new ArrayList<>();

        for (CategoryForum category : CategoryForum.values()) {
            categoryList.add(category.getLabel());
        }

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line, categoryList);
        spinnerCategory.setAdapter(adapter);
    }


    // On click creare post forum
    private View.OnClickListener onClickCreateNewForumPost() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validare()) {
                    // Preluare date
                    String textTitle = tiet_title.getText().toString().trim();
                    String textContent = tiet_content.getText().toString().trim();
                    CategoryForum category = getSelectedCategory();

                    // Creare si adaugare in BD
                    ForumPost forumPost = new ForumPost(currentUser.getId(), currentUser.getUsername(),
                            textTitle, textContent, category);
                    forumPostService.getNextForumPostId(callbackgetNextForumPostId(forumPost));
                } else {
                    Toast.makeText(CreateForumPostActivity.this,
                            getString(R.string.criterii_neindeplinite_postForumNou),
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
    }


    // Callback get next forum post id
    private Callback<Integer> callbackgetNextForumPostId(ForumPost forumPost) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result != -1) {
                    forumPostService.insertNewForumPost(forumPost, result, callbackCreateNewForum());
                } else {
                    Toast.makeText(CreateForumPostActivity.this,
                            getString(R.string.eroare_nextId_postForum),
                            Toast.LENGTH_SHORT).show();
                    Log.e(tagLog, getString(R.string.log_nextForumPostId));
                }
            }
        };
    }


    // Callback creare interventie forum noua
    private Callback<ForumPost> callbackCreateNewForum() {
        return new Callback<ForumPost>() {
            @Override
            public void runResultOnUiThread(ForumPost result) {
                Toast.makeText(CreateForumPostActivity.this,
                        getString(R.string.toast_creare_interventirForum_reusita),
                        Toast.LENGTH_SHORT).show();

                intent.putExtra(NEW_FORUM_POST_KEY, result);
                setResult(RESULT_OK, intent);
                finish();
            }
        };
    }


    // Validare finala
    private boolean validare() {
        if (tiet_title.getText().toString().trim().length() == 0) {
            return false;
        }

        if (tiet_content.getText().toString().trim().length() == 0) {
            return false;
        }

        if (tiet_title.getError() != null || tiet_content.getError() != null) {
            return false;
        }

        return true;
    }


    private CategoryForum getSelectedCategory() {
        long idSelectat = spinnerCategory.getSelectedItemId();
        CategoryForum categoryForum = null;

        switch ((int) idSelectat) {
            case 0:
                categoryForum = CategoryForum.SFATURI;
                break;
            case 1:
                categoryForum = CategoryForum.PESTI;
                break;
            case 2:
                categoryForum = CategoryForum.ECHIPAMENTE;
                break;
            case 3:
                categoryForum = CategoryForum.INCEPATORI;
                break;
            case 4:
                categoryForum = CategoryForum.GLUME;
                break;
            default:
                categoryForum = CategoryForum.SFATURI;
        }

        return categoryForum;
    }


    // Watchers
    // Watcher verificare titlu
    private TextWatcher watcherVerificareTitlu() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String textTitle = tiet_title.getText().toString().trim();

                if (textTitle.length() < 3) {
                    tiet_title.setError(getString(R.string.error_title_below3char));
                } else if (textTitle.length() > 100) {
                    tiet_title.setError(getString(R.string.error_title_aboce100char));
                }
            }
        };
    }

    // Wathcer verificare content
    private TextWatcher watcherVerificareContent() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String textContent = tiet_content.getText().toString().trim();

                if (textContent.length() < 5) {
                    tiet_content.setError(getString(R.string.error_content_below5char));
                } else if (textContent.length() > 500) {
                    tiet_content.setError(getString(R.string.error_content_above500char));
                }
            }
        };
    }


}