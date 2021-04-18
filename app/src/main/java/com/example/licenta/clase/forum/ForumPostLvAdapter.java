package com.example.licenta.clase.forum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.licenta.R;
import com.example.licenta.util.dateUtils.DateConverter;

import java.util.List;

public class ForumPostLvAdapter extends ArrayAdapter<ForumPost> {

    // Atribute
    private Context context;
    private List<ForumPost> forumPostList;
    private LayoutInflater inflater;
    private int resource;


    // Constructor
    public ForumPostLvAdapter(@NonNull Context context, int resource, @NonNull List<ForumPost> objects,
                              LayoutInflater layoutInflater) {
        super(context, resource, objects);

        this.context = context;
        this.forumPostList = objects;
        this.inflater = layoutInflater;
        this.resource = resource;
    }


    // GetView
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        ForumPost forumPost = forumPostList.get(position);

        // user
        TextView tvUser = view.findViewById(R.id.tv_user_forumPostRowAdapter);
        tvUser.setText(forumPost.getCreatorUsername());

        // date
        TextView tvDate = view.findViewById(R.id.tv_date_forumPostRowAdapter);
        tvDate.setText(DateConverter.toString(forumPost.getPostDate()));

        // title
        TextView tvTitle = view.findViewById(R.id.tv_title_forumPostRowAdapter);
        tvTitle.setText(forumPost.getTitle());

        // content
        TextView tvContent = view.findViewById(R.id.tv_content_forumPostRowAdapter);
        tvContent.setText(forumPost.getContent());

        // nr likes
        TextView tvNrLikes = view.findViewById(R.id.tv_nrLikes_forumPostRowAdapter);
        tvNrLikes.setText(String.valueOf(forumPost.getNrLikes() - forumPost.getNrDislikes()));

        // nr comments
        TextView tvNrComments = view.findViewById(R.id.tv_nrComments_forumPostRowAdapter);
        tvNrComments.setText(String.valueOf(forumPost.getNrComments()));


        // Butoane
        // Initializare
        ImageButton btnLike = view.findViewById(R.id.imgBtn_like_forumPostRowAdapter);
        ImageButton btnDislike = view.findViewById(R.id.imgBtn_dislike_forumPostRowAdapter);
        btnLike.setFocusable(false);
        btnDislike.setFocusable(false);

        // Functii
        btnLike.setOnClickListener(onClickLikeForumPost(forumPost));
        btnDislike.setOnClickListener(onClickDislikeForumPost(forumPost));

        return view;
    }



    // Functii
    // On click like forum post
    private View.OnClickListener onClickDislikeForumPost(ForumPost forumPost) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Dislike la postul " + forumPost.getId(), Toast.LENGTH_SHORT).show();
            }
        };
    }


    // On click dislike forum post
    private View.OnClickListener onClickLikeForumPost(ForumPost forumPost) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Like la postul " + forumPost.getId(), Toast.LENGTH_SHORT).show();
            }
        };
    }



}
