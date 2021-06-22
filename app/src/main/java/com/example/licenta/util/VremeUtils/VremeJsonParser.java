package com.example.licenta.util.VremeUtils;

import com.example.licenta.clase.vreme.Vreme;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class VremeJsonParser {

    public static List<Vreme> fromJson(String json) {
        try {
            JSONObject obiect = new JSONObject(json);
            JSONArray array = obiect.getJSONArray("daily");
            return readVreme(array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();

    }
    private static List<Vreme> readVreme(JSONArray array) throws JSONException {
        List<Vreme> vremeList = new ArrayList<>();
        for (int i = 0,j=0; i < array.length()&&j<7; i++,j++) {
            List<Vreme> meciuri = readVremes(array.getJSONObject(i),j);
            vremeList.addAll(meciuri);
        }
        return vremeList;
    }

    private static List<Vreme> readVremes(JSONObject object,int j) throws JSONException {
        List<Vreme> rezultatList = new ArrayList<>();
        String data = object.getString("dt");
        JSONObject temperatura = object.getJSONObject("temp");
        DecimalFormat df = new DecimalFormat("#.#");

        String[] datavector = Calendar.getInstance().getTime().toString().split(" ");
        SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        String sourceDate = datavector[1] + " " + datavector[2];
        String dataNoua=getCalculatedDate("dd-MM-yyyy", j);

        double tempZi = Double.parseDouble(temperatura.getString("day")) / 10;
        double tempNoapte = Double.parseDouble(temperatura.getString("night")) / 10;
        double tempZinou = Double.parseDouble(df.format(tempZi));
        double tempNoaptenou = Double.parseDouble(df.format(tempNoapte));
        int umiditate = Integer.parseInt(object.getString("humidity"));
        float probPrecipitatii = Float.parseFloat(object.getString("pop")) * 100;
        JSONArray vreme = object.getJSONArray("weather");
        JSONObject vremeJSONObject;
        String iconita="";
        for (int i = 0; i < vreme.length(); i++) {
            vremeJSONObject = vreme.getJSONObject(i);
            iconita = "https://openweathermap.org/img/wn/" + vremeJSONObject.getString("icon") + "@2x.png";
        }
        Vreme vreme1 = new Vreme(dataNoua, tempZinou, tempNoaptenou, umiditate, probPrecipitatii, iconita);
        rezultatList.add(vreme1);
        return rezultatList;
    }

    public static String getCalculatedDate(String dateFormat, int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat(dateFormat);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return s.format(new Date(cal.getTimeInMillis()));
    }
}
