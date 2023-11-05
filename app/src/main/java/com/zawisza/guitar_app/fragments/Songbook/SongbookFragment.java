package com.zawisza.guitar_app.fragments.Songbook;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.zawisza.guitar_app.R;
import com.zawisza.guitar_app.fragments.Content.SongBookContentFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class SongbookFragment extends Fragment implements SelectListener{

    private static final String TAG = "Guitar-Master - SongbookFragment";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Query songRef;

    public RecyclerView recyclerView;

    private ArrayList<Songbook> songbookArrayList;
    private RecyclerAdapter recyclerAdapter;

    private Songbook songbook;

    private FirebaseAuth auth;
    private final HashMap<Songbook, String> map = new HashMap<>();

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

        songRef = db.collection(requireActivity().getString(R.string.songbook));
        songRef
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
                            Log.d(TAG, songbook.toString());
                            map.put(songbook, documentChange.getDocument().getId());
                            Log.d(TAG,"Adding SongBook");
                        }
                        if(documentChange.getType() == DocumentChange.Type.MODIFIED){
                            Songbook newSongbook = documentChange.getDocument().toObject(Songbook.class);
                            Log.d(TAG,"Trying modified SongBook");
                            for(int i = 0; i< songbookArrayList.size(); i++){
                                if(newSongbook.getTitle().equals(songbookArrayList.get(i).getTitle())){
                                    songbookArrayList.set(i, newSongbook);
                                    Log.d(TAG,"Complete modified SongBook");
                                    break;
                                }

                            }
                        }
                        if(documentChange.getType() == DocumentChange.Type.REMOVED){
                            Songbook newSongbook = documentChange.getDocument().toObject(Songbook.class);
                            Log.d(TAG,"Trying remove SongBook");
                            for(int i = 0; i< songbookArrayList.size(); i++){
                                if(newSongbook.getTitle().equals(songbookArrayList.get(i).getTitle())){
                                    songbookArrayList.remove(i);
                                    Log.d(TAG,"Complete remove SongBook");
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

        Log.d(TAG,"Open SongBookFragment");

        auth = FirebaseAuth.getInstance();

        setUpRecyclerView(view);

        return view;
    }

    @Override
    public void onItemClick(int number) {
        Collections.sort(songbookArrayList);
        if(auth.getCurrentUser() != null) {
            replaceFragments(R.id.frameLayout_login, songbookArrayList.get(number));
        }else{
            replaceFragments(R.id.frameLayout, songbookArrayList.get(number));
        }
    }

    public void replaceFragments(int id_layout, Songbook songbook1) {
        Fragment fragment = new SongBookContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", songbook1.getTitle());
        bundle.putString("accords", songbook1.getAccords());
        bundle.putBoolean("isTabs", songbook1.isTabs());
        bundle.putString("rate", songbook1.getRate());
        bundle.putString("text", songbook1.getText());
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations( R.anim.enter_right_to_left, R.anim.exit_right_to_left, R.anim.enter_left_to_right , R.anim.exit_left_to_right)
                .replace(id_layout, fragment, TAG)
                .commit();

    }

}