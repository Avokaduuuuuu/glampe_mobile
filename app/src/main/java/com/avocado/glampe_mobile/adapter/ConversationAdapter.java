package com.avocado.glampe_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.model.dto.chat.ConversationInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    private List<ConversationInfo> conversations = new ArrayList<>();
    private ConversationListener listener;

    private final SimpleDateFormat timeFmt =
            new SimpleDateFormat("HH:mm", Locale.getDefault());


    public ConversationAdapter(List<ConversationInfo> conversations, ConversationListener listener) {
        this.conversations = conversations;
        this.listener = listener;
    }

    public interface ConversationListener {
        void onClick(ConversationInfo conversationInfo);
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_conversation, viewGroup, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int i) {
        ConversationInfo conversationInfo = conversations.get(i);

        holder.tvName.setText(conversationInfo.getUser().getLastName() + " " + conversationInfo.getUser().getFirstName());
        holder.tvLastMessage.setText(conversationInfo.getLastMessage());
        holder.tvTimeStamp.setText(timeFmt.format(conversationInfo.getTimestamp()));
        holder.layout.setOnClickListener(v -> listener.onClick(conversationInfo));
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class ConversationViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLastMessage, tvTimeStamp;
        ImageView ivProfile;
        LinearLayout layout;

        public ConversationViewHolder(@NonNull View view) {
            super(view);

            tvName = view.findViewById(R.id.tvName);
            tvLastMessage = view.findViewById(R.id.tvLastMessage);
            tvTimeStamp = view.findViewById(R.id.tvTimeStamp);
            ivProfile = view.findViewById(R.id.ivProfile);
            layout = view.findViewById(R.id.layoutConversation);
        }
    }
}
