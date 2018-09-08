package com.example.rubel.simpleweather;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rubel.simpleweather.common.Common;
import com.example.rubel.simpleweather.models.WeatherReult;
import com.example.rubel.simpleweather.retrofit.IOpenWeatherMap;
import com.example.rubel.simpleweather.retrofit.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class CityFragment extends Fragment {

    private List<String> lstCities;
    private MaterialSearchBar searchBar;

    ImageView img_weather;
    TextView txt_city_name, txt_humidity, txt_sunrise, txt_sunset, txt_pressure, txt_temperature, txt_description, txt_date_time, txt_wind, txt_geo_coord;
    LinearLayout weather_panel;
    ProgressBar loading;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;


    static CityFragment instance;

    public static CityFragment getInstance() {
        if(instance==null)
            instance = new CityFragment();
        return instance;
    }

    public CityFragment() {
        // Required empty public constructor
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_city, container, false);
        img_weather =(ImageView)itemView.findViewById(R.id.img_weather);
        txt_city_name =(TextView)itemView.findViewById(R.id.txt_city_name);
        txt_humidity =(TextView)itemView.findViewById(R.id.txt_humidity);
        txt_sunrise =(TextView)itemView.findViewById(R.id.txt_sunrise);
        txt_sunset =(TextView)itemView.findViewById(R.id.txt_sunset);
        txt_pressure =(TextView)itemView.findViewById(R.id.txt_pressure);
        txt_temperature =(TextView)itemView.findViewById(R.id.txt_temperature);
        txt_description =(TextView)itemView.findViewById(R.id.txt_description);
        txt_date_time =(TextView)itemView.findViewById(R.id.txt_date_time);
        txt_wind =(TextView)itemView.findViewById(R.id.txt_wind);
        txt_geo_coord =(TextView)itemView.findViewById(R.id.txt_geo_coord);

        weather_panel =(LinearLayout)itemView.findViewById(R.id.weather_panel);
        loading =(ProgressBar)itemView.findViewById(R.id.loading);

        searchBar = (MaterialSearchBar)itemView.findViewById(R.id.weather_panel);
        searchBar.setEnabled(false);

        new LoadCities().execute(); //asyncTask class to load Cities List
        return itemView;
    }
    

    private class LoadCities extends SimpleAsyncTask<List<String>> {
        @Override
        protected List<String> doInBackgroundSimple() {
            lstCities = new ArrayList<>();
            try {
                StringBuilder builder = new StringBuilder();
                InputStream is = getResources().openRawResource(R.raw.city_list);
                GZIPInputStream gzipInputStream = new GZIPInputStream(is);

                InputStreamReader reader = new InputStreamReader(gzipInputStream);
                BufferedReader in = new BufferedReader(reader);
                String readed;
                while ((readed = in.readLine()) !=null)
                    builder.append(readed);
                lstCities = new Gson().fromJson(builder.toString(), new TypeToken<List<String>>(){}.getType());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return lstCities;
        }

        //ctrl+O

        @Override
        protected void onSuccess(final List<String> listCity) {
            super.onSuccess(listCity);

            searchBar.setEnabled(true);
            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<String> suggest = new ArrayList<>();
                    for (String search : listCity){
                        if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                            suggest.add(search);
                    }
                    searchBar.setLastSuggestions(suggest);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {

                }

                @Override
                public void onSearchConfirmed(CharSequence text) {

                    getWeatherInformation(text.toString());

                    searchBar.setLastSuggestions(listCity);
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });

            searchBar.setLastSuggestions(listCity);
            loading.setVisibility(View.GONE);
        }
    }

    private void getWeatherInformation(String cityName) {
        compositeDisposable.add(mService.getWeatherByCityName(cityName,
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherReult>() {
                    @Override
                    public void accept(WeatherReult weatherReult) throws Exception {

                        //Load image
                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                                .append(weatherReult.getWeather().get(0).getIcon())
                                .append(".png").toString()).into(img_weather);

                        //Load information
                        txt_city_name.setText(weatherReult.getName());
                        txt_description.setText(new StringBuilder("Weather in ")
                                .append(weatherReult.getName()).toString());
                        txt_temperature.setText(new StringBuilder(
                                String.valueOf(weatherReult.getMain().getTemp())).append("°C").toString());
                        txt_date_time.setText(Common.convertUnixToDate(weatherReult.getDt()));
                        txt_pressure.setText(new StringBuilder(String.valueOf(weatherReult.getMain().getPressure())).append(" hpa").toString());
                        txt_humidity.setText(new StringBuilder(String.valueOf(weatherReult.getMain().getHumidity())).append(" %").toString());
                        txt_sunrise.setText(Common.convertUnixToHour(weatherReult.getSys().getSunrise()));
                        txt_sunset.setText(Common.convertUnixToHour(weatherReult.getSys().getSunset()));
                        txt_geo_coord.setText(new StringBuilder(weatherReult.getCoord().toString()).toString());

                        //Display panel
                        weather_panel.setVisibility(View.VISIBLE);
                        //loading.setVisibility(View.GONE);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })

        );
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
