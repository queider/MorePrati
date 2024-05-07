package com.example.moreprati.adapters;// MessageAdapter.java

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moreprati.objects.Message;
import com.example.moreprati.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private String currentUserId;

    public MessageAdapter(List<Message> messages, String currentUserId) {
        this.currentUserId = currentUserId;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message, currentUserId);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }

        public void bind(Message message, String currentUserId) {
            messageTextView.setText(message.getMessageText());

            // Set background based on the sender's UID
            String senderUid = message.getSender();
            if (senderUid.equals(currentUserId)) {
                messageTextView.setBackgroundResource(R.drawable.receiver_bubble);
                // Set other properties for the sender's bubble
                messageTextView.setGravity(Gravity.END); // Align text to the end (right) of the view
            }else {
                messageTextView.setBackgroundResource(R.drawable.sender_bubble);
                // Set other properties for the receiver's bubble
            }
        }
    }
}
