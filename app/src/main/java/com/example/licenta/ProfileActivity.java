package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.example.licenta.asyncTask.AsyncTaskRunner;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.peste.Peste;
import com.example.licenta.clase.peste.PestiAdaptor;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.clase.user.User;
import com.example.licenta.database.ConexiuneBD;
import com.example.licenta.database.service.CommentForumService;
import com.example.licenta.database.service.UserService;
import com.example.licenta.util.dateUtils.DateConverter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class ProfileActivity extends AppCompatActivity {

    private List<Peste> pesteList = new ArrayList<>();
    private ConexiuneBD conexiuneBD = ConexiuneBD.getInstance();
    private AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();
    private CurrentUser currentUser = CurrentUser.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getAllFishById(currentUser.getId(), callbackGetPestiById());

        CommentForumService commentForumService = new CommentForumService();
        commentForumService.getAnswersCountByUserId(currentUser.getId(), new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                int ceva = result;
            }
        });
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
                pesteList.add(result.get(0));


                ListView listView = findViewById(R.id.lv_bestFish_profileActivity);
                PestiAdaptor adaptor = new PestiAdaptor(getApplicationContext(), R.layout.listview_pesti,
                        pesteList, getLayoutInflater());
                listView.setAdapter(adaptor);
            }
        };
    }
}