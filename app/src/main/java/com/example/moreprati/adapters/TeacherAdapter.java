package com.example.moreprati.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moreprati.R;
import com.example.moreprati.objects.Teacher;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

public class TeacherAdapter extends FirebaseRecyclerAdapter<Teacher, TeacherAdapter.ViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Teacher teacher);
    }


    public TeacherAdapter(@NonNull FirebaseRecyclerOptions<Teacher> options, OnItemClickListener listener ) {
        super(options);
        this.listener = listener;

    }


    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Teacher model) {
        if (position != RecyclerView.NO_POSITION) {
            holder.bind(model);

            // Set a click listener for the item
            holder.itemView.setOnClickListener(v -> {
                // Call the onItemClick method of the listener and pass the clicked teacher
                listener.onItemClick(model);
            });
        }
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
        public RatingBar ratingbar;
        public TextView wayOfLearningTextView;
        public TextView pricePerHourTextView;
        public TextView cityTextView;
        public TextView subjectsTextView;
        public TextView howManyRated;

        public ImageView profileImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.fullname);
            ratingbar = itemView.findViewById(R.id.ratingBar);
            wayOfLearningTextView = itemView.findViewById(R.id.wayOfLearning);
            pricePerHourTextView = itemView.findViewById(R.id.pricePerHour);
            cityTextView = itemView.findViewById(R.id.city);
            subjectsTextView = itemView.findViewById(R.id.subjects);
            profileImageView = itemView.findViewById(R.id.profilePic);
            howManyRated = itemView.findViewById(R.id.howManyRated);
        }

        public void bind(Teacher teacher) {
            // Bind data to views
            fullNameTextView.setText(teacher.getFullname());
            ratingbar.setRating(teacher.getRating());
            wayOfLearningTextView.setText(teacher.getWayOfLearning());
            pricePerHourTextView.setText(String.valueOf(teacher.getPricePerHour()) + " ₪ לשעה");
            cityTextView.setText(teacher.getCity());
            howManyRated.setText("(" +String.valueOf( teacher.getHowManyRated()) + ")");

            // Extract subjects from the map and join them into a string
            StringBuilder subjectsStringBuilder = new StringBuilder();
            for (String subject : teacher.getSubjects().keySet()) {
                if (teacher.getSubjects().get(subject)) {
                    if (subjectsStringBuilder.length() > 0) {
                        subjectsStringBuilder.append(" · ");
                    }
                    subjectsStringBuilder.append(subject);
                }
            }
            subjectsTextView.setText(subjectsStringBuilder.toString());

            Log.d("TAG", "bind: "+ teacher.getImageUrl());
            Picasso.get().load(teacher.getImageUrl()).placeholder(R.drawable.default_profile_pic).into(profileImageView);


        }
    }
}

