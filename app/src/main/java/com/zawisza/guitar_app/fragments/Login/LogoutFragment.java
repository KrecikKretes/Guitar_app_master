package com.zawisza.guitar_app.fragments.Login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.zawisza.guitar_app.R;
import com.zawisza.guitar_app.activities.UserActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class LogoutFragment extends Fragment {

    Button logout;
    Button delete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout, container, false);

        logout = view.findViewById(R.id.button_logout);
        delete = view.findViewById(R.id.button_delete_memory);

        TextView textView = view.findViewById(R.id.logout_login);

        textView.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());


        listeners();

        return view;
    }

    public void listeners(){
        logout.setOnClickListener(view -> dialogLogOut());

        delete.setOnClickListener(view -> dialogDelete());
    }

    private void dialogLogOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Czy na pewno chcesz się wylogować?");
        //builder.setMessage("???");
        builder.setPositiveButton("Tak", (dialogInterface, i) -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "Wylogowało się", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getContext(), UserActivity.class));
            requireActivity().finish();
        });
        builder.setNegativeButton("Nie", (dialogInterface, i) -> {
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteImages() {
        File dir = new File("/data/data/com.zawisza.raid/app_images");
        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void dialogDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Czy na pewno chcesz usunąć wszystkie zdjęcia?");
        //builder.setMessage("???");
        builder.setPositiveButton("Tak", (dialogInterface, i) -> {
            deleteImages();
            Toast.makeText(getContext(), "Poprawnie usunięto zdjęcia", Toast.LENGTH_LONG).show();
        });
        builder.setNegativeButton("Nie", (dialogInterface, i) -> {
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}