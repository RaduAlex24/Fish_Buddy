package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.licenta.clase.forum.CategoryForum;
import com.example.licenta.clase.forum.ForumPost;
import com.example.licenta.clase.forum.ForumPostLvAdapter;
import com.example.licenta.clase.forum.LikeForum;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.homeFragments.ForumFragment;
import com.example.licenta.util.dateUtils.DateConverter;
import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.widget.ArrayAdapter.createFromResource;

public class ForumPostDetailedActivity extends AppCompatActivity {

    public static final String FORUM_POST_MODIFICAT_LIKE_DISLIKE_KEY = "FORUM_POST_MODIFICAT_LIKE/DISLIKE_KEY";
    // Componente vizuale
    private ListView lvForumPostDetalied;
    private Spinner spinnerSortComments;
    private ListView lvComments;
    private TextInputEditText tietAddComment;
    private ImageButton imgBtnAddComment;

    // Utile
    private CurrentUser currentUser = CurrentUser.getInstance();
    private Intent intentPrimit;
    private List<ForumPost> forumPostListSingular = new ArrayList<>();
    private Map<Integer, LikeForum> likeForumMap;
    private List<Integer> favouritePostsIdList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_post_detailed);

        // Initializare componente
        initComponents();

        // Initializare spinner
        initSortCommentsSpinner();

        // Preluare intent
        initIntent();

        // Initializare adapter listview forum post
        initLvAdapterForumPost();
    }


    // Functii
    // Initializare componente
    private void initComponents() {
        lvForumPostDetalied = findViewById(R.id.lv_forumPost_forumPostDetailed);
        spinnerSortComments = findViewById(R.id.spinner_sortComments_forumPostDetailed);
        lvComments = findViewById(R.id.lv_comments_forumPostDetailed);
        tietAddComment = findViewById(R.id.tiet_addComment_forumPostDetailed);
        imgBtnAddComment = findViewById(R.id.imgBtn_addComment_forumPostDetailed);
    }


    // Initializare spinner
    private void initSortCommentsSpinner() {
        ArrayAdapter<CharSequence> adapter = createFromResource(getApplicationContext(),
                R.array.OptiuniSortareComentarii,
                android.R.layout.simple_spinner_dropdown_item);

        spinnerSortComments.setAdapter(adapter);
    }


    // Initializare intent
    private void initIntent() {
        // Intent
        intentPrimit = getIntent();

        // Forum post
        ForumPost forumPost = (ForumPost) intentPrimit.getSerializableExtra(ForumFragment.FORUM_POST_KEY);
        forumPostListSingular.clear();
        forumPostListSingular.add(forumPost);

        // Like forum map
        likeForumMap = (Map<Integer, LikeForum>) intentPrimit.getSerializableExtra(ForumFragment.LIKE_FORUM_MAP_KEY);

        // Favourite posts id list
        favouritePostsIdList = (List<Integer>) intentPrimit.getSerializableExtra(ForumFragment.FAVOURITE_POSTS_ID_LIST_KEY);
    }


    // Initializare adapter listview forum post
    private void initLvAdapterForumPost() {
        ForumPostLvAdapter adapter = new ForumPostLvAdapter(getApplicationContext(),
                R.layout.listview_row_forum_post, forumPostListSingular, getLayoutInflater(),
                likeForumMap, currentUser, favouritePostsIdList, true);
        lvForumPostDetalied.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        intentPrimit.putExtra(FORUM_POST_MODIFICAT_LIKE_DISLIKE_KEY, (Serializable) forumPostListSingular);
        setResult(RESULT_CANCELED, intentPrimit);

        super.onBackPressed();
    }
}