package com.zawisza.guitar_app.fragments.Chords;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.zawisza.guitar_app.Functions;
import com.zawisza.guitar_app.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    private static final String TAG = "Adappciak - PersonnelRecyclerAdapter";

    private final SelectListener listener;

    private final ArrayList<Chords> chordsArrayList;

    private final Context context;

    public RecyclerAdapter(Context mContext, ArrayList<Chords> chordsArrayList, SelectListener listener) {
        this.context = mContext;
        this.chordsArrayList = chordsArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chords_item, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chords chords = chordsArrayList.get(position);
        holder.chordsNameTextView.setText(chords.getChord());

/*
        if(holder.personnelImageView.getTag().equals("No Personnel Image")){
            if(chords.getImage() != null && !chords.getImage().equals("")){
                functions.loadImageFromStorage(personnel.getImage(), holder.personnelImageView, context, context.getResources().getString(R.string.storagePersonnel));
                holder.personnelImageView.setTag("Set Personnel Image");
            }else {
                Glide.with(context).load(R.drawable.loading)
                        .into(holder.personnelImageView);
            }
        }

 */


        holder.cardview.setOnClickListener(view ->
                listener.onItemClick(chordsArrayList.get(holder.getBindingAdapterPosition()))
        );
    }

    @Override
    public int getItemCount() {
        return chordsArrayList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView chordsNameTextView;
        CardView cardview;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            chordsNameTextView = itemView.findViewById(R.id.chord_name);

            cardview = itemView.findViewById(R.id.item_card_view);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}