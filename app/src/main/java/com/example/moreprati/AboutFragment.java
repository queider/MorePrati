package com.example.moreprati;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;

public class AboutFragment extends Fragment {

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        // Display your text or information here (TextView, etc.)

        // Logout button
        view.findViewById(R.id.logoutButton).setOnClickListener(v -> logoutUser());
    }

    private void logoutUser() {
        mAuth.signOut();
        startActivity(new Intent(requireActivity(), SignUp.class));
        requireActivity().finish(); // Close the current activity
    }
}
