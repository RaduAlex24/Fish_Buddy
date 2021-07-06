package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.database.service.CommentForumService;
import com.example.licenta.database.service.ForumPostService;
import com.example.licenta.database.service.LikeCommentService;
import com.example.licenta.database.service.LikeForumService;

import org.jetbrains.annotations.NotNull;

public class ProfileActivity extends AppCompatActivity {

    // Controale vizuale
    private TextView tvPageTitle;
    private ImageView imageViewProfilePicture;
    private TextView tvFishingTitle;
    private TextView tvNumberAnswers;
    private TextView tvNumberLikes;
    private TextView tvNumberPostsCreated;
    private TextView tvNumberPoints;
    private ListView lvBestFish;
    private TextView tvUsername;
    private TextView tvEmail;
    private Button btnModificareCont;
    private Button btnStergereCont;


    // Utile
    private CurrentUser currentUser = CurrentUser.getInstance();
    private CommentForumService commentForumService = new CommentForumService();
    private LikeForumService likeForumService = new LikeForumService();
    private LikeCommentService likeCommentService = new LikeCommentService();
    private ForumPostService forumPostService = new ForumPostService();

    private int numarAprecieri = 0;


    // On create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initializare componente
        initComponents();

        // Inlocuire campuri
        replaceFields();
    }


    // Metode
    // Initializare componente
    private void initComponents() {
        // Text view
        tvPageTitle = findViewById(R.id.tv_title_profile);
        tvFishingTitle = findViewById(R.id.tv_fisingTitle_profile);
        tvNumberAnswers = findViewById(R.id.tv_numberAnswers_profile);
        tvNumberLikes = findViewById(R.id.tv_numberLikes_profile);
        tvNumberPostsCreated = findViewById(R.id.tv_numberPostsCreated_profile);
        tvNumberPoints = findViewById(R.id.tv_numberPoints_profile);
        tvUsername = findViewById(R.id.tv_username_profile);
        tvEmail = findViewById(R.id.tv_email_profile);

        // Image view
        imageViewProfilePicture = findViewById(R.id.imageView_profile);

        // Lista
        lvBestFish = findViewById(R.id.lv_bestFish_profile);

        // Butoane
        btnModificareCont = findViewById(R.id.btn_modificareCont_profile);
        btnStergereCont = findViewById(R.id.btn_stergereCont_profile);
    }


    // Inlocuire campuri
    private void replaceFields() {
        repaceFromAccount();
        replaceFromDataBase();
    }


    // Inlocuire instanta
    private void repaceFromAccount() {
        // Titlu
        String surname = currentUser.getSurname().substring(0, 1).toUpperCase() + currentUser.getSurname().substring(1).toLowerCase();
        String name = currentUser.getName().substring(0, 1).toUpperCase() + currentUser.getName().substring(1).toLowerCase();
        tvPageTitle.setText(getString(R.string.profile_title_replace, surname + " " + name));

        // Puncte obtinute
        tvNumberPoints.setText(getString(R.string.profile_puncteObtinute_replace, currentUser.getPoints()));

        // Username
        tvUsername.setText(getString(R.string.profile_numeUtiliator_replace, currentUser.getUsername()));

        // Email
        tvEmail.setText(getString(R.string.profile_email_replace, currentUser.getEmail()));
    }


    // Inlocuire din bd
    private void replaceFromDataBase() {
        // Comentarii create
        commentForumService.getAnswersCountByUserId(currentUser.getId(), callbackPreluareNumarComentarii());

        // Aprecieri acordate
        likeForumService.getForumLikesCountByUserId(currentUser.getId(), callbackPreluareNumarLikeuriForum());

        // Postari create
        forumPostService.getForumPostCountByUserId(currentUser.getId(), callbackPreluareNumarPostariCreate());
    }


    // Callback preluare numar comentarii
    @NotNull
    private Callback<Integer> callbackPreluareNumarComentarii() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                tvNumberAnswers.setText(getString(R.string.profile_raspunsuriAcordate_replace, result));
            }
        };
    }


    // Callback pentru preluare numar likeuri postari forum
    @NotNull
    private Callback<Integer> callbackPreluareNumarLikeuriForum() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                numarAprecieri += result;
                likeCommentService.getCommentLikesCountByUserId(currentUser.getId(), callbackPreluareNumarLikeuriComentarii());
            }
        };
    }


    // Callback pentru preluare numar likeuri comentarii
    @NotNull
    private Callback<Integer> callbackPreluareNumarLikeuriComentarii() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                numarAprecieri += result;
                tvNumberLikes.setText(getString(R.string.profile_aprecieriAcordate_replace, numarAprecieri));
            }
        };
    }


    // Callback preluare numar postari create
    @NotNull
    private Callback<Integer> callbackPreluareNumarPostariCreate() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                tvNumberPostsCreated.setText(getString(R.string.profile_postariCreate_replace, result));
            }
        };
    }

}