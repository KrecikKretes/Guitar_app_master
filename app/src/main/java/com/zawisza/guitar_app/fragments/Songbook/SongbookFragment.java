package com.zawisza.guitar_app.fragments.Songbook;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.zawisza.guitar_app.Functions;
import com.zawisza.guitar_app.R;
import com.zawisza.guitar_app.Variables;

import java.util.ArrayList;
import java.util.HashMap;


public class SongbookFragment extends Fragment implements SelectListener{

    private static final String TAG = "Rajd - FAQFragment";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Query faqRef = db.collection(Variables.getFAQs());

    public RecyclerView recyclerView;

    private ArrayList<Songbook> songbookArrayList;
    private RecyclerAdapter recyclerAdapter;

    private Songbook songbook;
    private final HashMap<Songbook, String> map = new HashMap<>();

    private final Functions functions = new Functions();

    private void setUpRecyclerView(View view) {

        songbookArrayList = new ArrayList<>();
        recyclerAdapter = new RecyclerAdapter(getContext(), songbookArrayList, this);

        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);


        EventChangeListener();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }



    @SuppressLint("NotifyDataSetChanged")
    private void EventChangeListener(){

        faqRef
                .orderBy("no", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {

                    if(error != null){
                        Log.d("Error", error.toString());
                        return;
                    }
                    assert value != null;
                    for(DocumentChange documentChange : value.getDocumentChanges()){
                        if(documentChange.getType() == DocumentChange.Type.ADDED){
                            songbook = documentChange.getDocument().toObject(Songbook.class);
                            songbookArrayList.add(songbook);
                            map.put(songbook, documentChange.getDocument().getId());
                            Log.d(TAG,"Adding FAQ");
                        }
                        if(documentChange.getType() == DocumentChange.Type.MODIFIED){
                            Songbook newSongbook = documentChange.getDocument().toObject(Songbook.class);
                            Log.d(TAG,"Trying modified FAQ");
                            for(int i = 0; i< songbookArrayList.size(); i++){
                                if(newSongbook.getQuestion().equals(songbookArrayList.get(i).getQuestion())){
                                    songbookArrayList.set(i, newSongbook);
                                    Log.d(TAG,"Complete modified FAQ");
                                    break;
                                }

                            }
                        }
                        if(documentChange.getType() == DocumentChange.Type.REMOVED){
                            Songbook newSongbook = documentChange.getDocument().toObject(Songbook.class);
                            Log.d(TAG,"Trying remove FAQ");
                            for(int i = 0; i< songbookArrayList.size(); i++){
                                if(newSongbook.getQuestion().equals(songbookArrayList.get(i).getQuestion())){
                                    songbookArrayList.remove(i);
                                    Log.d(TAG,"Complete remove FAQ");
                                    break;
                                }
                            }
                        }
                        recyclerAdapter.notifyDataSetChanged();
                    }
                    recyclerAdapter.notifyDataSetChanged();
                });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songbook, container, false);


        setUpRecyclerView(view);

        return view;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onItemClick(TextView[] textViewArray, ImageView imageView, ViewSwitcher viewSwitcher) {
        functions.onItemClickAnimation(textViewArray,imageView,viewSwitcher,getContext(), "FAQ", null);
    }
}