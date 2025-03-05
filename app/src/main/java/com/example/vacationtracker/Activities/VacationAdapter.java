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

import com.example.vacationtracker.Entities.Vacation;
import com.example.vacationtracker.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {
    private List<Vacation> vacationList;
    private Context context;

    public VacationAdapter(List<Vacation> vacationList, Context context) {
        this.vacationList = vacationList;
        this.context = context;
    }

    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vacation, parent, false);
        return new VacationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        Vacation vacation = vacationList.get(position);
        holder.vacationName.setText(vacation.getVacationName());
        holder.hotel.setText(vacation.getHotel());
        holder.startDate.setText(vacation.getStartDate());
        holder.endDate.setText(vacation.getEndDate());

        holder.fabEditVacation.setOnClickListener(v -> {
            Intent editVacationIntent = new Intent(context, VacationDetails.class);
            editVacationIntent.putExtra("vacationId", vacation.getVacationID());
            context.startActivity(editVacationIntent);
        });

        holder.fabDeleteVacation.setOnClickListener(v -> {
            if (context instanceof VacationList) {
                ((VacationList) context).deleteVacation(vacation);
            }
        });

        holder.fabAlertVacation.setOnClickListener(v -> {
            if (context instanceof VacationList) {
                ((VacationList) context).setAlertForVacation(vacation.getStartDate(), "Vacation " + vacation.getVacationName() + " is starting", vacation.getVacationID() * 2);
                ((VacationList) context).setAlertForVacation(vacation.getEndDate(), "Vacation " + vacation.getVacationName() + " is ending", vacation.getVacationID() * 2 + 1);
                Toast.makeText(context, "Alert has been set for " + vacation.getVacationName(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.fabShareVacation.setOnClickListener(v -> {
            if (context instanceof VacationList) {
                ((VacationList) context).shareVacationDetails(vacation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vacationList.size();
    }

    public void updateVacations(List<Vacation> newVacationList) {
        this.vacationList = newVacationList;
        notifyDataSetChanged();
    }

    public static class VacationViewHolder extends RecyclerView.ViewHolder {
        TextView vacationName, hotel, startDate, endDate, excursionName, excursionStartDate;
        FloatingActionButton fabEditVacation, fabDeleteVacation, fabAlertVacation, fabShareVacation;

        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);
            vacationName = itemView.findViewById(R.id.vacationName);
            hotel = itemView.findViewById(R.id.hotel);
            startDate = itemView.findViewById(R.id.startDate);
            endDate = itemView.findViewById(R.id.endDate);
            excursionName = itemView.findViewById(R.id.excursionName);
            excursionStartDate = itemView.findViewById(R.id.excursionStartDate);
            fabEditVacation = itemView.findViewById(R.id.fabEditVacation);
            fabDeleteVacation = itemView.findViewById(R.id.fabDeleteVacation);
            fabAlertVacation = itemView.findViewById(R.id.fabAlertVacation);
            fabShareVacation = itemView.findViewById(R.id.fabShareVacation);
        }
    }
}