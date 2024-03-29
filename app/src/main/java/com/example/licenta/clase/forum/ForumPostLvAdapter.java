package com.example.licenta.clase.forum;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.licenta.ProfileActivity;
import com.example.licenta.R;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.clase.user.FishingTitleEnum;
import com.example.licenta.database.service.FavouriteForumPostService;
import com.example.licenta.database.service.ForumPostService;
import com.example.licenta.database.service.LikeForumService;
import com.example.licenta.database.service.UserService;
import com.example.licenta.homeFragments.ForumFragment;
import com.example.licenta.util.dateUtils.DateConverter;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ForumPostLvAdapter extends ArrayAdapter<ForumPost> {

    // Atribute
    private Context context;
    private List<ForumPost> forumPostList;
    private LayoutInflater inflater;
    private int resource;
    private Map<Integer, LikeForum> likeForumMap;
    private List<Integer> favouritePostsIdList;
    private CurrentUser currentUser;
    private Boolean isDetalied;

    // Controale vizuale
    private TextView tvUser;
    private TextView tvCategory;
    private TextView tvEdited;
    private TextView tvDate;
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvNrLikes;
    private TextView tvNrComments;
    private ImageButton btnFavourite;
    private ImageButton btnLike;
    private ImageButton btnDislike;

    // Utile
    private LikeForumService likeForumService = new LikeForumService();
    private ForumPostService forumPostService = new ForumPostService();
    private FavouriteForumPostService favouriteForumPostService = new FavouriteForumPostService();
    private UserService userService = new UserService();
    private String LogTag = "ForumPostLvAdapter";
    private static final int MaxContentCharacters = 200;


    // Sortari
    Comparator<ForumPost> comparatorPointsDesc;
    Comparator<ForumPost> comparatorCommentsDesc;


    // Constructor
    public ForumPostLvAdapter(@NonNull Context context, int resource, @NonNull List<ForumPost> objects,
                              LayoutInflater layoutInflater, Map<Integer, LikeForum> likeForumMap,
                              CurrentUser currentUser, List<Integer> favouritePostsIdList, Boolean isDetalied) {
        super(context, resource, objects);

        this.context = context;
        this.forumPostList = objects;
        this.inflater = layoutInflater;
        this.resource = resource;
        this.likeForumMap = likeForumMap;
        this.favouritePostsIdList = favouritePostsIdList;
        this.currentUser = currentUser;
        this.isDetalied = isDetalied;
    }


    // GetView
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);

       ForumPost  forumPost = forumPostList.get(position);

        // Initializare componente
        initComponents(view, forumPost);

        // Initializare likes dislikes
        if (likeForumMap.containsKey(forumPost.getId())) {
            initLikeForum(likeForumMap.get(forumPost.getId()));
        }

        // Initializare favorite
        initFavouritePosts(forumPost.getId());

        // Initializare comparatori
        initForumPostsComparators();

        // Functii butoane
        btnLike.setOnClickListener(onClickLikeForumPost(forumPost));
        btnDislike.setOnClickListener(onClickDislikeForumPost(forumPost));
        btnFavourite.setOnClickListener(onClickAddFavouriteForumPost(forumPost));

        return view;
    }


    // Functii
    // Initializare componente
    private void initComponents(View view, ForumPost forumPost) {
        // user si fishing title
        FishingTitleEnum fishingTitleEnum = FishingTitleEnum.preluareTitluInFunctieDeUsername(forumPost.getCreatorUsername());
        tvUser = view.findViewById(R.id.tv_user_forumPostRowAdapter);
        tvUser.setText(forumPost.getCreatorUsername().split(":")[0]);
        tvUser.setTextColor(FishingTitleEnum.preluareCuloareInFunctieDeTitlu(fishingTitleEnum));

        // category
        tvCategory = view.findViewById(R.id.tv_category_forumPostRowAdapter);
        tvCategory.setText(forumPost.getCategory().getLabel());

        // is edited
        tvEdited = view.findViewById(R.id.tv_isEdited_forumPostRowAdapter);
        if (forumPost.isEdited()) {
            tvEdited.setVisibility(View.VISIBLE);
        }

        // date
        tvDate = view.findViewById(R.id.tv_date_forumPostRowAdapter);
        tvDate.setText(DateConverter.toString(forumPost.getPostDate()));

        // title
        tvTitle = view.findViewById(R.id.tv_title_forumPostRowAdapter);
        tvTitle.setText(forumPost.getTitle());

        // content
        tvContent = view.findViewById(R.id.tv_content_forumPostRowAdapter);
        if (isDetalied == true) {
            tvContent.setText(forumPost.getContent());
        } else {
            String smallContent = fitContent(forumPost.getContent(), MaxContentCharacters);
            tvContent.setText(smallContent);
        }

        // nr likes
        tvNrLikes = view.findViewById(R.id.tv_nrLikes_forumPostRowAdapter);
        tvNrLikes.setText(String.valueOf(forumPost.getNrLikes() - forumPost.getNrDislikes()));

        // nr comments
        tvNrComments = view.findViewById(R.id.tv_nrComments_forumPostRowAdapter);
        tvNrComments.setText(String.valueOf(forumPost.getNrComments()));

        // buton like
        btnLike = view.findViewById(R.id.imgBtn_like_forumPostRowAdapter);
        btnLike.setFocusable(false);

        // buton dislike
        btnDislike = view.findViewById(R.id.imgBtn_dislike_forumPostRowAdapter);
        btnDislike.setFocusable(false);

        // buton favourite
        btnFavourite = view.findViewById(R.id.imgBtn_favourite_forumPostRowAdapter);
        btnFavourite.setFocusable(false);

        // Anulare click cand este pe pagina de detalii forum post
        if (isDetalied) {
            btnLike.setFocusable(true);
            btnDislike.setFocusable(true);
            btnFavourite.setFocusable(true);
        }
    }


    // Taiere continut
    private String fitContent(String content, int maxCharacters) {
        if (content.length() > maxCharacters) {
            String smallContent = content.substring(0, maxCharacters) + "...";
            return smallContent;
        } else {
            return content;
        }
    }


    // Initializare comparatori
    private void initForumPostsComparators() {
        // Puncte
        comparatorPointsDesc = new Comparator<ForumPost>() {
            @Override
            public int compare(ForumPost o1, ForumPost o2) {
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


        // Comentarii
        comparatorCommentsDesc = new Comparator<ForumPost>() {
            @Override
            public int compare(ForumPost o1, ForumPost o2) {
                int comments1 = o1.getNrComments();
                int comments2 = o2.getNrComments();

                if (comments1 > comments2) {
                    return -1;
                } else if (comments1 < comments2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }


    // Ordonare dinaminca dupa apasare like dislike
    private void ordonareForumPostsDupaApasareLikeDislike() {
        if (ForumFragment.postsOrder.equals("ORDER BY nrLikes-nrDislikes DESC")) {
            Collections.sort(forumPostList, comparatorPointsDesc);
            notifyDataSetChanged();

        } else if (ForumFragment.postsOrder.equals("ORDER BY nrLikes-nrDislikes")) {
            Collections.sort(forumPostList, comparatorPointsDesc);
            Collections.reverse(forumPostList);
            notifyDataSetChanged();

        } else if (ForumFragment.postsOrder.equals("ORDER BY nrComments DESC")) {
            Collections.sort(forumPostList, comparatorCommentsDesc);
            notifyDataSetChanged();

        } else if (ForumFragment.postsOrder.equals("ORDER BY nrComments")) {
            Collections.sort(forumPostList, comparatorCommentsDesc);
            Collections.reverse(forumPostList);
            notifyDataSetChanged();
        }
    }


    // Initializare like dislike
    private void initLikeForum(LikeForum likeForum) {
        if (likeForum.isLiked()) {
            btnLike.setImageResource(R.drawable.ic_baseline_arrow_upward_24_blue);
        } else if (likeForum.isDisliked()) {
            btnDislike.setImageResource(R.drawable.ic_baseline_arrow_downward_24_red);
        }
    }


    // Initializare favorite
    private void initFavouritePosts(Integer id) {
        if (favouritePostsIdList.contains(id)) {
            btnFavourite.setImageResource(R.drawable.ic_baseline_favorite_24);
        } else {
            btnFavourite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }
    }


    // Click pe butonul post favorit
    @NotNull
    private View.OnClickListener onClickAddFavouriteForumPost(ForumPost forumPost) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavouriteForum favouriteForum = new FavouriteForum(currentUser.getId(), forumPost.getId());

                if (!favouritePostsIdList.contains(forumPost.getId())) {
                    // Adaugare in baza de date
                    favouriteForumPostService.insertNewFavouriteForum(favouriteForum,
                            callbackInsertNewFavouritePost(forumPost));
                } else {
                    // Stergere din baza de date
                    favouriteForumPostService.deleteFavouriteForumByFavouriteForum(favouriteForum,
                            callbackStergerePostForumFavorit(forumPost));
                }
            }
        };
    }


    // Callback inserare nou post favorit
    @NotNull
    private Callback<Integer> callbackInsertNewFavouritePost(ForumPost forumPost) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    favouritePostsIdList.add(forumPost.getId());
                    notifyDataSetChanged();
                } else {
                    Log.e(LogTag, context.getString(R.string.log_bad_insert_favourite_forumPostLvAdapter));
                }
            }
        };
    }

    // Callback stergere post forum favorit
    @NotNull
    private Callback<Integer> callbackStergerePostForumFavorit(ForumPost forumPost) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    favouritePostsIdList.remove((Integer) forumPost.getId());

                    // Eliminare din lista de favorite
                    if (!isDetalied && ForumFragment.forumPostCategory.equals("POSTARILE_FAVORITE")) {
                        forumPostList.remove(forumPost);
                    }

                    notifyDataSetChanged();
                } else {
                    Log.e(LogTag, context.getString(R.string.log_bad_delete_favourite_forumPostLvAdapter));
                }
            }
        };
    }


    // LIKE
    // On click like forum post
    private View.OnClickListener onClickLikeForumPost(ForumPost forumPost) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nrLikeuriSchimbate = 0;
                int nrDislikeuriSchimbate = 0;

                // Schimbari vizuale si in BD a like forum
                if (likeForumMap.containsKey(forumPost.getId())) {
                    LikeForum likeForumExistent = likeForumMap.get(forumPost.getId());

                    if (likeForumExistent.isLiked()) {
                        // Stergere like forum existent
                        likeForumService.deleteLikeForumByUserIdAndForumPostId(
                                likeForumExistent.getUserId(),
                                likeForumExistent.getForumPostId(),
                                callbackStergereLikeForum(likeForumExistent));
                        nrLikeuriSchimbate = -1;
                        nrDislikeuriSchimbate = 0;

                    } else if (likeForumExistent.isDisliked()) {
                        // Editare like forum existent
                        likeForumExistent.setLiked(true);
                        likeForumExistent.setDisliked(false);
                        likeForumService.updateLikeForumByLikeForum(likeForumExistent,
                                callbackEditareDislikeToLikeForum(likeForumExistent));
                        nrLikeuriSchimbate = 1;
                        nrDislikeuriSchimbate = -1;

                    }
                } else {
                    // Creare nou like forum
                    LikeForum likeForum = new LikeForum(currentUser.getId(), forumPost.getId(), true, false);
                    likeForumService.insertNewLikeForum(likeForum, callbackInsertNewLikeForum(likeForum, forumPost));
                    nrLikeuriSchimbate = 1;
                    nrDislikeuriSchimbate = 0;
                }

                // Schimbari vizuale si bd a nrLikeuri form si puncte utilizator
                forumPost.setNrLikes(forumPost.getNrLikes() + nrLikeuriSchimbate);
                forumPost.setNrDislikes(forumPost.getNrDislikes() + nrDislikeuriSchimbate);
                forumPostService.updatenrLikesnrDislikesByForumPost(forumPost,
                        callbackUpdatenrLikesnrDislikesByForumPost());

                // Schimbare puncte utilizator apreciat
                userService.updatePointsByUserIdAndPoints(forumPost.getUserId(),
                        nrLikeuriSchimbate, callbackUpdatePointsUser(forumPost));

                // Schimbare dinamica a ordinii
                ordonareForumPostsDupaApasareLikeDislike();
            }
        };
    }


    // Callback editare dislike to like forum
    private Callback<Integer> callbackEditareDislikeToLikeForum(LikeForum likeForumExistent) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    btnLike.setImageResource(R.drawable.ic_baseline_arrow_upward_24_blue);
                    likeForumMap.put(likeForumExistent.getForumPostId(), likeForumExistent);
                    notifyDataSetChanged();
                } else {
                    Log.e(LogTag, context.getString(R.string.log_update_dislike2like_forumPostLvAdapter));
                }
            }
        };
    }

    // Callback stergere like forum
    private Callback<Integer> callbackStergereLikeForum(LikeForum likeForumExistent) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    btnLike.setImageResource(R.drawable.ic_baseline_arrow_upward_24);
                    likeForumMap.remove(likeForumExistent.getForumPostId());
                    notifyDataSetChanged();
                } else {
                    Log.e(LogTag, context.getString(R.string.log_delete_like_forumPostLvAdapter));
                }
            }
        };
    }

    // Callback insert new like forum
    private Callback<Integer> callbackInsertNewLikeForum(LikeForum likeForum, ForumPost forumPost) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    btnLike.setImageResource(R.drawable.ic_baseline_arrow_upward_24_blue);
                    likeForumMap.put(forumPost.getId(), likeForum);
                    notifyDataSetChanged();
                } else {
                    Log.e(LogTag, context.getString(R.string.log_insertLike_forumPostLvAdapter));
                }
            }
        };
    }


    // DISLIKE
    // On click dislike forum post
    private View.OnClickListener onClickDislikeForumPost(ForumPost forumPost) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nrLikeuriSchimbate = 0;
                int nrDislikeuriSchimbate = 0;

                // Schimbari vizuale si in BD a like forum
                if (likeForumMap.containsKey(forumPost.getId())) {
                    LikeForum likeForumExistent = likeForumMap.get(forumPost.getId());

                    if (likeForumExistent.isDisliked()) {
                        // Stergere like forum existent
                        likeForumService.deleteLikeForumByUserIdAndForumPostId(
                                likeForumExistent.getUserId(),
                                likeForumExistent.getForumPostId(),
                                callbackDeleteDislikeForum(likeForumExistent));
                        nrLikeuriSchimbate = 0;
                        nrDislikeuriSchimbate = -1;

                    } else if (likeForumExistent.isLiked()) {
                        // Editare like forum existent
                        likeForumExistent.setLiked(false);
                        likeForumExistent.setDisliked(true);
                        likeForumService.updateLikeForumByLikeForum(likeForumExistent,
                                callbackUpdateLikeToDislikeForum(likeForumExistent));
                        nrLikeuriSchimbate = -1;
                        nrDislikeuriSchimbate = 1;

                    }
                } else {
                    // Creare nou dislike forum
                    LikeForum likeForum = new LikeForum(currentUser.getId(), forumPost.getId(), false, true);
                    likeForumService.insertNewLikeForum(likeForum, callbackInsertNewDislikeForum(likeForum, forumPost));
                    nrLikeuriSchimbate = 0;
                    nrDislikeuriSchimbate = 1;
                }

                // Schimbari vizuale si bd a nrLikeuri form si puncte utilizator
                forumPost.setNrLikes(forumPost.getNrLikes() + nrLikeuriSchimbate);
                forumPost.setNrDislikes(forumPost.getNrDislikes() + nrDislikeuriSchimbate);
                forumPostService.updatenrLikesnrDislikesByForumPost(forumPost,
                        callbackUpdatenrLikesnrDislikesByForumPost());

                // Schimbare puncte utilizator apreciat
                userService.updatePointsByUserIdAndPoints(forumPost.getUserId(),
                        nrLikeuriSchimbate, callbackUpdatePointsUser(forumPost));

                // Schimbare dinamica a ordinii
                ordonareForumPostsDupaApasareLikeDislike();
            }
        };
    }

    // Callback update like to dislike forum
    private Callback<Integer> callbackUpdateLikeToDislikeForum(LikeForum likeForumExistent) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    btnDislike.setImageResource(R.drawable.ic_baseline_arrow_downward_24_red);
                    likeForumMap.put(likeForumExistent.getForumPostId(), likeForumExistent);
                    notifyDataSetChanged();
                } else {
                    Log.e(LogTag, context.getString(R.string.log_update_like2dislike_forumPostLvAdapter));
                }
            }
        };
    }

    // Callback delete dislike forum
    private Callback<Integer> callbackDeleteDislikeForum(LikeForum likeForumExistent) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    btnDislike.setImageResource(R.drawable.ic_baseline_arrow_downward_24);
                    likeForumMap.remove(likeForumExistent.getForumPostId());
                    notifyDataSetChanged();
                } else {
                    Log.e(LogTag, context.getString(R.string.log_delete_dislike_forumPostLvAdapter));
                }
            }
        };
    }

    // Callback insert new dislike forum
    private Callback<Integer> callbackInsertNewDislikeForum(LikeForum likeForum, ForumPost forumPost) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    btnDislike.setImageResource(R.drawable.ic_baseline_arrow_downward_24_red);
                    likeForumMap.put(forumPost.getId(), likeForum);
                    notifyDataSetChanged();
                } else {
                    Log.e(LogTag, context.getString(R.string.log_insertDislike_forumPostLvAdapter));
                }
            }
        };
    }


    // Callback update nr likes si nr dislikes pt forum post by forum post
    private Callback<Integer> callbackUpdatenrLikesnrDislikesByForumPost() {
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
    private Callback<Integer> callbackUpdatePointsUser(ForumPost forumPost) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result != 1) {
                    Log.e(LogTag, context.getString(R.string.log_updatePoints_user_forumPostLvAdapter));
                }

                FishingTitleEnum.verificaSiActualieazaTitlu(forumPost.getUserId(), forumPost.getCreatorUsername());
            }
        };
    }

}
