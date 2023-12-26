package com.example.moreprati;

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
import com.squareup.picasso.Picasso;

public class RecentChatsAdapter extends FirebaseRecyclerAdapter<RecentChats, RecentChatsAdapter.RecentChatsViewHolder> {
    private RecentChatsAdapter.OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(String chatUserId);  // Pass any necessary data for the new activity
    }
    @Override
    public void onDataChanged() {
        super.onDataChanged();
        // This method will be called when there are changes in the data
        // You can perform additional tasks if needed

        // For example, you can log a message
        Log.d("YAZAN", "onDataChanged: Data set changed");

        // Or you can update UI, show/hide views, etc.
        // For instance, you might want to display a message when there are no items
        if (getItemCount() == 0) {
            // Handle the case when there are no items
        }
    }

    public RecentChatsAdapter(@NonNull FirebaseRecyclerOptions<RecentChats> options, OnItemClickListener listener) {
        super(options);
        this.listener = listener;
    }


    @NonNull
    @Override
    public RecentChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("YAZAN", "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chats_item, parent, false);
        return new RecentChatsViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull RecentChatsViewHolder holder, int position, @NonNull RecentChats model) {
        Log.d("YAZAN", "onBindViewHolder for position: " + position);

        if (position >= 0 && model != null) {
            holder.chatUserName.setText(model.getFullname());
            holder.lastMessage.setText(model.getLastMessage());
            Picasso.get()
                    .load(model.getImageUrl())
                    .placeholder(R.drawable.default_profile_pic)
                    .error(R.drawable.default_profile_pic)
                    .into(holder.image);

            holder.itemView.setOnClickListener(v -> {
                Log.d("YAZAN", "onBindViewHolder: ChatUserID is " + model.getChatUserId());
                listener.onItemClick(model.getChatUserId());
            });
        }
    }



    public static class RecentChatsViewHolder extends RecyclerView.ViewHolder {
        TextView chatUserName;
        TextView lastMessage;

        ImageView image;

        public RecentChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            chatUserName = itemView.findViewById(R.id.chatUserName);
            image = itemView.findViewById(R.id.image);
        }
    }
}
