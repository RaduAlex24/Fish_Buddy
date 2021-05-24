package com.example.licenta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.forum.CommentForum;
import com.example.licenta.clase.forum.CommentLvAdapter;
import com.example.licenta.clase.forum.ForumPost;
import com.example.licenta.clase.forum.ForumPostLvAdapter;
import com.example.licenta.clase.forum.LikeComment;
import com.example.licenta.clase.forum.LikeForum;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.database.service.CommentForumService;
import com.example.licenta.database.service.ForumPostService;
import com.example.licenta.database.service.LikeCommentService;
import com.example.licenta.homeFragments.ForumFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.ArrayAdapter.createFromResource;

public class ForumPostDetailedActivity extends AppCompatActivity {

    // Componente vizuale
    private ListView lvForumPostDetalied;
    private Spinner spinnerSortComments;
    private ListView lvComments;
    private TextInputLayout tilAddComment;
    private TextInputEditText tietAddComment;
    private ImageButton imgBtnAddComment;

    // Utile forum post
    private Intent intentPrimit;
    private List<ForumPost> forumPostListSingular = new ArrayList<>();
    private ForumPost forumPost;
    private ForumPostService forumPostService = new ForumPostService();
    private Map<Integer, LikeForum> likeForumMap;
    private List<Integer> favouritePostsIdList;

    // Utile comments
    private CommentForumService commentForumService = new CommentForumService();
    private LikeCommentService likeCommentService = new LikeCommentService();
    private List<CommentForum> commentForumList = new ArrayList<>();
    private Map<Integer, LikeComment> likeCommentMap = new HashMap<>();
    private String commentsOrder = "ORDER BY nrLikes-nrDislikes DESC";
    private CommentForum commentForumModificat;

    // Utile generale
    private CurrentUser currentUser = CurrentUser.getInstance();
    private static final String tagLog = "ForumPostDetaliedAct";
    public static final String FORUM_POST_MODIFICAT_LIKE_DISLIKE_KEY = "FORUM_POST_MODIFICAT_LIKE/DISLIKE_KEY";
    public static final String LogTag = "LogTagForumPostDetailed";
    private boolean isEditingComment = false;


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

        // Preluare comentarii
        initCommentsByForumPostId();

        // Functie pentru butonul add comment
        imgBtnAddComment.setOnClickListener(onClickAddComment());

        // Text watcher pentru tiet add comment
        tietAddComment.addTextChangedListener(textWatcherAddComment());

        // Adaugare eveniment pentru sortare comentarii
        spinnerSortComments.setOnItemSelectedListener(onItemClickSpinnerSortComments());

