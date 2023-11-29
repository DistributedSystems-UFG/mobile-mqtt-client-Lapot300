package com.example.basicandroidmqttclient;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;

public class RegistredTempAdapter extends RecyclerView.Adapter<RegistredTempAdapter.ViewHolder> {

    private List<RegistredTemp> registredTemps;

    public RegistredTempAdapter(List<RegistredTemp> registredTemps) {
        this.registredTemps = registredTemps;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registred_temp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RegistredTemp item = registredTemps.get(position);

        // Supondo que 'RegistredTemp' tem métodos getTemp() e getDate()
        String tempString = item.getTemp() + "°C";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dateString = dateFormat.format(item.getDate());

        holder.tempTextView.setText(tempString);
        holder.dateTextView.setText(dateString);
    }

    @Override
    public int getItemCount() {
        return registredTemps.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tempTextView;
        TextView dateTextView;

        ViewHolder(View view) {
            super(view);
            tempTextView = view.findViewById(R.id.tempTextView); // ID conforme seu layout de item
            dateTextView = view.findViewById(R.id.dateTextView); // ID conforme seu layout de item
        }
    }
}

