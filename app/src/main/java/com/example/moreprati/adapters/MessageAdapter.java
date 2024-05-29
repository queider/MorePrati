package com.example.moreprati.adapters;// MessageAdapter.java

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moreprati.objects.Message;
import com.example.moreprati.R;
import java.util.List;
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private String currentUserId;
    private FrameLayout messageLayout;


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
            messageLayout = itemView.findViewById(R.id.messageLayout);
            messageTextView = itemView.findViewById(R.id.messageTextView);

        }

        public void bind(Message message, String currentUserId) {
            messageTextView.setText(message.getMessageText());

            String senderUid = message.getSender();
            if (senderUid.equals(currentUserId)) {
                messageTextView.setBackgroundResource(R.drawable.sender_bubble);
                updateGravity(Gravity.END);


            } else {
                messageTextView.setBackgroundResource(R.drawable.receiver_bubble);
                updateGravity(Gravity.START);

            }
        }

        private void updateGravity(int gravity) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) messageTextView.getLayoutParams();
            layoutParams.gravity = gravity;
            messageTextView.setLayoutParams(layoutParams);
        }


    }
}