        // Adaugare functie lv comments on long click pt editare
        lvComments.setOnItemLongClickListener(onLongClickEditComment());
    }


    // Functii
    // Initializare componente
    private void initComponents() {
        lvForumPostDetalied = findViewById(R.id.lv_forumPost_forumPostDetailed);
        spinnerSortComments = findViewById(R.id.spinner_sortComments_forumPostDetailed);
        lvComments = findViewById(R.id.lv_comments_forumPostDetailed);
        tilAddComment = findViewById(R.id.til_addComment_forumPostDetailed);
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
        CommentLvAdapter adapter = new CommentLvAdapter(getApplicationContext(),
                R.layout.listview_row_comment_forum, commentForumList, getLayoutInflater(),
                likeCommentMap, currentUser, forumPost, commentsOrder);
        lvComments.setAdapter(adapter);
    }


    // Notificare adapter comentarii de schimbare
    private void notifyInternalAdapter() {
        ArrayAdapter adapter = (ArrayAdapter) lvComments.getAdapter();
        adapter.notifyDataSetChanged();
    }


    // Notificare adapter post de schimbare
    private void notifyInternalForumPostAdapter() {
        ArrayAdapter adapter = (ArrayAdapter) lvForumPostDetalied.getAdapter();
        adapter.notifyDataSetChanged();
    }


    // Preluare initiala a like comment
    private void getCommentLikes() {
        likeCommentService.getLikeCommentMapByUserIdAndForumPostId(currentUser.getId(),
                forumPost.getId(),
                callbackGetCommentsLike());
    }

    // Callback preluare likeuri pentru commentarii
    @NotNull
    private Callback<Map<Integer, LikeComment>> callbackGetCommentsLike() {
        return new Callback<Map<Integer, LikeComment>>() {
            @Override
            public void runResultOnUiThread(Map<Integer, LikeComment> result) {
                likeCommentMap = result;

                // Initializare listview adapter pentru forum comments
                initLvAdapterCommentsForum();
                notifyInternalAdapter();
            }
        };
    }


    // On long click editare comentariu
    @NotNull
    private AdapterView.OnItemLongClickListener onLongClickEditComment() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                commentForumModificat = commentForumList.get(position);

                // Verificare apartenenta comentariu
                if (currentUser.getId() == commentForumModificat.getUserId()) {
                    showDialogBuilderForComment(commentForumModificat);
                }

                return true;
            }
        };
    }


    // Afisare dialog builder modificare comentariu propriu
    private void showDialogBuilderForComment(CommentForum commentForum) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editare comentariu propriu");
        builder.setMessage("Doriti sa stergeri sau sa modificati comentariul?");


        // Anulare operatie
        builder.setNeutralButton("Anulare", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Modificare comentariu
        builder.setPositiveButton("Modificare", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tietAddComment.setText(commentForum.getContent());
                tietAddComment.requestFocus();
                tilAddComment.setHint("Editare comnetariu");
                isEditingComment = true;
                openKeyboard();
            }
        });

        // Stergere comentariu
        builder.setNegativeButton("Stergere", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                likeCommentService.deleteLikeCommentByCommentId(commentForum.getId(),
                        callbackStergereLikeCommentByCommentId(commentForum));
            }
        });

        builder.show();
    }


    // Callback stergere like comment by comment id
    @NotNull
    private Callback<Integer> callbackStergereLikeCommentByCommentId(CommentForum commentForum) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                likeCommentMap.remove(commentForum.getId());
                commentForumService.deleteCommentByCommentId(commentForum.getId(),
                        callbackStergereCommentByCommentId(commentForum));
            }
        };
    }


    // Callback stergere comment by comment id
    @NotNull
    private Callback<Integer> callbackStergereCommentByCommentId(CommentForum commentForum) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                commentForumList.remove(commentForum);
                notifyInternalAdapter();

                // Modificare nr comms forum post
                forumPostService.updateNrCommentsByForumPostAndNrComments(forumPost, -1,
                        callbackModificareNegativaNrDeCommForumPost());
            }
        };
    }


    // Callback modificare cu -1 a nr de commentarii forum post
    @NotNull
    private Callback<Integer> callbackModificareNegativaNrDeCommForumPost() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    forumPost.setNrComments(forumPost.getNrComments() - 1);
                    notifyInternalForumPostAdapter();
                } else {
                    Log.e(LogTag, getString(R.string.log_updateForumPostsComments_ForumPostDetailed));
                }
            }
        };
    }


    // Preluare initiala a comentariilor
    private void initCommentsByForumPostId() {
        commentForumService.getCommentsByForumPostId(forumPost.getId(),
                callbackGetCommentsByForumPostId(),
                commentsOrder);
    }


    // Callback preluare initiala a comentariilor
    @NotNull
    private Callback<List<CommentForum>> callbackGetCommentsByForumPostId() {
        return new Callback<List<CommentForum>>() {
            @Override
            public void runResultOnUiThread(List<CommentForum> result) {
                commentForumList.clear();
                commentForumList.addAll(result);

                // Preluare a like pentru comments
                getCommentLikes();
            }
        };
    }


    // Modificare comentariu
    private void updateComment(CommentForum commentForum) {
        String newContent = tietAddComment.getText().toString().trim();
        commentForum.setContent(newContent);
        commentForum.setEdited(true);

        commentForumService.updateContentByCommentForum(commentForum, callbackUpdateCommentContent());
    }

    // Callback update content comment
    @NotNull
    private Callback<Integer> callbackUpdateCommentContent() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    finalizareOperatieCuTIETaddComment();
                    tilAddComment.setHint("Adauga un comentariu");
                    isEditingComment = false;
                    notifyInternalAdapter();

                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_comentariu_actualizat_succes),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(LogTag, getString(R.string.log_updateComment_ForumPostDetalied));
                }
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
                    if (!isEditingComment) {
                        // Adaugare comentariu nou
                        // Preluare next comment id
                        commentForumService.getNextCommentId(callbackGetNextCommentId());
                    } else if (commentForumModificat != null) {
                        // Modificare comentariu
                        updateComment(commentForumModificat);
                    }
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


    // Finalizare operatie cu add coment text imput
    private void finalizareOperatieCuTIETaddComment() {
        tietAddComment.setText("");
        tietAddComment.setError(null);
        closeKeyboard();
        tietAddComment.clearFocus();
    }


    // Callback insert new comment
    @NotNull
    private Callback<Integer> callbackInsertNewComment(CommentForum commentForum) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    // Pentru a-l inserta in lista ordonat
//                    commentForumList.add(commentForum);
//                    initCommentsByForumPostId();

                    // Pentru a aparea primul
                    commentForumList.add(0, commentForum);
                    notifyInternalAdapter();

                    // Modificare bd
                    forumPostService.updateNrCommentsByForumPostAndNrComments(forumPost, 1,
                            callbackUpdateNrComments());

                    finalizareOperatieCuTIETaddComment();

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


    // Callback modificare numar comentarii
    @NotNull
    private Callback<Integer> callbackUpdateNrComments() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    forumPost.setNrComments(forumPost.getNrComments() + 1);
                    notifyInternalForumPostAdapter();
                } else {
                    Log.e(LogTag, getString(R.string.log_updateForumPostsComments_ForumPostDetailed));
                }
            }
        };
    }


    // On click pe spinner pentru sortare de comentarii
    @NotNull
    private AdapterView.OnItemSelectedListener onItemClickSpinnerSortComments() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Schimbare comments order
                switch (position) {
                    case 0:
                        commentsOrder = "ORDER BY nrLikes-nrDislikes DESC";
                        break;
                    case 1:
                        commentsOrder = "ORDER BY nrLikes-nrDislikes";
                        break;
                    case 2:
                        commentsOrder = "ORDER BY TO_DATE(postDate,'dd-mm-yyyy') DESC";
                        break;
                    case 3:
                        commentsOrder = "ORDER BY TO_DATE(postDate,'dd-mm-yyyy')";
                        break;
                    default:
                        commentsOrder = "ORDER BY nrLikes-nrDislikes DESC";
                }

                initCommentsByForumPostId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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


    // Meniu vechi pentru update si delete forum post
    // Creare menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(currentUser.getId() == forumPost.getUserId()) {
            super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.old_menu_edit_forumpost, menu);
            return true;
        } else {
            return false;
        }
    }


    // Selectare elemente din old menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.old_menu_editForumPost){
            Toast.makeText(getApplicationContext(), "Selectare optiune editare postare " + forumPost.getId(),
                    Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.old_menu_deleteForumPost){
            Toast.makeText(getApplicationContext(), "Selectare optiune stergere postare " + forumPost.getId(),
                    Toast.LENGTH_SHORT).show();
        }

        return  true;
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

    // Deschidere tastatura
    private void openKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }


}