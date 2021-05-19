package com.example.licenta.clase.forum;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.licenta.ForumPostDetailedActivity;
import com.example.licenta.R;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.database.service.CommentForumService;
import com.example.licenta.database.service.LikeCommentService;
import com.example.licenta.database.service.UserService;
import com.example.licenta.util.dateUtils.DateConverter;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CommentLvAdapter extends ArrayAdapter<CommentForum> {

    // Atribute
    private Context context;
    private List<CommentForum> commentList;
    private LayoutInflater inflater;
    private int resource;
    private Map<Integer, LikeComment> likeCommentMap;
    private CurrentUser currentUser;
    private ForumPost forumPostParinte;
    private String commentsOrder = "";

    // Controale vizuale
    private ImageButton btnLike;
    private ImageButton btnDislike;
    private TextView tvPoints;
    private TextView tvCreatorUsername;
    private TextView tvPostDate;
    private TextView tvContent;
    private TextView tvEdited;

    // Utile
    private LikeCommentService likeCommentService = new LikeCommentService();
    private CommentForumService commentForumService = new CommentForumService();
    private UserService userService = new UserService();
    private String LogTag = "ForumCommentLvAdapter";

    // Sortari
    Comparator<CommentForum> comparatorPointsDesc;


    // Constructor
    public CommentLvAdapter(@NonNull Context context, int resource, @NonNull List<CommentForum> objects,
                            LayoutInflater layoutInflater, Map<Integer, LikeComment> likeCommentMap,
                            CurrentUser currentUser, ForumPost forumPostParinte, String commentsOrder) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        this.commentList = objects;
        this.inflater = layoutInflater;
        this.likeCommentMap = likeCommentMap;
        this.currentUser = currentUser;
        this.forumPostParinte = forumPostParinte;
        this.commentsOrder = commentsOrder;
    }


    // Get view
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);

        // Comentariu
        CommentForum commentForum = commentList.get(position);

        // Initializare componente
        initComponents(view, commentForum);

        // Initializare like / dislike
        if (likeCommentMap.containsKey(commentForum.getId())) {
            initLikeComment(likeCommentMap.get(commentForum.getId()));
        }

        // Initializare comparatori
        initCommentsComparator();

        // Adaugare functii butoane
        btnLike.setOnClickListener(onClickLikeComment(commentForum));
        btnDislike.setOnClickListener(onClickDislikeComment(commentForum));

        return view;
    }


    // Functii
    // Initializare componente
    private void initComponents(View view, CommentForum commentForum) {
        // creator username
        tvCreatorUsername = view.findViewById(R.id.tv_creatorUsername_commentRowAdapter);
        tvCreatorUsername.setText(commentForum.getCreatorUsername());

        // is edited
        tvEdited = view.findViewById(R.id.tv_isEdited_commentRowAdapter);
        if(commentForum.isEdited()){
            tvEdited.setVisibility(View.VISIBLE);
        }

        // post date
        tvPostDate = view.findViewById(R.id.tv_postDate_commentRowAdapter);
        tvPostDate.setText(DateConverter.toString(commentForum.getPostDate()));

        // points
        tvPoints = view.findViewById(R.id.tv_nrPoints_commentRowAdapter);
        int points = commentForum.getNrLikes() - commentForum.getNrDislikes();
        tvPoints.setText(String.valueOf(points));

        // content
        tvContent = view.findViewById(R.id.tv_content_commentRowAdapter);
        tvContent.setText("   " + commentForum.getContent());

        // btn like
        btnLike = view.findViewById(R.id.imgBtn_like_commentRowAdapter);

        // btn dislike
        btnDislike = view.findViewById(R.id.imgBtn_dislike_commentRowAdapter);

        // setare butoane
        btnLike.setFocusable(false);
        btnDislike.setFocusable(false);

    }


    // Initializare comparator
    private void initCommentsComparator() {
        comparatorPointsDesc = new Comparator<CommentForum>() {
            @Override
            public int compare(CommentForum o1, CommentForum o2) {
                int points1 = o1.getNrLikes() - o1.getNrDislikes();
                int points2 = o2.getNrLikes() - o2.getNrDislikes();

                if (points1 > points2) {
                    return -1;
                } else if (points1 < points2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }


    // Ordonare dinaminca dupa apasare like dislike
    private void ordonareCommentsDupaApasareLikeDislike(){
        if(commentsOrder.equals("ORDER BY nrLikes-nrDislikes DESC")){
            Collections.sort(commentList, comparatorPointsDesc);
            notifyDataSetChanged();
        } else if (commentsOrder.equals("ORDER BY nrLikes-nrDislikes")){
            Collections.sort(commentList, comparatorPointsDesc);
            Collections.reverse(commentList);
            notifyDataSetChanged();
        }
    }


    // Initializare like dislike comment
    private void initLikeComment(LikeComment likeComment) {
        if (likeComment.isLiked()) {
            btnLike.setImageResource(R.drawable.ic_baseline_arrow_upward_24_blue);
        } else if (likeComment.isDisliked()) {
            btnDislike.setImageResource(R.drawable.ic_baseline_arrow_downward_24_red);
        }
    }


    // Functii butoane
    // LIKE
    @NotNull
    private View.OnClickListener onClickLikeComment(CommentForum commentForum) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nrLikeuriSchimbate = 0;
                int nrDislikeuriSchimbate = 0;

                // Schimbari vizuale si in BD a like comment
                if (likeCommentMap.containsKey(commentForum.getId())) {
                    LikeComment likeCommentExistent = likeCommentMap.get(commentForum.getId());

                    if (likeCommentExistent.isLiked()) {
                        // Stergere like comment existent
                        likeCommentService.deleteLikeCommentByUserIdAndCommentId(
                                likeCommentExistent.getUserId(),
                                likeCommentExistent.getCommentId(),
                                callbackStergereLikeComment(likeCommentExistent));
                        nrLikeuriSchimbate = -1;
                        nrDislikeuriSchimbate = 0;

                    } else if (likeCommentExistent.isDisliked()) {
                        // Editare like comment existent
                        likeCommentExistent.setLiked(true);
                        likeCommentExistent.setDisliked(false);
                        likeCommentService.updateLikeCommentByLikeComment(likeCommentExistent,
                                callbackEditareDislikeToLikeComment(likeCommentExistent));
                        nrLikeuriSchimbate = 1;
                        nrDislikeuriSchimbate = -1;

                    }
                } else {
                    // Creare nou like comment
                    LikeComment likeComment = new LikeComment(currentUser.getId(), commentForum.getId(),
                            forumPostParinte.getId(), true, false);
                    likeCommentService.insertNewLikeComment(likeComment,
                            callbackInsertNewLikeComment(likeComment, commentForum));
                    nrLikeuriSchimbate = 1;
                    nrDislikeuriSchimbate = 0;
                }

                // Schimbari vizuale si bd a nrLikeuri comment si puncte utilizator
                commentForum.setNrLikes(commentForum.getNrLikes() + nrLikeuriSchimbate);
                commentForum.setNrDislikes(commentForum.getNrDislikes() + nrDislikeuriSchimbate);
                commentForumService.updatenrLikesnrDislikesByCommentForum(commentForum,
                        callbackUpdatenrLikesnrDislikesByForumComment());

                // Schimbare puncte utilizator apreciat
                userService.updatePointsByUserIdAndPoints(commentForum.getUserId(),
                        nrLikeuriSchimbate, callbackUpdatePointsUser());

                // Ordonare
                ordonareCommentsDupaApasareLikeDislike();
            }
        };
    }


    // Callback editare dislike to like comment
    private Callback<Integer> callbackEditareDislikeToLikeComment(LikeComment likeCommentExistent) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    btnLike.setImageResource(R.drawable.ic_baseline_arrow_upward_24_blue);
                    likeCommentMap.put(likeCommentExistent.getCommentId(), likeCommentExistent);
                    notifyDataSetChanged();
                } else {
                    Log.e(LogTag, context.getString(R.string.log_update_dislike2like_commentLvAdapter));
                }
            }
        };
    }

    // Callback stergere like comment
    private Callback<Integer> callbackStergereLikeComment(LikeComment likeCommentExistent) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    btnLike.setImageResource(R.drawable.ic_baseline_arrow_upward_24);
                    likeCommentMap.remove(likeCommentExistent.getCommentId());
                    notifyDataSetChanged();
                } else {
                    Log.e(LogTag, context.getString(R.string.log_deleteLike_commentLvAdapter));
                }
            }
        };
    }

    // Callback insert new like comment
    private Callback<Integer> callbackInsertNewLikeComment(LikeComment likeComment, CommentForum commentForum) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    btnLike.setImageResource(R.drawable.ic_baseline_arrow_upward_24_blue);
                    likeCommentMap.put(commentForum.getId(), likeComment);
                    notifyDataSetChanged();
                } else {
                    Log.e(LogTag, context.getString(R.string.log_insert_likeComment_commentLvAdapter));
                }
            }
        };
    }


    // DISLIKE
    @NotNull
    private View.OnClickListener onClickDislikeComment(CommentForum commentForum) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nrLikeuriSchimbate = 0;
                int nrDislikeuriSchimbate = 0;

                // Schimbari vizuale si in BD a like comment
                if (likeCommentMap.containsKey(commentForum.getId())) {
                    LikeComment likeCommentExistent = likeCommentMap.get(commentForum.getId());

                    if (likeCommentExistent.isDisliked()) {
                        // Stergere like comment existent
                        likeCommentService.deleteLikeCommentByUserIdAndCommentId(
                                likeCommentExistent.getUserId(),
                                likeCommentExistent.getCommentId(),
                                callbackDeleteDislikeComment(likeCommentExistent));
                        nrLikeuriSchimbate = 0;
                        nrDislikeuriSchimbate = -1;

                    } else if (likeCommentExistent.isLiked()) {
                        // Editare like comment existent
                        likeCommentExistent.setLiked(false);
                        likeCommentExistent.setDisliked(true);
                        likeCommentService.updateLikeCommentByLikeComment(likeCommentExistent,
                                callbackUpdateLikeToDislikeComment(likeCommentExistent));
                        nrLikeuriSchimbate = -1;
                        nrDislikeuriSchimbate = 1;

                    }
                } else {
                    // Creare nou dislike comment
                    LikeComment likeComment = new LikeComment(currentUser.getId(), commentForum.getId(),
                            forumPostParinte.getId(), false, true);
                    likeCommentService.insertNewLikeComment(likeComment,
                            callbackInsertNewDislikeComment(likeComment, commentForum));
                    nrLikeuriSchimbate = 0;
                    nrDislikeuriSchimbate = 1;
                }

                // Schimbari vizuale si bd a nrLikeuri comment si puncte utilizator
                commentForum.setNrLikes(commentForum.getNrLikes() + nrLikeuriSchimbate);
                commentForum.setNrDislikes(commentForum.getNrDislikes() + nrDislikeuriSchimbate);
                commentForumService.updatenrLikesnrDislikesByCommentForum(commentForum,
                        callbackUpdatenrLikesnrDislikesByForumComment());

                // Schimbare puncte utilizator apreciat
                userService.updatePointsByUserIdAndPoints(commentForum.getUserId(),
                        nrLikeuriSchimbate, callbackUpdatePointsUser());

                // Ordonare
                ordonareCommentsDupaApasareLikeDislike();
            }
        };
    }


    // Callback update like to dislike comment
    private Callback<Integer> callbackUpdateLikeToDislikeComment(LikeComment likeCommentExistent) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    btnDislike.setImageResource(R.drawable.ic_baseline_arrow_downward_24_red);
                    likeCommentMap.put(likeCommentExistent.getCommentId(), likeCommentExistent);
                    notifyDataSetChanged();
                } else {
                    Log.e(LogTag, context.getString(R.string.log_update_like2dislike_commentLvAdapter));
                }
            }
        };
    }

    // Callback delete dislike comment
    private Callback<Integer> callbackDeleteDislikeComment(LikeComment likeCommentExistent) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    btnDislike.setImageResource(R.drawable.ic_baseline_arrow_downward_24);
                    likeCommentMap.remove(likeCommentExistent.getCommentId());
                    notifyDataSetChanged();
                } else {
                    Log.e(LogTag, context.getString(R.string.log_deleteDislike_commentLvAdapter));
                }
            }
        };
    }

    // Callback insert new dislike comment
    private Callback<Integer> callbackInsertNewDislikeComment(LikeComment likeComment, CommentForum commentForum) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    btnDislike.setImageResource(R.drawable.ic_baseline_arrow_downward_24_red);
                    likeCommentMap.put(commentForum.getId(), likeComment);
                    notifyDataSetChanged();
                } else {
                    Log.e(LogTag, context.getString(R.string.log_insertDislike_commentLvAdapter));
                }
            }
        };
    }


    // Universale
    // Callback update nr likes si nr dislikes pt forum comment by forum comment
    private Callback<Integer> callbackUpdatenrLikesnrDislikesByForumComment() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result != 1) {
                    Log.e(LogTag, context.getString(R.string.log_updateNrLikesDislikes_forum_forumPostLvAdapter));
                }
            }
        };
    }

    // Callback update points pt user
    private Callback<Integer> callbackUpdatePointsUser() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result != 1) {
                    Log.e(LogTag, context.getString(R.string.log_updatePoints_user_forumPostLvAdapter));
                }
            }
        };
    }

}
