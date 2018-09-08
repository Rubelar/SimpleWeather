package com.example.rubel.simpleweather.retrofit;

import com.example.rubel.simpleweather.models.WeatherForecastReult;
import com.example.rubel.simpleweather.models.WeatherReult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherMap {
    @GET("weather")
    Observable<WeatherReult> getWeatherByLatLng(@Query("lat") String lat,
                                                @Query("lon") String lng,
                                                @Query("appid") String appid,
                                                @Query("units") String unit);

    @GET("weather")
    Observable<WeatherReult> getWeatherByCityName(@Query("q") String cityName,
                                                @Query("appid") String appid,
                                                @Query("units") String unit);

    @GET("forecast")
    Observable<WeatherForecastReult> getForecastWeatherByLatLng(@Query("lat") String lat,
                                                                @Query("lon") String lng,
                                                                @Query("appid") String appid,
                                                                @Query("units") String unit);
}
