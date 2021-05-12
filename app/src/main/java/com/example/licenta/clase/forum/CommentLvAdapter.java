package com.example.licenta.clase.forum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.licenta.R;
import com.example.licenta.util.dateUtils.DateConverter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommentLvAdapter extends ArrayAdapter<CommentForum> {


    // Atribute
    private Context context;
    private List<CommentForum> commentList;
    private LayoutInflater inflater;
    private int resource;


    // Controale vizuale
    private ImageButton btnLike;
    private ImageButton btnDislike;
    private TextView tvPoints;
    private TextView tvCreatorUsername;
    private TextView tvPostDate;
    private TextView tvContent;


    // Constructor
    public CommentLvAdapter(@NonNull Context context, int resource, @NonNull List<CommentForum> objects,
                            LayoutInflater layoutInflater) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        this.commentList = objects;
        this.inflater = layoutInflater;
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

        // Adaugare functii butoane
        btnLike.setOnClickListener(onClickLikeComment(commentForum));

        btnDislike.setOnClickListener(onClickDislikeComment(commentForum));

        return view;
    }



    // Functii
    // Initializare componente
    private void initComponents(View view, CommentForum commentForum){
        // creator username
        tvCreatorUsername = view.findViewById(R.id.tv_creatorUsername_commentRowAdapter);
        tvCreatorUsername.setText(commentForum.getCreatorUsername());

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



    // Functii butoane
    @NotNull
    private View.OnClickListener onClickDislikeComment(CommentForum commentForum) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Dislike la id: " + commentForum.getId(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    @NotNull
    private View.OnClickListener onClickLikeComment(CommentForum commentForum) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Like la id: " + commentForum.getId(), Toast.LENGTH_SHORT).show();
            }
        };
    }


}
