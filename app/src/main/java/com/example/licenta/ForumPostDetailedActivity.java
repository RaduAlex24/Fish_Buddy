package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.forum.CommentForum;
import com.example.licenta.clase.forum.ForumPost;
import com.example.licenta.clase.forum.ForumPostLvAdapter;
import com.example.licenta.clase.forum.LikeForum;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.database.service.CommentForumService;
import com.example.licenta.homeFragments.ForumFragment;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

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

    // Utile post
    private CurrentUser currentUser = CurrentUser.getInstance();
    private Intent intentPrimit;
    private List<ForumPost> forumPostListSingular = new ArrayList<>();
    private ForumPost forumPost;
    private Map<Integer, LikeForum> likeForumMap;
    private List<Integer> favouritePostsIdList;

    // Utile comments
    private CommentForumService commentForumService = new CommentForumService();
    private List<CommentForum> commentForumList = new ArrayList<>();
    private static final String tagLog = "ForumPostDetaliedAct";


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

        // Initializare listview adapter pentru forum comments
        initLvAdapterCommentsForum();

        // Preluare comentarii
        initCommentsByForumPostId();

        // Functie pentru butonul add comment
        imgBtnAddComment.setOnClickListener(onClickAddComment());

        // Text watcher pentru tiet add comment
        tietAddComment.addTextChangedListener(textWatcherAddComment());
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
        forumPost = (ForumPost) intentPrimit.getSerializableExtra(ForumFragment.FORUM_POST_KEY);
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


    // Pentru intoarcere la navigare
    @Override
    public void onBackPressed() {
        intentPrimit.putExtra(FORUM_POST_MODIFICAT_LIKE_DISLIKE_KEY, (Serializable) forumPostListSingular);
        setResult(RESULT_CANCELED, intentPrimit);

        super.onBackPressed();
    }


    // Initializare listview adapter pentru forum comments
    private void initLvAdapterCommentsForum() {
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_1, commentForumList);
        lvComments.setAdapter(adapter);
    }


    // Notificare adapter de schimbare
    private void notifyInternalAdapter() {
        ArrayAdapter adapter = (ArrayAdapter) lvComments.getAdapter();
        adapter.notifyDataSetChanged();
    }


    // Preluare initiala a comentariilor
    private void initCommentsByForumPostId() {
        commentForumService.getCommentsByForumPostId(forumPost.getId(), callbackGetCommentsByForumPostId());
    }


    // Callback preluare initiala a comentariilor
    @NotNull
    private Callback<List<CommentForum>> callbackGetCommentsByForumPostId() {
        return new Callback<List<CommentForum>>() {
            @Override
            public void runResultOnUiThread(List<CommentForum> result) {
                commentForumList.addAll(result);
                notifyInternalAdapter();
            }
        };
    }


    // Functie pentru butonul add comment
    @NotNull
    private View.OnClickListener onClickAddComment() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validareComentariu(tietAddComment)) {
                    // Preluare next comment id
                    commentForumService.getNextCommentId(callbackGetNextCommentId());
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_comentariu_incorect),
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    // Callback preluare next comment id
    @NotNull
    private Callback<Integer> callbackGetNextCommentId() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result != -1) {

                    // Preluare date
                    int id = result;
                    int userId = currentUser.getId();
                    int forumPostId = forumPost.getId();
                    String creatorUsername = currentUser.getUsername();
                    String content = tietAddComment.getText().toString().trim();

                    CommentForum commentForum = new CommentForum(id, userId, forumPostId,
                            creatorUsername, content);
                    commentForumService.insertNewCommentForum(commentForum,
                            callbackInsertNewComment(commentForum));

                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.eroare_nextId_postForum),
                            Toast.LENGTH_SHORT).show();
                    Log.e(tagLog, getString(R.string.log_nextId_comment));
                }
            }
        };
    }


    // Callback insert new comment
    @NotNull
    private Callback<Integer> callbackInsertNewComment(CommentForum commentForum) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    commentForumList.add(commentForum);
                    notifyInternalAdapter();
                    tietAddComment.setText("");
                    tietAddComment.setError(null);
                    closeKeyboard();

                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_comentariu_adaugat_succes),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(tagLog, getString(R.string.tagLog_insertNewComment_MoreThanOneRow));
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_eroare_bd),
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
    }


    // Validare comentariu
    private boolean validareComentariu(TextInputEditText tiet) {
        if (tiet.getText().toString().replace(" ", "").length() == 0 ||
                tiet.getError() != null) {
            return false;
        }

        return true;
    }


    // Text watcher pentru tiet add comment
    @NotNull
    private TextWatcher textWatcherAddComment() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String stringTietComment = tietAddComment.getText().toString().replace(" ", "");

                if (stringTietComment.length() < 2) {
                    tietAddComment.setError(getString(R.string.error_comment_sub2Char));
                } else if (stringTietComment.length() >= 200) {
                    tietAddComment.setError(getString(R.string.error_comment_peste20Char));
                } else {
                    tietAddComment.setError(null);
                }

            }
        };
    }


    // Inchidere tastatura
    private void closeKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this
                    .getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}