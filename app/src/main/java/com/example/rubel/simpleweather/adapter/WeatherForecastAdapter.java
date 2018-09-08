package com.example.rubel.simpleweather.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rubel.simpleweather.R;
import com.example.rubel.simpleweather.common.Common;
import com.example.rubel.simpleweather.models.WeatherForecastReult;
import com.squareup.picasso.Picasso;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.MyViewholder> {

    Context context;
    WeatherForecastReult weatherForecastReult;

    public WeatherForecastAdapter(Context context, WeatherForecastReult weatherForecastReult) {
        this.context = context;
        this.weatherForecastReult = weatherForecastReult;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_weather_forecast,parent,false);
        return new MyViewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, int position) {
        //Load icon
        //Load image
        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                .append(weatherForecastReult.list.get(position).weather.get(0).getIcon())
                .append(".png").toString()).into(holder.img_wether);

        holder.txt_date_time.setText(new StringBuilder(Common.convertUnixToDate(weatherForecastReult
        .list.get(position).dt)));
        holder.txt_description.setText(new StringBuilder(weatherForecastReult.list.get(position)
        .weather.get(0).getDescription()));

        holder.txt_temperature.setText(new StringBuilder(String.valueOf(weatherForecastReult.list.get(position)
        .main.getTemp())).append("°C"));

    }

    @Override
    public int getItemCount() {
        return weatherForecastReult.list.size();
    }

    public class MyViewholder extends RecyclerView.ViewHolder {
        TextView txt_date_time, txt_description, txt_temperature;
        ImageView img_wether;
        public MyViewholder(View itemView) {
            super(itemView);
            img_wether =(ImageView)itemView.findViewById(R.id.img_weather);
            txt_date_time=(TextView)itemView.findViewById(R.id.txt_date);
            txt_description=(TextView)itemView.findViewById(R.id.txt_description);
            txt_temperature=(TextView)itemView.findViewById(R.id.txt_temperature);
        }
    }
}
