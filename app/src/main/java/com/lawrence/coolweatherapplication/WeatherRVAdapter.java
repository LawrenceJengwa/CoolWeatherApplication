package com.lawrence.coolweatherapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lawrence.coolweatherapplication.model.WeatherRVModel;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.WeatherViewHolder> {
    private Context context;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;

    public WeatherRVAdapter(Context context, ArrayList<WeatherRVModel> weatherRVModelArrayList) {
        this.context = context;
        this.weatherRVModelArrayList = weatherRVModelArrayList;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        WeatherRVModel weatherModel = weatherRVModelArrayList.get(position);
        String baseUrl = "http:";
        Picasso.get().load(baseUrl.concat(weatherModel.getIcon())).into(holder.conditionIV);
        holder.temperatureTv.setText(weatherModel.getTemperature() + "\t°C");
        holder.windTv.setText(weatherModel.getWindSpeed() + "km/h");

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm aa");
        try{
            Date date = inputFormat.parse(weatherModel.getTime());
            holder.timeTv.setText(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return weatherRVModelArrayList.size();
    }

    protected static class WeatherViewHolder extends RecyclerView.ViewHolder {

        private TextView windTv, temperatureTv, timeTv;
        private ImageView conditionIV;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            windTv = itemView.findViewById(R.id.idTVWindSpeed);
            temperatureTv = itemView.findViewById(R.id.idTVTemperature);
            timeTv = itemView.findViewById(R.id.idTVTime);
            conditionIV = itemView.findViewById(R.id.idIVCondition);
        }
    }
}
