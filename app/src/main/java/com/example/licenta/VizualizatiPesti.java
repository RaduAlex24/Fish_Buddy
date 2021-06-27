package com.example.licenta;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.licenta.asyncTask.AsyncTaskRunner;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.forum.CategoryForum;
import com.example.licenta.clase.forum.ForumPost;
import com.example.licenta.clase.peste.Peste;
import com.example.licenta.clase.peste.PestiAdaptor;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.database.ConexiuneBD;
import com.example.licenta.util.dateUtils.DateConverter;

import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class VizualizatiPesti extends AppCompatActivity {

    private ListView lvPesti;
    private List<Peste> pesteList = new ArrayList<>();
    private Button adaugaPeste;
    private int ReqPeste = 1060;
    private ConexiuneBD conexiuneBD=ConexiuneBD.getInstance();
    private CurrentUser currentUser = CurrentUser.getInstance();
    private AsyncTaskRunner asyncTaskRunner=new AsyncTaskRunner();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pesti);
        initcomponents();
        addAdapter();
        adaugaPeste.setOnClickListener(goToAdaugarePeste());
        getAllFishById(currentUser.getId(),callbackGetPestiById());
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ReqPeste && resultCode == RESULT_OK && data != null) {
            Peste peste = (Peste) data.getSerializableExtra(AdaugaPeste.Peste_key);
            if (peste != null) {
                pesteList.add(peste);
                notifyAdapter();
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
                List<Peste> pesteListDinBd= new ArrayList<>();
                String sql = "SELECT * FROM " + "FISH where USERID = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1,id);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    String specii = resultSet.getString(3);
                    int lungime =resultSet.getInt(4);
                    int greutate = resultSet.getInt(5);
                    String dataS = resultSet.getString(6);
                    String locatie = resultSet.getString(7);
                    byte[] imgByte = resultSet.getBytes(8);

                    Date date = DateConverter.toDate(dataS);
                    Peste peste = new Peste(greutate,lungime,specii,locatie,date,imgByte);
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
}