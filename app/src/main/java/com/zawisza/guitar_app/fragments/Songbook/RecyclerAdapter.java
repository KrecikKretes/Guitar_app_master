package com.zawisza.guitar_app.fragments.Songbook;

import android.content.Context;
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

        holder.questionTextView.setText(functions.newLinesRepairer(songbook.getQuestion()));

        holder.answerTextView.setText(functions.newLinesRepairer(songbook.getAnswer()));

        TextView[] textViewArray = new TextView[1];
        textViewArray[0] = holder.answerTextView;

        holder.cardview.setOnClickListener(view ->
                listener.onItemClick(textViewArray, holder.imageView , holder.viewSwitcher)
        );

    }

    @Override
    public int getItemCount() {
        return songbookArrayList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{

        ViewSwitcher viewSwitcher;
        TextView questionTextView, answerTextView;
        ImageView imageView;
        CardView cardview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            questionTextView = itemView.findViewById(R.id.text_view_question);
            answerTextView = itemView.findViewById(R.id.text_view_answer);
            viewSwitcher = itemView.findViewById(R.id.faq_view_switcher);

            cardview = itemView.findViewById(R.id.item_card_view);

            imageView = itemView.findViewById(R.id.image_view_show_more);

        }
    }




}
