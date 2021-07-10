package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.peste.Peste;
import com.example.licenta.clase.peste.PestiAdaptor;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.clase.user.FishingTitleEnum;
import com.example.licenta.database.service.CommentForumService;
import com.example.licenta.database.service.FishService;
import com.example.licenta.database.service.ForumPostService;
import com.example.licenta.database.service.LikeCommentService;
import com.example.licenta.database.service.LikeForumService;
import com.example.licenta.database.service.UserService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.licenta.LogInActivity.SHARED_PREF_FILE_NAME;
import static com.example.licenta.VizualizatiPesti.FISH_ID_SP;

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
    private UserService userService = new UserService();
    private CommentForumService commentForumService = new CommentForumService();
    private LikeForumService likeForumService = new LikeForumService();
    private LikeCommentService likeCommentService = new LikeCommentService();
    private ForumPostService forumPostService = new ForumPostService();
    private FishService fishService = new FishService();

    private int numarAprecieri = 0;
    private SharedPreferences sharedPreferences;
    private List<Peste> pesteList = new ArrayList<>();


    // On create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initializare componente
        initComponents();

        // Inlocuire campuri
        replaceFields();

        // Adaugare adapter
        addAdapter();

        // Verificare existenta peste favorit
        getFavouriteFishFromSharedPreferences();
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

        // Shared preferences
        sharedPreferences = getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE);
    }


    // Get fish from shared preferences
    private void getFavouriteFishFromSharedPreferences(){
        int fishId = sharedPreferences.getInt(FISH_ID_SP, -1);

        if(fishId != -1){
            fishService.getFavouriteFishByIdAndUserId(fishId, currentUser.getId(),
                    callbackGetFavouriteFish());
        }
    }


    // Callback get favourite fish
    @NotNull
    private Callback<Peste> callbackGetFavouriteFish() {
        return new Callback<Peste>() {
            @Override
            public void runResultOnUiThread(Peste result) {
                pesteList.add(result);
                notifyAdapter();
            }
        };
    }


    // Adaugare adapter
    private void addAdapter() {
        PestiAdaptor adaptor = new PestiAdaptor(getApplicationContext(), R.layout.listview_pesti,
                pesteList, getLayoutInflater());
        lvBestFish.setAdapter(adaptor);
    }


    // Notificare adapter
    private void notifyAdapter() {
        PestiAdaptor adapter = (PestiAdaptor) lvBestFish.getAdapter();
        adapter.notifyDataSetChanged();
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

        // Username
        tvUsername.setText(getString(R.string.profile_numeUtiliator_replace, currentUser.getUsername().split(":")[0]));

        // Email
        tvEmail.setText(getString(R.string.profile_email_replace, currentUser.getEmail()));

        // Fishing title
        tvFishingTitle.setText(currentUser.getFishingTitle().getLabel());
    }


    // Inlocuire din bd
    private void replaceFromDataBase() {
        // Comentarii create
        commentForumService.getAnswersCountByUserId(currentUser.getId(), callbackPreluareNumarComentarii());

        // Aprecieri acordate
        likeForumService.getForumLikesCountByUserId(currentUser.getId(), callbackPreluareNumarLikeuriForum());

        // Postari create
        forumPostService.getForumPostCountByUserId(currentUser.getId(), callbackPreluareNumarPostariCreate());

        // Numar puncte
        userService.getPointsForCurrentUser(currentUser.getId(), callbackPreluareNumarPuncte());
    }


    // Callback preluare numar puncte
    @NotNull
    private Callback<Integer> callbackPreluareNumarPuncte() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                currentUser.setPoints(result);
                tvNumberPoints.setText(getString(R.string.profile_puncteObtinute_replace, currentUser.getPoints()));
            }
        };
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