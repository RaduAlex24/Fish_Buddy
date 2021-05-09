package com.example.licenta.homeFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.licenta.CreateForumPostActivity;
import com.example.licenta.ForumPostDetailedActivity;
import com.example.licenta.R;
import com.example.licenta.SignUpActivity;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.forum.CategoryForum;
import com.example.licenta.clase.forum.ForumPost;
import com.example.licenta.clase.forum.ForumPostLvAdapter;
import com.example.licenta.clase.forum.LikeForum;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.database.service.FavouriteForumPostService;
import com.example.licenta.database.service.ForumPostService;
import com.example.licenta.database.service.LikeForumService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class ForumFragment extends Fragment {

    // TO DO ORDINEA FORUMURILOR SI LIKEURILOR
    public static final int REQUEST_CODE_CREATE_FORUM_POST = 201;
    public static final int REQUEST_CODE_FORUM_POST_DETAILED = 202;
    public static final String FORUM_POST_KEY = "FORUM_POST_KEY";
    public static final String LIKE_FORUM_MAP_KEY = "LIKE_FORUM_MAP_KEY";
    public static final String FAVOURITE_POSTS_ID_LIST_KEY = "FAVOURITE_POSTS_ID_LIST_KEY";
    private Spinner spinnerCategory;
    private FloatingActionButton fabAddPost;
    private ListView lvForum;

    private static final String tagLog = "FragmentForum";
    private CurrentUser currentUser = CurrentUser.getInstance();

    private ForumPostService forumPostService = new ForumPostService();
    private List<ForumPost> forumPostList = new ArrayList<>();

    private LikeForumService likeForumService = new LikeForumService();
    private Map<Integer, LikeForum> likeForumMap = new HashMap<>();

    private FavouriteForumPostService favouriteForumPostService = new FavouriteForumPostService();
    public List<Integer> favouritePostsIdList = new ArrayList<>();


    public ForumFragment() {
        // Required empty public constructor
    }

//    public static ForumFragment newInstance(String param1, String param2) {
//        ForumFragment fragment = new ForumFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forum, container, false);

        // Preluare like forum posts
        initLikForumMap(0);

        // Preluare favourite forum posts
        initFavouritePostsList();

        // Initializare componente
        initComponents(view);

        // Initializare adapter spinner category
        initSpinnerCategory();

        // Preluare initiala de posturi
        //initialGetAllForumPosts();


        // Evenimentru pentru schimbarea categoriei
        spinnerCategory.setOnItemSelectedListener(onItemSelectedListenerSpinner());

        // Eveniment pt click pe butonul de creare interventie forum
        fabAddPost.setOnClickListener(onClickCreateForumPost());

        // Adaugare eveniment click pe obiectele din listview
        lvForum.setOnItemClickListener(onClickListViewItem());

        return view;
    }


    // Metode
    // Init components
    private void initComponents(View view) {
        // Initializare controale vizuale
        spinnerCategory = view.findViewById(R.id.spinner_category_forumPost);
        fabAddPost = view.findViewById(R.id.fab_createPost_forumFragment);
        lvForum = view.findViewById(R.id.lv_forumFragment);
    }


    // Preluare like forum posts si initializare likeForumMap
    private void initLikForumMap(int selection) {
        likeForumService.getLikeForumByUserId(currentUser.getId(), callbackGetLikeFroumByUserId(selection));
    }

    // Callback preluare like forum posts si initializare likeForumMap
    private Callback<Map<Integer, LikeForum>> callbackGetLikeFroumByUserId(int selection) {
        return new Callback<Map<Integer, LikeForum>>() {
            @Override
            public void runResultOnUiThread(Map<Integer, LikeForum> result) {
                // Primire like forum
                likeForumMap = result;
                // Initializare listview adapter
                initListViewAdapter();
                // Saritura la postarea curenta
                lvForum.setSelection(selection);
            }
        };
    }


    // Preluare favourite forum posts
    private void initFavouritePostsList() {
        favouriteForumPostService.getFavouritePostsIdListByUserId(currentUser.getId(), callbackGetFavouritePostsId());
    }

    // Callback pentru preluarea listei de id uri ale posturilor favorite
    @NotNull
    private Callback<List<Integer>> callbackGetFavouritePostsId() {
        return new Callback<List<Integer>>() {
            @Override
            public void runResultOnUiThread(List<Integer> result) {
                favouritePostsIdList.clear();
                favouritePostsIdList.addAll(result);
            }
        };
    }


    // Init spinner cu enumerare
    private void initSpinnerCategory() {
        List<String> categoryList = new ArrayList<>();
        categoryList.add("General");

        for (CategoryForum category : CategoryForum.values()) {
            categoryList.add(category.getLabel());
        }

        categoryList.add("Postarile mele");
        categoryList.add("Postari Favorite");

        ArrayAdapter adapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_dropdown_item_1line, categoryList);
        spinnerCategory.setAdapter(adapter);
    }


    // Initializare listview adapter
    private void initListViewAdapter() {
        ForumPostLvAdapter adapter = new ForumPostLvAdapter(getContext(),
                R.layout.listview_row_forum_post, forumPostList,
                getLayoutInflater(), likeForumMap, currentUser, favouritePostsIdList);
        lvForum.setAdapter(adapter);
    }

    // Notificare adapter de schimbare
    private void notifyInternalAdapter() {
        ArrayAdapter adapter = (ArrayAdapter) lvForum.getAdapter();
        adapter.notifyDataSetChanged();
    }


    // Preluare initiala a posturilor de pe forum
    private void initialGetAllForumPosts() {
        forumPostService.getAllForumPosts(callbackGetAllForumPostInitialy());
    }

    // Callback gett all forum posts initailly
    private Callback<List<ForumPost>> callbackGetAllForumPostInitialy() {
        return new Callback<List<ForumPost>>() {
            @Override
            public void runResultOnUiThread(List<ForumPost> result) {
                forumPostList.clear();
                forumPostList.addAll(result);
                notifyInternalAdapter();
            }
        };
    }


    // On clcikpt fab add new forum post
    private View.OnClickListener onClickCreateForumPost() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateForumPostActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CREATE_FORUM_POST);
            }
        };
    }


    // On click list view item - forum posts
    private AdapterView.OnItemClickListener onClickListViewItem() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(),
                        "Click pe forum postul " + forumPostList.get(position).getId(),
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(), ForumPostDetailedActivity.class);
                intent.putExtra(FORUM_POST_KEY, forumPostList.get(position));
                intent.putExtra(LIKE_FORUM_MAP_KEY, (Serializable) likeForumMap);
                intent.putExtra(FAVOURITE_POSTS_ID_LIST_KEY, (Serializable) favouritePostsIdList);

                startActivityForResult(intent, REQUEST_CODE_FORUM_POST_DETAILED);
            }
        };
    }


    // Spinner on item selected
    private AdapterView.OnItemSelectedListener onItemSelectedListenerSpinner() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Switch pe enum
                String category = "";
                switch (position) {
                    case 0:
                        category = "GENERAL";
                        break;
                    case 1:
                        category = "SFATURI";
                        break;
                    case 2:
                        category = "PESTI";
                        break;
                    case 3:
                        category = "ECHIPAMENTE";
                        break;
                    case 4:
                        category = "INCEPATORI";
                        break;
                    case 5:
                        category = "GLUME";
                        break;
                    case 6:
                        category = "POSTARILE_MELE";
                        break;
                    case 7:
                        category = "POSTARILE_FAVORITE";
                        break;
                    default:
                        category = "";
                }

                if (!(category.equals("GENERAL") || category.equals("POSTARILE_MELE") || category.equals("POSTARILE_FAVORITE"))) {
                    forumPostService.getAllForumPostsByCategory(category, callbackGetAllByCategory());
                } else if (category.equals("GENERAL")) {
                    initialGetAllForumPosts();
                } else if (category.equals("POSTARILE_MELE")) {
                    forumPostService.getAllForumPostsByUserId(currentUser.getId(), callbackGetForumPostsByUserId());

                } else if (category.equals("POSTARILE_FAVORITE")) {
                    if (favouritePostsIdList.size() == 0) {
                        Toast.makeText(getContext(), "Nimic aici", Toast.LENGTH_SHORT).show();
                        forumPostList.clear();
                    } else {
                        forumPostService.getFavouriteForumPostsByIdList(favouritePostsIdList, callbackGetFavouriteForumPosts());
                    }
                }

                lvForum.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }


    // Callback pt get all favourite posts
    @NotNull
    private Callback<List<ForumPost>> callbackGetFavouriteForumPosts() {
        return new Callback<List<ForumPost>>() {
            @Override
            public void runResultOnUiThread(List<ForumPost> result) {
                forumPostList.clear();
                forumPostList.addAll(result);
                notifyInternalAdapter();
            }
        };
    }

    // Callback pt get all post by currentuser id
    private Callback<List<ForumPost>> callbackGetForumPostsByUserId() {
        return new Callback<List<ForumPost>>() {
            @Override
            public void runResultOnUiThread(List<ForumPost> result) {
                forumPostList.clear();
                forumPostList.addAll(result);
                notifyInternalAdapter();
            }
        };
    }

    // Callback get all by category
    private Callback<List<ForumPost>> callbackGetAllByCategory() {
        return new Callback<List<ForumPost>>() {
            @Override
            public void runResultOnUiThread(List<ForumPost> result) {
                forumPostList.clear();
                forumPostList.addAll(result);
                notifyInternalAdapter();
            }
        };
    }


    // On activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Post forum nou
        if (requestCode == REQUEST_CODE_CREATE_FORUM_POST && resultCode == RESULT_OK && data != null) {
            ForumPost forumPost = (ForumPost) data.getSerializableExtra(CreateForumPostActivity.NEW_FORUM_POST_KEY);
            spinnerCategory.setSelection(0);
            forumPostList.add(forumPost);
            notifyInternalAdapter();
        }

        // Intoarcere de la vizitarea unui post detaliat
        if (requestCode == REQUEST_CODE_FORUM_POST_DETAILED && resultCode == RESULT_CANCELED && data != null) {
            List<ForumPost> forumPostListSingular = (List<ForumPost>) data.
                    getSerializableExtra(ForumPostDetailedActivity.FORUM_POST_MODIFICAT_LIKE_DISLIKE_KEY);

            // Modificare puncte forum post si set selection pentru acesta
            ForumPost forumPostNou = forumPostListSingular.get(0);
            int nrSelection = -2;
            boolean notFound = true;
            for (ForumPost forumPost : forumPostList) {
                if (notFound) {
                    nrSelection++;
                }
                if (forumPost.getId() == forumPostNou.getId()) {
                    forumPost.setNrLikes(forumPostNou.getNrLikes());
                    forumPost.setNrDislikes(forumPostNou.getNrDislikes());
                    notFound = false;
                }
            }

            // Preluare din noi din BD a postarilor favorite si a like-urilor
            initFavouritePostsList();
            initLikForumMap(nrSelection);

        }


    }
}