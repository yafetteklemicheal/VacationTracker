package com.example.vacationtracker.Activities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacationtracker.Entities.Excursion;
import com.example.vacationtracker.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {
    private List<Excursion> excursionList;
    private Context context;

    public ExcursionAdapter(List<Excursion> excursionList, Context context) {
        this.excursionList = excursionList;
        this.context = context;
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_excursion, parent, false);
        return new ExcursionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        Excursion excursion = excursionList.get(position);
        holder.excursionName.setText(excursion.getExcursionName());
        holder.excursionStartDate.setText(excursion.getExcursionDate());

        holder.fabEditExcursion.setOnClickListener(v -> {
            Intent editExcursionIntent = new Intent(context, ExcursionDetails.class);
            editExcursionIntent.putExtra("excursionId", excursion.getExcursionID());
            editExcursionIntent.putExtra("vacationStartDate", ((VacationDetails) context).getVacationStartDate());
            editExcursionIntent.putExtra("vacationEndDate", ((VacationDetails) context).getVacationEndDate());
            context.startActivity(editExcursionIntent);
        });

        holder.fabDeleteExcursion.setOnClickListener(v -> {
            if (context instanceof VacationDetails) {
                ((VacationDetails) context).deleteExcursion(excursion);
            }
        });

        holder.fabAlertExcursion.setOnClickListener(v -> {
            if (context instanceof VacationDetails) {
                ((VacationDetails) context).setAlertForExcursion(excursion.getExcursionDate(), "Excursion " + excursion.getExcursionName() + " is starting", excursion.getExcursionID() * 2);
                Toast.makeText(context, "Alert has been set for " + excursion.getExcursionName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return excursionList.size();
    }

    public void updateExcursions(List<Excursion> newExcursionList) {
        this.excursionList = newExcursionList;
        notifyDataSetChanged();
    }

    public static class ExcursionViewHolder extends RecyclerView.ViewHolder {
        TextView excursionName, excursionStartDate;
        FloatingActionButton fabEditExcursion, fabDeleteExcursion, fabAlertExcursion;

        public ExcursionViewHolder(@NonNull View itemView) {
            super(itemView);
            excursionName = itemView.findViewById(R.id.excursionName);
            excursionStartDate = itemView.findViewById(R.id.excursionStartDate);
            fabEditExcursion = itemView.findViewById(R.id.fabEditExcursion);
            fabDeleteExcursion = itemView.findViewById(R.id.fabDeleteExcursion);
            fabAlertExcursion = itemView.findViewById(R.id.fabAlertExcursion);
        }
    }
}