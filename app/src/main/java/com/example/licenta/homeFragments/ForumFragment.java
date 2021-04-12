package com.example.licenta.homeFragments;

import android.content.Intent;
import android.os.Bundle;

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

import com.example.licenta.R;
import com.example.licenta.SignUpActivity;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.forum.CategoryForum;
import com.example.licenta.clase.forum.ForumPost;
import com.example.licenta.database.service.ForumPostService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class ForumFragment extends Fragment {

    private Spinner spinnerCategory;
    private FloatingActionButton fabAddPost;
    private ListView lvForum;

    private static final String tagLog = "FragmentForum";

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


    // Spinner on item selected
    private AdapterView.OnItemSelectedListener onItemSelectedListenerSpinner() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Switch pe enum
                String category = "";
                switch (position){
                    case 0:
                        category = "";
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
                    default:
                        category = "";
                }

                if(!category.equals("")) {
                    forumPostService.getAllForumPostsByCategory(category, callbackGetAllByCategory());
                }
                else{
                    initialGetAllForumPosts();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

}