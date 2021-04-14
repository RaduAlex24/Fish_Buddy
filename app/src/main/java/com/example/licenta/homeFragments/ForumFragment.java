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
import com.example.licenta.R;
import com.example.licenta.SignUpActivity;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.forum.CategoryForum;
import com.example.licenta.clase.forum.ForumPost;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.database.service.ForumPostService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class ForumFragment extends Fragment {

    public static final int REQUEST_CODE_CREATE_FORUM_POST = 201;
    private Spinner spinnerCategory;
    private FloatingActionButton fabAddPost;
    private ListView lvForum;

    private static final String tagLog = "FragmentForum";
    private CurrentUser currentUser = CurrentUser.getInstance();

    private ForumPostService forumPostService = new ForumPostService();
    private List<ForumPost> forumPostList = new ArrayList<>();


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

        // Initializare componente
        initComponents(view);

        // Initializare listview adapter
        initListViewAdapter();

        // Initializare adapter spinner category
        initSpinnerCategory();

        // Preluare initiala de posturi
        //initialGetAllForumPosts();

        // Evenimentru pentru schimbarea categoriei
        spinnerCategory.setOnItemSelectedListener(onItemSelectedListenerSpinner());

        // Eveniment pt click pe butonul de creare interventie forum
        fabAddPost.setOnClickListener(onClickCreateForumPost());

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


    // Init spinner cu enumerare
    private void initSpinnerCategory() {
        List<String> categoryList = new ArrayList<>();
        categoryList.add("General");

        for (CategoryForum category : CategoryForum.values()) {
            categoryList.add(category.getLabel());
        }

        categoryList.add("Postarile mele");

        ArrayAdapter adapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_dropdown_item_1line, categoryList);
        spinnerCategory.setAdapter(adapter);
    }


    // Initializare listview adapter
    private void initListViewAdapter() {
        ArrayAdapter adapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_list_item_1, forumPostList);
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
                    default:
                        category = "";
                }

                if (!(category.equals("GENERAL") || category.equals("POSTARILE_MELE"))) {
                    forumPostService.getAllForumPostsByCategory(category, callbackGetAllByCategory());
                } else if(category.equals("GENERAL")) {
                    initialGetAllForumPosts();
                } else if(category.equals("POSTARILE_MELE")){
                    forumPostService.getAllForumPostsByUserId(currentUser.getId(), callbackGetForumPostsByUserId());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
    }
}