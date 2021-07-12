package com.example.licenta;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.licenta.asyncTask.AsyncTaskRunner;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.forum.CategoryForum;
import com.example.licenta.clase.forum.CommentForum;
import com.example.licenta.clase.forum.ForumPost;
import com.example.licenta.clase.peste.Peste;
import com.example.licenta.clase.peste.PestiAdaptor;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.database.ConexiuneBD;
import com.example.licenta.database.service.FishService;
import com.example.licenta.util.dateUtils.DateConverter;

import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import static com.example.licenta.LogInActivity.SHARED_PREF_FILE_NAME;

public class VizualizatiPesti extends AppCompatActivity {

    public static final String FISH_ID_SP = "FISH_ID_SP";
    private ListView lvPesti;
    private List<Peste> pesteList = new ArrayList<>();
    private Button adaugaPeste;
    private int ReqPeste = 1060;
    private ConexiuneBD conexiuneBD = ConexiuneBD.getInstance();
    private CurrentUser currentUser = CurrentUser.getInstance();
    private AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();

    // Pentru adaugare la favorit un peste
    private FishService fishService = new FishService();
    private SharedPreferences sharedPreferences;
    private String LogTag = "LogTagListaPesti";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pesti);
        initcomponents();
        addAdapter();
        adaugaPeste.setOnClickListener(goToAdaugarePeste());
        fishService.getAllFishById(currentUser.getId(), callbackGetPestiById());
        //getAllFishById(currentUser.getId(), callbackGetPestiById());

        // Adaugare la favorite peste
        lvPesti.setOnItemLongClickListener(onLongClickSelectFish());
    }

    @NotNull
    private View.OnClickListener goToAdaugarePeste() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdaugaPeste.class);
                startActivityForResult(intent, ReqPeste);
            }
        };
    }

    private void initcomponents() {
        lvPesti = findViewById(R.id.lv_peste);
        adaugaPeste = findViewById(R.id.button_adaugare_peste);

        sharedPreferences = getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ReqPeste && resultCode == RESULT_OK && data != null) {
            Peste peste = (Peste) data.getSerializableExtra(AdaugaPeste.Peste_key);
            if (peste != null) {
                pesteList.add(peste);
                notifyAdapter();
                fishService.getAllFishById(currentUser.getId(), callbackGetPestiById());
            }
        }
    }

    private void addAdapter() {
        PestiAdaptor adaptor = new PestiAdaptor(getApplicationContext(), R.layout.listview_pesti,
                pesteList, getLayoutInflater());
        lvPesti.setAdapter(adaptor);
    }

    private void notifyAdapter() {
        PestiAdaptor adapter = (PestiAdaptor) lvPesti.getAdapter();
        adapter.notifyDataSetChanged();
    }

    public void getAllFishById(int id, Callback<List<Peste>> callback) {
        Callable<List<Peste>> callable = new Callable<List<Peste>>() {
            @Override
            public List<Peste> call() throws Exception {
                List<Peste> pesteListDinBd = new ArrayList<>();
                String sql = "SELECT * FROM " + "FISH where USERID = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    String specii = resultSet.getString(3);
                    int lungime = resultSet.getInt(4);
                    int greutate = resultSet.getInt(5);
                    String dataS = resultSet.getString(6);
                    String locatie = resultSet.getString(7);
                    byte[] imgByte = resultSet.getBytes(8);

                    Date date = DateConverter.toDate(dataS);
                    Peste peste = new Peste(greutate, lungime, specii, locatie, date, imgByte);
                    pesteListDinBd.add(peste);
                }
                statement.close();
                resultSet.close();
                return pesteListDinBd;
            }
        };
        asyncTaskRunner.executeAsync(callable, callback);
    }

    private Callback<List<Peste>> callbackGetPestiById() {
        return new Callback<List<Peste>>() {
            @Override
            public void runResultOnUiThread(List<Peste> result) {
                pesteList.clear();
                pesteList.addAll(result);
                notifyAdapter();
            }
        };
    }


    // Adaugare peste la favorit
    // On long click fish list item
    @NotNull
    private AdapterView.OnItemLongClickListener onLongClickSelectFish() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Peste peste = pesteList.get(position);
                showDialogBuilderForFish(peste);

                return true;
            }
        };
    }


    // Afisare dialog builder pentru a sterge sau a adauga la favorte un peste
    private void showDialogBuilderForFish(Peste pesteSelectat) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Optiuni pentru peste");
        builder.setMessage("Doriti sa stergeri sau sa adaugati ca favorit pestele?");


        // Anulare operatie
        builder.setNeutralButton("Anulare", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Adaugre peste la favorite
        builder.setPositiveButton("Favorit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveFishIdInSharedPreferences(pesteSelectat.getId());
                Toast.makeText(getApplicationContext(), getString(R.string.toast_pesteFavorit_VizualizarePEsti),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Stergere peste
        builder.setNegativeButton("Stergere", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFishFromSharedPreferences(pesteSelectat.getId());
                fishService.deleteFishByFishId(pesteSelectat.getId(),
                        callbackStergerePeste(pesteSelectat));
            }
        });

        builder.show();
    }


    // Callback stergere peste
    @NotNull
    private Callback<Integer> callbackStergerePeste(Peste pesteSelectat) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    pesteList.remove(pesteSelectat);
                    notifyAdapter();
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_pesteSters_VizualizarePesti),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(LogTag, getString(R.string.logtag_delete_fish_fishList));
                }
            }
        };
    }


    // Scriere id peste in in SP
    private void saveFishIdInSharedPreferences(int fishId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(FISH_ID_SP, fishId);
        editor.apply();
    }

    // Stergere din shared preferences
    private void deleteFishFromSharedPreferences(int fishId) {
        int fishIdSp = sharedPreferences.getInt(FISH_ID_SP, -1);

        if (fishId == fishIdSp) {
            saveFishIdInSharedPreferences(-1);
        }
    }

}