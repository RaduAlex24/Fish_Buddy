package com.example.licenta.homeFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.licenta.CreateForumPostActivity;
import com.example.licenta.ProfileActivity;
import com.example.licenta.VizualizatiPesti;
import com.example.licenta.Profil;
import com.example.licenta.R;
import com.example.licenta.Setari;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.forum.ForumPost;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.database.service.ForumPostService;
import com.example.licenta.introTutorialSlider.IntroTutorialSlider;
import com.example.licenta.virtualAssistant.clase.KeyWordsChatBot;
import com.example.licenta.virtualAssistant.clase.MessageLvAdapter;
import com.example.licenta.virtualAssistant.dialogFlow.BotReply;
import com.example.licenta.virtualAssistant.clase.Message;
import com.example.licenta.virtualAssistant.dialogFlow.SendMessageInBackground;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VirtualAssistantFragment extends Fragment implements BotReply {

    // Cotroale vizuale
    private ListView lvMesaje;
    private TextInputEditText tietMesaj;
    private ImageButton btnSendMessage;

    // Utile
    List<Message> messageList = new ArrayList<>();
    private Message mesajInitial;
    private CurrentUser currentUser = CurrentUser.getInstance();
    private BottomNavigationView bottomNavigationView;
    private boolean modCautarePostari = false;
    private ForumPostService forumPostService = new ForumPostService();

    // Dialogflow
    private SessionsClient sessionsClient;
    private SessionName sessionName;
    private String uuid = UUID.randomUUID().toString();
    private static final String TagLog = "VirtualAssistantFrag";


    // Constructor
    public VirtualAssistantFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_virtual_assistant, container, false);

        // Initializare componente
        initComponents(view);

        // Initializare adapter
        initListViewAdapter();

        // Atasare functie buton trimite mesaj
        btnSendMessage.setOnClickListener(onClickSendMessageButton());

        return view;
    }


    // Metode
    // Initializare componente
    private void initComponents(View view) {
        lvMesaje = view.findViewById(R.id.lv_chat_asistentVirtual);
        tietMesaj = view.findViewById(R.id.tiet_addMessage_asistentVirtual);
        btnSendMessage = view.findViewById(R.id.imgBtn_addMessage_asistentVirtual);

        // Pregatire chat bot
        setUpBot();

        // Initializare mesaj
        mesajInitial = new Message(getString(R.string.mesaj_initial_asistentVirtual), true);
        messageList.add(mesajInitial);

        // Preluare bottomNavigationView
        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
    }


    // Initializare listview adapter
    private void initListViewAdapter() {
        MessageLvAdapter adapter = new MessageLvAdapter(getContext(),
                R.layout.listview_row_message_virtualassistent,
                messageList,
                getLayoutInflater());
        lvMesaje.setAdapter(adapter);
    }


    // Notificare adapter de schimbare
    private void notifyInternalAdapter() {
        ArrayAdapter adapter = (ArrayAdapter) lvMesaje.getAdapter();
        adapter.notifyDataSetChanged();
//        lvMesaje.setSelection(messageList.size() - 1);
        lvMesaje.smoothScrollToPosition(messageList.size() - 1);
    }


    // Chat bot
    // Set up chat bot
    private void setUpBot() {
        try {
            InputStream stream = this.getResources().openRawResource(R.raw.credential);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(
                    FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            sessionName = SessionName.of(projectId, uuid);

            Log.d(TagLog, "projectId : " + projectId);
        } catch (Exception e) {
            Log.d(TagLog, "setUpBot: " + e.getMessage());
        } finally {
            //sessionsClient.close();
        }
    }


//    @Override
//    public void onDestroy() {
//        Toast.makeText(getContext(), "ssss", Toast.LENGTH_SHORT).show();
//        sessionsClient.close();
//        super.onDestroy();
//    }

    // Trimitere de mesaj
    private void sendMessageToBot(String message) {
        QueryInput input = QueryInput.newBuilder()
                .setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build();
        new SendMessageInBackground(this, sessionName, sessionsClient, input).execute();
    }


    // AICI VOR FI PRELUCRARI PE RASPUNSUL BOTULUI
    // Primire de mesaj prin callback
    @Override
    public void callback(DetectIntentResponse returnResponse) {
        if (returnResponse != null) {
            // Preluare mesaj chatbot
            String botReply = returnResponse.getQueryResult().getFulfillmentText();

            // Verificare existenta
            if (!botReply.isEmpty()) {
                // Prelucrari
                botReply = efectuareCereriUtilizator(botReply);

                // Adaugare in lista
                messageList.add(new Message(botReply, true));
                notifyInternalAdapter();
            } else {
                Toast.makeText(getContext(),
                        getString(R.string.toast_errNecunoscuta_AsistentVirtual),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(),
                    getString(R.string.toast_errChatBotTimeOut_AsistentVirtual),
                    Toast.LENGTH_LONG).show();
        }
    }


    // Efectuare cereri utilizator
    private String efectuareCereriUtilizator(String botReply) {
        // Redirectionari
        // Vizualizare pagina forum
        if (botReply.toUpperCase().contains(KeyWordsChatBot.VIZUALIZARE_POSTARI_FORUM.getLabel())) {
            redirectionareVizualizareForumPosts();
        }

        // Vizualizare harti
        if (botReply.toUpperCase().contains(KeyWordsChatBot.VIZUALIZARE_HARTI.getLabel())) {
            redirectionareVizualizareHarti();

        }

        // Creare interventie forum
        if (botReply.toUpperCase().contains(KeyWordsChatBot.CREARE_INTERVENTIE.getLabel())) {
            redirectionareCreareInterventie();
        }

        // Vizualizare cont
        if (botReply.toUpperCase().contains(KeyWordsChatBot.VIZUALIZARE_CONT.getLabel())) {
            redirectionareVizualizareCont();
        }

        // Vizualizare setari
        if (botReply.toUpperCase().contains(KeyWordsChatBot.VIZUALIZARE_SETARI.getLabel())) {
            redirectionareVizualizareSetari();
        }

        // Vizualizare lista pesti prinsi
        if (botReply.toUpperCase().contains(KeyWordsChatBot.VIZUALIZARE_LISTA_PESTI.getLabel())) {
            redirectionareVizualizareListaPesti();
        }

        // Incepere cautare forum post
        if (botReply.toUpperCase().contains(KeyWordsChatBot.CAUTARE.getLabel())) {
            modCautarePostari = true;
        }

        // Vizualizare tutorial
        if (botReply.toUpperCase().contains(KeyWordsChatBot.TUTORIAL.getLabel())) {
            redirectionareTutorial();
        }

        // Inlocuire nume
        if (botReply.toUpperCase().contains(KeyWordsChatBot.INLOCCUIESTE_NUME.getLabel())) {
            botReply = botReply.replace(KeyWordsChatBot.INLOCCUIESTE_NUME.getLabel(), currentUser.getName());
        }


        return botReply;
    }


    // Redirectionari
    // Redirectionare forum
    private void redirectionareVizualizareForumPosts() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                closeKeyboard();
                bottomNavigationView.setSelectedItemId(R.id.nav_forum);
            }
        }, 5000);
    }


    // Redirectionare harti
    private void redirectionareVizualizareHarti() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                closeKeyboard();
                bottomNavigationView.setSelectedItemId(R.id.nav_map);
            }
        }, 5000);
    }


    // Creare interventie
    private void redirectionareCreareInterventie() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getContext(), CreateForumPostActivity.class);
                startActivity(intent);
            }
        }, 5000);
    }


    // Redirectionare cont personal
    private void redirectionareVizualizareCont() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        }, 5000);
    }


    // Redirectionare setari
    private void redirectionareVizualizareSetari() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getContext(), Setari.class);
                startActivity(intent);
            }
        }, 5000);
    }


    // Redirectionare lista pesti
    private void redirectionareVizualizareListaPesti() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getContext(), VizualizatiPesti.class);
                startActivity(intent);
            }
        }, 5000);
    }


    // Functie on click pentru trimitere mesaj
    @NotNull
    private View.OnClickListener onClickSendMessageButton() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = tietMesaj.getText().toString().trim();

                if (!message.isEmpty()) {
                    messageList.add(new Message(message, false));
                    tietMesaj.setText("");
                    notifyInternalAdapter();

                    // Verificare cautare
                    if (!modCautarePostari) {
                        sendMessageToBot(message);
                    } else {
                        message = formatareCuvintePentruCautare(message);
                        forumPostService.getAllForumPostsBySearchWords("ORDER BY nrLikes-nrDislikes DESC",
                                message, callbackGetForumPostsBySearchWords());
                    }
                } else {
                    Toast.makeText(getContext(),
                            getString(R.string.toast_errMesajGol_AisistentVirtual),
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
    }


    // Callback get forum post by search words
    // De bagat in strings !!!
    @NotNull
    private Callback<List<ForumPost>> callbackGetForumPostsBySearchWords() {
        return new Callback<List<ForumPost>>() {
            @Override
            public void runResultOnUiThread(List<ForumPost> result) {
                String rezultatString;
                boolean existaPostari = false;

                if (result.size() != 0) {
                    existaPostari = true;
                    rezultatString = "Gata cautarea pescare, am gasit " + result.size()
                            + " posturi ce contin acele cuvinte! Vei fi redirectionat imediat la ele.";
                } else {
                    rezultatString = "Nu am gasit nici o postare ce sa contina acele cuvinte :(";
                }

                Message message = new Message(rezultatString, true);
                messageList.add(message);
                modCautarePostari = false;

                // Notificare intarziata a adapterului pentru a face scroll
                notificareIntarziataAdapter();

                // Redirectionare
                if (existaPostari) {
                    redirectionareForumFragmentDupaCautare(result);
                }

            }
        };
    }


    // Redirectioare tutorial
    private void redirectionareTutorial() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getContext(), IntroTutorialSlider.class);
                startActivity(intent);
            }
        }, 5000);
    }


    // Redirectioare forum fragment dupa cautare
    private void redirectionareForumFragmentDupaCautare(List<ForumPost> result) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                closeKeyboard();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        ForumFragment.newInstance(result)).commit();
            }
        }, 5000);
    }


    // Notificare intarziata adapter
    private void notificareIntarziataAdapter() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyInternalAdapter();
            }
        }, 500);
    }


    // Functie formatare cuvinte pentru cautare
    private String formatareCuvintePentruCautare(String message) {
        return "%" + message.replace(" ", "%") + "%";
    }


    // Inchidere tastatura
    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(getContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}