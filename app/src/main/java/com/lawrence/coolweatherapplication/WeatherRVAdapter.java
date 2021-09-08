package com.lawrence.coolweatherapplication;

import static com.lawrence.coolweatherapplication.utils.WeatherUtil.INPUT_PATTERN;
import static com.lawrence.coolweatherapplication.utils.WeatherUtil.OUTPUT_PATTERN;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lawrence.coolweatherapplication.databinding.WeatherRvItemBinding;
import com.lawrence.coolweatherapplication.model.WeatherRVModel;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.WeatherViewHolder> {
    private final Context context;
    private final ArrayList<WeatherRVModel> weatherRVModelArrayList;

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
        Picasso.get().load(baseUrl.concat(weatherModel.getIcon())).into(holder.binding.idIVCondition);
        holder.binding.idTVTemperature.setText(String.format("%s\t°C", weatherModel.getTemperature()));
        holder.binding.idTVWindSpeed.setText(String.format("%skm/h", weatherModel.getWindSpeed()));

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat inputFormat = new SimpleDateFormat(OUTPUT_PATTERN);
        @SuppressLint("SimpleDateFormat")
         SimpleDateFormat outputFormat = new SimpleDateFormat(INPUT_PATTERN);
        try {
            Date date = inputFormat.parse(weatherModel.getTime());
            holder.binding.idTVTime.setText(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return weatherRVModelArrayList.size();
    }

    protected static class WeatherViewHolder extends RecyclerView.ViewHolder {

        private final WeatherRvItemBinding binding;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = WeatherRvItemBinding.bind(itemView);
        }
    }
}
