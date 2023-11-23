package com.example.moreprati;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso; // Assuming you use Picasso for image loading


public class TeacherAdapter extends FirebaseRecyclerAdapter<Teacher, TeacherAdapter.ViewHolder> {

    public TeacherAdapter(@NonNull FirebaseRecyclerOptions<Teacher> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Teacher model) {
        Log.d("TeacherAdapter", "Binding data for position: " + position);
        holder.bind(model);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_card_item, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView fullNameTextView;
        public TextView ratingTextView;
        public TextView wayOfLearningTextView;
        public TextView pricePerHourTextView;
        public TextView cityTextView;
        public TextView subjectsTextView;
        public ImageView profileImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.fullname);
            ratingTextView = itemView.findViewById(R.id.rating);
            wayOfLearningTextView = itemView.findViewById(R.id.wayOfLearning);
            pricePerHourTextView = itemView.findViewById(R.id.pricePerHour);
            cityTextView = itemView.findViewById(R.id.city);
            subjectsTextView = itemView.findViewById(R.id.subjects);
            profileImageView = itemView.findViewById(R.id.profilePic);
        }

        public void bind(Teacher teacher) {
            // Bind data to views
            fullNameTextView.setText(teacher.getFullname());
            ratingTextView.setText(String.valueOf(teacher.getRating()));
            wayOfLearningTextView.setText(teacher.getWayOfLearning());
            pricePerHourTextView.setText(String.valueOf(teacher.getPricePerHour()));
            cityTextView.setText(teacher.getCity());
            subjectsTextView.setText(TextUtils.join(", ", teacher.getSubjects()));

            Log.d("TAG", "bind: "+ teacher.getImage());
            Picasso.get().load(teacher.getImage()).placeholder(R.drawable.default_profile_pic).into(profileImageView);


        }
    }
}

