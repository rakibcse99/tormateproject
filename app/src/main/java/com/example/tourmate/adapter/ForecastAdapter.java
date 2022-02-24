package com.example.tourmate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourmate.R;
import com.example.tourmate.forecastweather.ForecastList;
import com.example.tourmate.helper.EventUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {
    private Context context;
    private List<ForecastList> forecastLists;

    public ForecastAdapter(Context context, List<ForecastList> forecastLists) {
        this.context = context;
        this.forecastLists = forecastLists;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.forecast_row, parent, false);
        return new ForecastViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        holder.dateTV.setText(EventUtils.getFormattedDate(forecastLists.get(position).getDt()));
        //set dayname according to date value
        holder.dayTV.setText(EventUtils.getFormattedDay(forecastLists.get(position).getDt()));
        holder.hTempTv.setText(String.valueOf(forecastLists.get(position).getTemp().getMax()));
        holder.lTempTv.setText(String.valueOf(forecastLists.get(position).getTemp().getMin()));
        holder.conditionTV.setText(forecastLists.get(position).getWeather().get(0).getDescription());
        holder.cloudTV.setText(String.valueOf(forecastLists.get(position).getClouds())+" %,");
        holder.pressureTV.setText(String.valueOf(forecastLists.get(position).getPressure())+" hpa");
        holder.windTV.setText(String.valueOf(forecastLists.get(position).getSpeed())+" m/s");
        String iconUrl = EventUtils.WeatherUtils.ICON_URL_PREFIX+
                forecastLists.get(position).getWeather()
                        .get(0).getIcon()+".png";
        Picasso.get().load(iconUrl).into(holder.iconIV);

    }

    @Override
    public int getItemCount() {
        return forecastLists.size();
    }

    class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView dateTV,dayTV,hTempTv,lTempTv, conditionTV, windTV, cloudTV, pressureTV;
        ImageView iconIV;
        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTV = itemView.findViewById(R.id.row_wdate);
            dayTV = itemView.findViewById(R.id.row_wday);
            hTempTv = itemView.findViewById(R.id.row_wHtemp);
            lTempTv = itemView.findViewById(R.id.row_wLtemp);
            conditionTV = itemView.findViewById(R.id.row_wforcast);
            cloudTV = itemView.findViewById(R.id.row_wCloud);
            pressureTV = itemView.findViewById(R.id.row_wPressure);
            windTV = itemView.findViewById(R.id.row_wWind);

            iconIV = itemView.findViewById(R.id.row_wIcon);
        }
    }
}
