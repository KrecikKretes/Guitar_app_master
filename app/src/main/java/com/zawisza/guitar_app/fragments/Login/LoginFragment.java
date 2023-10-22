package com.zawisza.guitar_app.fragments.Login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.zawisza.guitar_app.R;
import com.zawisza.guitar_app.activities.AdminActivity;
import com.zawisza.guitar_app.fragments.GuitarPick.SoundMeter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private EditText email;
    private EditText password;

    private FirebaseAuth auth;

    private Button delete;

    private Context context;

    public LoginFragment() {
        // Required empty public constructor
    }


    private void loginUser(String txt_email, String txt_password) {
        SoundMeter.t1.cancel();
        auth.signInWithEmailAndPassword(txt_email,txt_password).addOnSuccessListener(authResult -> {
            Toast.makeText(context, "Zalogowano!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(context, AdminActivity.class));
            requireActivity().finish();
        }).addOnFailureListener(e -> Toast.makeText(context, "Nie poprawny login / hasło", Toast.LENGTH_LONG).show());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        Button login = view.findViewById(R.id.login);
        delete = view.findViewById(R.id.button_delete_memory);

        auth = FirebaseAuth.getInstance();
        context = getContext();

        login.setOnClickListener(view1 -> {
            String txt_email= email.getText().toString();
            String txt_password = password.getText().toString();

            if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                Toast.makeText(getContext(), "Puste okienko", Toast.LENGTH_SHORT).show();
            }else{
                loginUser(txt_email, txt_password);
            }
        });
        delete.setOnClickListener(view1 -> dialogDelete());

        return view;
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