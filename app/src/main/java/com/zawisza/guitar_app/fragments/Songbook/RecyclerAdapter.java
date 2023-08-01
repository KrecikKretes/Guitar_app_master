package com.zawisza.guitar_app.fragments.Songbook;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.zawisza.guitar_app.Functions;
import com.zawisza.guitar_app.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    private final SelectListener listener;

    private final ArrayList<Songbook> songbookArrayList;

    public RecyclerAdapter(Context mContext, ArrayList<Songbook> songbookArrayList, SelectListener listener) {
        this.songbookArrayList = songbookArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.songbook_item, parent, false);

       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Songbook songbook = songbookArrayList.get(position);
        Functions functions = new Functions();

        holder.titleTextView.setText(functions.newLinesRepairer(songbook.getTitle()));

        holder.cardview.setOnClickListener(view -> {
            listener.onItemClick(songbook.getNo());
        });
    }

    @Override
    public int getItemCount() {
        return songbookArrayList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView;
        CardView cardview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.text_view_title);
            cardview = itemView.findViewById(R.id.item_card_view);
        }
    }




}
