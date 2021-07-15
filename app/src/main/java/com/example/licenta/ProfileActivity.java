package com.example.licenta;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.peste.Peste;
import com.example.licenta.clase.peste.PestiAdaptor;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.database.service.CommentForumService;
import com.example.licenta.database.service.FavouriteForumPostService;
import com.example.licenta.database.service.FishService;
import com.example.licenta.database.service.ForumPostService;
import com.example.licenta.database.service.LikeCommentService;
import com.example.licenta.database.service.LikeForumService;
import com.example.licenta.database.service.UserService;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.licenta.LogInActivity.PASSWORD_SP;
import static com.example.licenta.LogInActivity.REMEMBER_CHECKED;
import static com.example.licenta.LogInActivity.SHARED_PREF_FILE_NAME;
import static com.example.licenta.LogInActivity.USERNAME_SP;
import static com.example.licenta.VizualizatiPesti.FISH_ID_SP;

public class ProfileActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_MODIFY_ACCOUNT = 224;
    public static final String CURRENT_USER_KEY = "CURRENT_USER_KEY";
    public static final int REQUEST_CODE_ADD_PROFILE_IMAGE = 289;
    // Controale vizuale
    private TextView tvPageTitle;
    private ImageView imageViewProfilePicture;
    private TextView tvFishingTitle;
    private TextView tvNumberAnswers;
    private TextView tvNumberLikes;
    private TextView tvNumberPostsCreated;
    private TextView tvNumberPoints;
    private ListView lvBestFish;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvBestFish;
    private Button btnModificareCont;
    private Button btnStergereCont;


    // Utile
    private CurrentUser currentUser = CurrentUser.getInstance();
    private UserService userService = new UserService();
    private CommentForumService commentForumService = new CommentForumService();
    private LikeForumService likeForumService = new LikeForumService();
    private LikeCommentService likeCommentService = new LikeCommentService();
    private ForumPostService forumPostService = new ForumPostService();
    private FishService fishService = new FishService();
    private FavouriteForumPostService favouriteForumPostService = new FavouriteForumPostService();
    private static final String tagLog = "ProfileActivityLog";

    private int numarAprecieri = 0;
    private SharedPreferences sharedPreferences;
    private List<Peste> pesteList = new ArrayList<>();
    private byte[] imagineByte;
    private List<Integer> listaIdComentarii;
    private List<Integer> listaIdPostariForum;


    // On create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initializare componente
        initComponents();

        // Inlocuire campuri
        replaceFields();

        // Preluare poza
        getUserPhotoFromDatabase(currentUser.getId());

        // Adaugare adapter
        addAdapter();

        // Verificare existenta peste favorit
        getFavouriteFishFromSharedPreferences();

        // Functii butoane
        btnModificareCont.setOnClickListener(onClickModificare());
        btnStergereCont.setOnClickListener(onClickStergereCont());

        // Functie adaugare poza
        imageViewProfilePicture.setOnClickListener(onClickListenerUserPhoto());
    }


    // Metode
    // Initializare componente
    private void initComponents() {
        // Text view
        tvPageTitle = findViewById(R.id.tv_title_profile);
        tvFishingTitle = findViewById(R.id.tv_fisingTitle_profile);
        tvNumberAnswers = findViewById(R.id.tv_numberAnswers_profile);
        tvNumberLikes = findViewById(R.id.tv_numberLikes_profile);
        tvNumberPostsCreated = findViewById(R.id.tv_numberPostsCreated_profile);
        tvNumberPoints = findViewById(R.id.tv_numberPoints_profile);
        tvUsername = findViewById(R.id.tv_username_profile);
        tvEmail = findViewById(R.id.tv_email_profile);
        tvBestFish = findViewById(R.id.tv_bestFish_profile);

        // Image view
        imageViewProfilePicture = findViewById(R.id.imageView_profile);

        // Lista
        lvBestFish = findViewById(R.id.lv_bestFish_profile);

        // Butoane
        btnModificareCont = findViewById(R.id.btn_modificareCont_profile);
        btnStergereCont = findViewById(R.id.btn_stergereCont_profile);

        // Shared preferences
        sharedPreferences = getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE);
    }


    // On click stergere cont
    @NotNull
    private View.OnClickListener onClickStergereCont() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogBuilderForDeletingAccountOne();
            }
        };
    }


    // Functie on click modificare cont
    @NotNull
    private View.OnClickListener onClickModificare() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                intent.putExtra(CURRENT_USER_KEY, currentUser);
                startActivityForResult(intent, REQUEST_CODE_MODIFY_ACCOUNT);
            }
        };
    }


    // Get fish from shared preferences
    private void getFavouriteFishFromSharedPreferences() {
        int fishId = sharedPreferences.getInt(FISH_ID_SP, -1);

        if (fishId != -1) {
            fishService.getFavouriteFishByIdAndUserId(fishId, currentUser.getId(),
                    callbackGetFavouriteFish());
        } else {
            tvBestFish.setVisibility(View.GONE);
            lvBestFish.setVisibility(View.GONE);
        }
    }


    // Callback get favourite fish
    @NotNull
    private Callback<Peste> callbackGetFavouriteFish() {
        return new Callback<Peste>() {
            @Override
            public void runResultOnUiThread(Peste result) {
                if (result != null) {
                    pesteList.add(result);
                    notifyAdapter();
                } else {
                    tvBestFish.setVisibility(View.GONE);
                    lvBestFish.setVisibility(View.GONE);
                }
            }
        };
    }


    // Adaugare adapter
    private void addAdapter() {
        PestiAdaptor adaptor = new PestiAdaptor(getApplicationContext(), R.layout.listview_pesti,
                pesteList, getLayoutInflater());
        lvBestFish.setAdapter(adaptor);
    }


    // Notificare adapter
    private void notifyAdapter() {
        PestiAdaptor adapter = (PestiAdaptor) lvBestFish.getAdapter();
        adapter.notifyDataSetChanged();
    }


    // Inlocuire campuri
    private void replaceFields() {
        repaceFromAccount();
        replaceFromDataBase();
    }


    // Inlocuire instanta
    private void repaceFromAccount() {
        // Titlu
        String surname = currentUser.getSurname().substring(0, 1).toUpperCase() + currentUser.getSurname().substring(1).toLowerCase();
        String name = currentUser.getName().substring(0, 1).toUpperCase() + currentUser.getName().substring(1).toLowerCase();
        tvPageTitle.setText(getString(R.string.profile_title_replace, surname + " " + name));

        // Username
        tvUsername.setText(getString(R.string.profile_numeUtiliator_replace, currentUser.getUsername().split(":")[0]));

        // Email
        tvEmail.setText(getString(R.string.profile_email_replace, currentUser.getEmail()));

        // Fishing title
        tvFishingTitle.setText(currentUser.getFishingTitle().getLabel());
    }


    // Inlocuire din bd
    private void replaceFromDataBase() {
        // Comentarii create
        commentForumService.getAnswersCountByUserId(currentUser.getId(), callbackPreluareNumarComentarii());

        // Aprecieri acordate
        likeForumService.getForumLikesCountByUserId(currentUser.getId(), callbackPreluareNumarLikeuriForum());

        // Postari create
        forumPostService.getForumPostCountByUserId(currentUser.getId(), callbackPreluareNumarPostariCreate());

        // Numar puncte
        userService.getPointsForCurrentUser(currentUser.getId(), callbackPreluareNumarPuncte());
    }


    // Callback preluare numar puncte
    @NotNull
    private Callback<Integer> callbackPreluareNumarPuncte() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                currentUser.setPoints(result);
                tvNumberPoints.setText(getString(R.string.profile_puncteObtinute_replace, currentUser.getPoints()));
            }
        };
    }


    // Callback preluare numar comentarii
    @NotNull
    private Callback<Integer> callbackPreluareNumarComentarii() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                tvNumberAnswers.setText(getString(R.string.profile_raspunsuriAcordate_replace, result));
            }
        };
    }


    // Callback pentru preluare numar likeuri postari forum
    @NotNull
    private Callback<Integer> callbackPreluareNumarLikeuriForum() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                numarAprecieri += result;
                likeCommentService.getCommentLikesCountByUserId(currentUser.getId(), callbackPreluareNumarLikeuriComentarii());
            }
        };
    }


    // Callback pentru preluare numar likeuri comentarii
    @NotNull
    private Callback<Integer> callbackPreluareNumarLikeuriComentarii() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                numarAprecieri += result;
                tvNumberLikes.setText(getString(R.string.profile_aprecieriAcordate_replace, numarAprecieri));
            }
        };
    }


    // Callback preluare numar postari create
    @NotNull
    private Callback<Integer> callbackPreluareNumarPostariCreate() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                tvNumberPostsCreated.setText(getString(R.string.profile_postariCreate_replace, result));
            }
        };
    }


    // On activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Modificare cont
        if (requestCode == REQUEST_CODE_MODIFY_ACCOUNT && resultCode == RESULT_OK) {
            currentUser = CurrentUser.getInstance();
            replaceFields();
        }


        // Modificare poza
        if (requestCode == REQUEST_CODE_ADD_PROFILE_IMAGE && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                int currentBitmapWidth = selectedImage.getWidth();
                int currentBitmapHeight = selectedImage.getHeight();
                int ivWidth = imageViewProfilePicture.getWidth();
                int ivHeight = imageViewProfilePicture.getHeight();
                int newHeight = (int) Math.floor((double) currentBitmapHeight * ((double) ivWidth / (double) currentBitmapWidth));

                Bitmap newbitMap = Bitmap.createScaledBitmap(selectedImage, ivWidth, newHeight, true);
                imageViewProfilePicture.setImageBitmap(newbitMap);

                byte[] pozaByteArray = getBitmapAsByteArray(newbitMap);
                long lengthbmp = pozaByteArray.length;
                if (lengthbmp / 1024.0 / 1024.0 >= 2) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_dimensiuneImaginePreaMare),
                            Toast.LENGTH_LONG).show();
                } else {
                    Bitmap poza_scalata;
                    poza_scalata = Bitmap.createScaledBitmap(selectedImage, 280, 280, true);
                    imageViewProfilePicture.setImageBitmap(poza_scalata);
                    imagineByte = getBitmapAsByteArray(poza_scalata);

                    // Adaugare bd
                    userService.updateUSerPhotoById(currentUser.getId(), imagineByte, new Callback<Integer>() {
                        @Override
                        public void runResultOnUiThread(Integer result) {
                            if (result == 1) {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.toast_poza_actualizata_succes),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(tagLog, getString(R.string.log_eroarePoza));
                            }
                        }
                    });
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    // Transformarea pozei in byte array
    private byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        return outputStream.toByteArray();
    }


    // On click listener user photo
    @NotNull
    private View.OnClickListener onClickListenerUserPhoto() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(chooserIntent, REQUEST_CODE_ADD_PROFILE_IMAGE);
            }
        };
    }


    // Preluare poza din baza de date
    private void getUserPhotoFromDatabase(int userId) {
        userService.getUserPhotoById(userId, callbackPreluarePozaDinBazaDeDate());
    }


    // Callback preluare poza din baza de date
    @NotNull
    private Callback<byte[]> callbackPreluarePozaDinBazaDeDate() {
        return new Callback<byte[]>() {
            @Override
            public void runResultOnUiThread(byte[] result) {
                if (result != null) {
                    Bitmap userPhoto = BitmapFactory.decodeByteArray(result, 0, result.length);
                    imageViewProfilePicture.setImageBitmap(userPhoto);
                }
            }
        };
    }


    // Afisare dialog builder avertizare stergere cont 1
    private void showDialogBuilderForDeletingAccountOne() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Doriti sa va stergeti contul?");
        builder.setMessage("Stergerea contului este o actiune definitiva si nu se mai poate fi " +
                "revocata, doriti sa continuati?");


        // Anulare operatie
        builder.setNeutralButton("Anulare", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Stergere
        builder.setPositiveButton("Stergere", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showDialogBuilderForDeletingAccountTwo();
            }
        });


        builder.show();
    }


    // Afisare dialog builder avertizare stergere cont 2
    private void showDialogBuilderForDeletingAccountTwo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Doriti sa va stergeti contul?");
        builder.setMessage("De asemenea, vor fi sterse totate comentariile si postarile create de " +
                "dumneavostrat, doriti sa continuati?");


        // Anulare operatie
        builder.setNeutralButton("Anulare", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Stergere
        builder.setPositiveButton("Stergere", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                favouriteForumPostService.deleteFavouriteForumsByUserId(currentUser.getId(), callbackStergereFavorite());
            }
        });


        builder.show();
    }


    // Callback stergere favorite
    @NotNull
    private Callback<Integer> callbackStergereFavorite() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                likeCommentService.deleteLikeCommentByUserId(currentUser.getId(), callbackStergereLikeComment());
            }
        };
    }


    // Callback stergere likeuri comentarii
    @NotNull
    private Callback<Integer> callbackStergereLikeComment() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                likeForumService.deleteLikesForumByUserId(currentUser.getId(), callbackStergereLikeForum());
            }
        };
    }


    // Contoare folosite pentru stergere
    int contorComentarii = 0;
    int contorPostari = 0;

    // Callback stergere likeuri forum
    @NotNull
    private Callback<Integer> callbackStergereLikeForum() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {

                // Stergere comentarii
                commentForumService.getAllCommentsIdByUserId(currentUser.getId(), new Callback<List<Integer>>() {
                    @Override
                    public void runResultOnUiThread(List<Integer> result) {
                        listaIdComentarii = result;

                        if (result.size() == 0) {
                            // Stergere postari
                            forumPostService.getAllForumPostsIdByUserId(currentUser.getId(), new Callback<List<Integer>>() {
                                @Override
                                public void runResultOnUiThread(List<Integer> result) {
                                    listaIdPostariForum = result;

                                    if (result.size() == 0) {
                                        fishService.deleteFishByUserId(currentUser.getId(), callbackStergerePesti());
                                    }

                                    for (int idForum : listaIdPostariForum) {
                                        stergereTotalaPostari(idForum);
                                    }
                                }
                            });
                        }

                        for (int idComentariu : listaIdComentarii) {
                            stergereTotalaComentariu(idComentariu);
                        }


                    }
                });

            }
        };
    }


    // Stergere totala comentarii
    private void stergereTotalaComentariu(int idComentariu) {
        likeCommentService.deleteLikeCommentByCommentId(idComentariu, new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                commentForumService.getForumPostIdByCommentId(idComentariu, new Callback<Integer>() {
                    @Override
                    public void runResultOnUiThread(Integer result) {
                        forumPostService.updateNrCommentsByForumPostAndNrComments(result, -1, new Callback<Integer>() {
                            @Override
                            public void runResultOnUiThread(Integer result) {
                                commentForumService.deleteCommentByCommentId(idComentariu, new Callback<Integer>() {
                                    @Override
                                    public void runResultOnUiThread(Integer result) {
                                        contorComentarii++;

                                        // Verificare incepere stergere postari
                                        if (contorComentarii == listaIdComentarii.size()) {
                                            // Stergere postari
                                            forumPostService.getAllForumPostsIdByUserId(currentUser.getId(), new Callback<List<Integer>>() {
                                                @Override
                                                public void runResultOnUiThread(List<Integer> result) {
                                                    listaIdPostariForum = result;

                                                    if (result.size() == 0) {
                                                        fishService.deleteFishByUserId(currentUser.getId(), callbackStergerePesti());
                                                    }

                                                    for (int idForum : listaIdPostariForum) {
                                                        stergereTotalaPostari(idForum);
                                                    }
                                                }
                                            });
                                        }


                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }


    // Stegere totala postari
    private void stergereTotalaPostari(int idPostare) {
        likeForumService.deleteLikesForumByForumPostId(idPostare, new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                favouriteForumPostService.deleteFavouriteForumsByForumPostId(idPostare, new Callback<Integer>() {
                    @Override
                    public void runResultOnUiThread(Integer result) {
                        forumPostService.deleteForumPostByForumPostId(idPostare, new Callback<Integer>() {
                            @Override
                            public void runResultOnUiThread(Integer result) {
                                contorPostari++;

                                // Verificare continuare cu stergere
                                if (contorPostari == listaIdPostariForum.size()) {
                                    fishService.deleteFishByUserId(currentUser.getId(), callbackStergerePesti());
                                }

                            }
                        });
                    }
                });
            }
        });
    }


    // Callback stergere pesti
    @NotNull
    private Callback<Integer> callbackStergerePesti() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                userService.deleteUserByUserId(currentUser.getId(), callbackStergereUser());
            }
        };
    }


    // Callback stergere user
    @NotNull
    private Callback<Integer> callbackStergereUser() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                CurrentUser.delelteInstance();
                stergereUtilizatorDinSharedPreferences();
                finish();
            }
        };
    }


    // Stergere date utilizator din shared preferences
    private void stergereUtilizatorDinSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_SP, "");
        editor.putString(PASSWORD_SP, "");
        editor.putBoolean(REMEMBER_CHECKED, false);
        editor.apply();
    }

}