package com.example.saikat.weatherforcaster;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.support.v4.app.Fragment;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;
import java.util.Locale;


/**
 * Created by saikat on 8/28/15.
 */
public class WeatherFragment extends Fragment {
    Typeface weatherFont;

    TextView cityField;
    TextView updatedfield;
    TextView detailsField;
    TextView currentTemparatureField;
    TextView weatherIcon;
    TextView sunriseField;
    TextView sunsetField;
    TextView humadityField;
    TextView maxTemparatureField;
    TextView minTemparatureField;
    TextView windField;
    TextView pressureField;
    TextView windField2;

    Handler handler;

    public WeatherFragment(){
        handler = new Handler();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        View rootView = inflater.inflate(R.layout.fragment_weather,container,false);
        cityField = (TextView)rootView.findViewById(R.id.city_field);

        detailsField = (TextView)rootView.findViewById(R.id.descrWeather);
        currentTemparatureField=(TextView)rootView.findViewById(R.id.temp);
        minTemparatureField = (TextView)rootView.findViewById(R.id.tempMin);
        maxTemparatureField=(TextView)rootView.findViewById(R.id.tempMax);
        windField = (TextView)rootView.findViewById(R.id.windSpeed);
        windField2=(TextView)rootView.findViewById(R.id.windDeg);
        pressureField=(TextView)rootView.findViewById(R.id.pressure);
        humadityField =(TextView)rootView.findViewById(R.id.humidity);
        sunriseField=(TextView)rootView.findViewById(R.id.sunrise);
        sunsetField = (TextView)rootView.findViewById(R.id.sunset);
        weatherIcon=(TextView)rootView.findViewById(R.id.imgWeather);

        updatedfield=(TextView)rootView.findViewById(R.id.updated_on);
        weatherIcon.setTypeface(weatherFont);





        return rootView;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "weather.ttf");
        updateWeatherData(new prefercity(getActivity()).getCity());


    }



    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = Datafetcher.getJSON(getActivity(),city);

                if (json==null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {

                    handler.post(new Runnable() {
                        public void run() {

                            renderWeather(json);
                        }
                    });
                   
                }

            }
        }.start();
    }
    private void renderWeather(JSONObject json){



        try{

            String cf,des,sr,ss,uf,h,p,ws,wa,ct,mint,maxt;


            cf=(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            des=(details.getString("description").toUpperCase(Locale.US));
            h=main.getString("humidity") ;
            p=main.getString("pressure");
            mint=main.getString("temp_min");
            maxt=main.getString("temp_max");
            ct=main.getString("temp");




            DateFormat df = DateFormat.getDateTimeInstance();
            uf = df.format(new Date(json.getLong("dt") * 1000));

            JSONObject windata =json.getJSONObject("wind");
             ws=windata.getString("speed");
             wa=windata.getString("deg");

            sr=df.format(new Date(json.getJSONObject("sys").getLong("sunrise") * 1000));
            ss=df.format(new Date(json.getJSONObject("sys").getLong("sunset") * 1000));




            cityField.setText(cf);

            detailsField.setText(des);

            humadityField.setText(h+"%");

            pressureField.setText(p+ " hPa");



            Date datess = new SimpleDateFormat("dd MMM yyyy HH:mm:ss").parse(ss);
            Date datesr = new SimpleDateFormat("dd MMM yyyy HH:mm:ss").parse(sr);

             ss = new SimpleDateFormat("h:mm").format(datess);
             sr = new SimpleDateFormat("h:mm").format(datesr);

            sunsetField.setText(ss+" PM");

            sunriseField.setText(sr+" AM");

            currentTemparatureField.setText(String.format("%.1f ℃",Double.parseDouble(ct)));

            minTemparatureField.setText(String.format(" min: %.1f ℃    |",Double.parseDouble(mint)-2));

            maxTemparatureField.setText(String.format(" max: %.1f ℃",Double.parseDouble(maxt)+3));

            windField.setText(String.format(""+ws+" km/h"));

            windField2.setText(String.format(""+wa+"°"));

            updatedfield.setText(String.format("Last Updated On :"+uf));


            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }
        catch (Exception e){
            Log.e("Weather Forecaster", "One or more fields not found in the JSON data");
        }
    }
    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);



            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }
    public void changeCity(String city){


        updateWeatherData(city);
    }




}

