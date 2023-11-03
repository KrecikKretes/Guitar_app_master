package com.zawisza.guitar_app.fragments.Chords;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.zawisza.guitar_app.R;
import com.zawisza.guitar_app.Variables;
import com.zawisza.guitar_app.fragments.Content.ChordsContentFragment;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;

public class ChordsFragment extends Fragment implements SelectListener {

    private static final String TAG = "Guitar-Master - ChordsFragment";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Query chordsRef;

    public RecyclerView recyclerView;

    private ArrayList<Chords> chordsArrayList;
    private ArrayList<Chords> chordsArrayListFilter;
    private EditText chordsFilter;
    private RecyclerAdapter recyclerAdapter, recyclerAdapterFilter;

    private Chords chord;
    private final HashMap<Chords, String> map = new HashMap<>();

    private void setUpRecyclerView(View view) {

        chordsFilter = view.findViewById(R.id.chords_textview);
        chordsArrayList = new ArrayList<>();
        chordsArrayListFilter = new ArrayList<>();

        recyclerAdapter = new RecyclerAdapter(getContext(), chordsArrayList, this);
        recyclerAdapterFilter = new RecyclerAdapter(getContext(), chordsArrayListFilter, this);

        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
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

        chordsFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG,"Text changed");
                chordsArrayListFilter.clear();
                for(Chords chord : chordsArrayList){
                    if(chord.getChord().matches("(.*)" + chordsFilter.getText() + "(.*)")){
                        chordsArrayListFilter.add(chord);
                    }
                }
                recyclerView.swapAdapter(recyclerAdapterFilter, true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        chordsRef = db.collection(requireActivity().getString(R.string.chords));

        chordsRef
                .orderBy("no", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {

                    if(error != null){
                        Log.d("Error", error.toString());
                        return;
                    }
                    assert value != null;
                    for(DocumentChange documentChange : value.getDocumentChanges()){
                        if(documentChange.getType() == DocumentChange.Type.ADDED){
                            chord = documentChange.getDocument().toObject(Chords.class);
                            chordsArrayList.add(chord);
                            map.put(chord, documentChange.getDocument().getId());
                            Log.d(TAG,"Adding Chord");
                            Log.d(TAG, chord.toString());
                        }
                        if(documentChange.getType() == DocumentChange.Type.MODIFIED){
                            Chords newChord = documentChange.getDocument().toObject(Chords.class);
                            Log.d(TAG,"Trying modified Chord");
                            for(int i = 0; i< chordsArrayList.size(); i++){
                                if(newChord.getNo() == (chordsArrayList.get(i).getNo())){
                                    chordsArrayList.set(i, newChord);
                                    Log.d(TAG,"Complete modified Chord");
                                    break;
                                }

                            }
                        }
                        if(documentChange.getType() == DocumentChange.Type.REMOVED){
                            Chords newChord = documentChange.getDocument().toObject(Chords.class);
                            Log.d(TAG,"Trying remove Chord");
                            for(int i = 0; i< chordsArrayList.size(); i++){
                                if(newChord.getNo() == (chordsArrayList.get(i).getNo())){
                                    chordsArrayList.remove(i);
                                    Log.d(TAG,"Complete remove Chord");
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
        View view = inflater.inflate(R.layout.fragment_chords, container, false);


        setUpRecyclerView(view);

        return view;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onItemClick(Chords chords) {
        Variables.getTitleTextView().setText("");
        Variables.getBackTextView().setText(getResources().getString(R.string.chords));
        Variables.getButton_login().setBackgroundResource(Variables.getIcon_back());
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            replaceFragments(R.id.frameLayout_login, chords, new ChordsContentFragment());
        }else{
            replaceFragments(R.id.frameLayout, chords, new ChordsContentFragment());
        }
    }

    public void replaceFragments(int id_layout, Chords chords, Fragment fragment) {
        String TAG = "Adappciak - DetailsPersonnelFragment";
        Bundle bundle = new Bundle();
        bundle.putInt("no", chords.getNo());
        bundle.putString("ton", chords.getTon());
        bundle.putString("chord", chords.getChord());
        bundle.putString("code", chords.getCode());
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations( R.anim.enter_right_to_left, R.anim.exit_right_to_left,  R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                .replace(id_layout, fragment, TAG)
                .commit();

    }
}