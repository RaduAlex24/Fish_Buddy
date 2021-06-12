package com.example.licenta.virtualAssistant.clase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.licenta.R;

import java.util.List;


public class MessageLvAdapter extends ArrayAdapter<Message> {

    // Atribute
    private Context context;
    private int resource;
    private List<Message> messageList;
    private LayoutInflater inflater;

    // Controale vizuale
    private TextView tvMessageSend;
    private TextView tvMessageReceived;


    // Constructor
    public MessageLvAdapter(@NonNull Context context, int resource, @NonNull List<Message> objects,
                            LayoutInflater layoutInflater) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        this.messageList = objects;
        this.inflater = layoutInflater;
    }


    // Get view
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);

        // Prelaure mesaj
        Message message = messageList.get(position);

        // Initializare componente
        initComponents(view, message);

        return view;
    }


    // Functii
    // Initializare componente
    private void initComponents(View view, Message message){
        // Preluare controale vizuale
        tvMessageSend = view.findViewById(R.id.tv_message_send_messageRowAdapter);
        tvMessageReceived = view.findViewById(R.id.tv_message_receive_messageRowAdapter);

        // Primit sau trimis
        if(message.isReceived()){
            tvMessageSend.setVisibility(View.GONE);
            tvMessageReceived.setVisibility(View.VISIBLE);

            // Continutul mesajului
            tvMessageReceived.setText(message.getContent());
        } else {
            tvMessageSend.setVisibility(View.VISIBLE);
            tvMessageReceived.setVisibility(View.GONE);

            // Continutul mesajului
            tvMessageSend.setText(message.getContent());
        }
    }

}
