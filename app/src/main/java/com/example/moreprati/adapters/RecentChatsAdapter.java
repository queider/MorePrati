package com.example.moreprati.adapters;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moreprati.R;

import com.example.moreprati.objects.RecentChat;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
public class RecentChatsAdapter extends RecyclerView.Adapter<RecentChatsAdapter.RecentChatsViewHolder> {
    private List<RecentChat> recentChatList = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(RecentChat recentChat);
    }

    public RecentChatsAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void addRecentChat(RecentChat recentChat) {
        recentChatList.add(recentChat);
        notifyItemInserted(recentChatList.size() - 1);
    }

    @NonNull
    @Override
    public RecentChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chats_item, parent, false);
        return new RecentChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentChatsViewHolder holder, int position) {
        RecentChat recentChat = recentChatList.get(position);
        holder.chatUserName.setText(recentChat.getFullname());
        Picasso.get()
                .load(recentChat.getImageUrl())
                .placeholder(R.drawable.default_profile_pic)
                .error(R.drawable.default_profile_pic)
                .into(holder.image);
        holder.lastMessage.setText(recentChat.getLastMessage());
        holder.lastMessage.setMaxLines(1); // Set maximum lines to 1
        holder.lastMessage.setEllipsize(TextUtils.TruncateAt.END); // Truncate text if it's too long
        holder.itemView.setOnClickListener(v -> listener.onItemClick(recentChat));
    }

    @Override
    public int getItemCount() {
        return recentChatList.size();
    }

    public static class RecentChatsViewHolder extends RecyclerView.ViewHolder {
        TextView chatUserName;
        ImageView image;
        TextView lastMessage;

        public RecentChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            chatUserName = itemView.findViewById(R.id.chatUserName);
            image = itemView.findViewById(R.id.image);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }
}
