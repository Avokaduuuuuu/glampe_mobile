package com.avocado.glampe_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.model.dto.chat.ChatMessageDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT     = 0;
    private static final int TYPE_RECEIVED = 1;

    List<ChatMessageDto> messages = new ArrayList<>();

    private final Long userId;
    private final SimpleDateFormat timeFmt =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    // Callback interface for scroll events
    public interface OnNewMessageListener {
        void onNewMessageAdded(int position, boolean shouldScrollToBottom);
    }

    private OnNewMessageListener onNewMessageListener;
    private RecyclerView recyclerView;

    public ChatAdapter(Long userId) {
        this.userId = userId;
    }

    // Set the callback listener
    public void setOnNewMessageListener(OnNewMessageListener listener) {
        this.onNewMessageListener = listener;
    }

    // Override onAttachedToRecyclerView to get reference to RecyclerView
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inf = LayoutInflater.from(viewGroup.getContext());
        if (i == TYPE_SENT) {
            View v = inf.inflate(R.layout.item_message_sent, viewGroup, false);
            return new SentVH(v);
        } else {
            View v = inf.inflate(R.layout.item_message_received, viewGroup, false);
            return new RecvVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ChatMessageDto message = messages.get(i);

        String hhmm = timeFmt.format(new Date(message.getTimestamp()));

        if (viewHolder instanceof SentVH) {
            SentVH vh = (SentVH) viewHolder;
            vh.content.setText(message.getContent());
            vh.time.setText(hhmm);
        } else {
            RecvVH vh = (RecvVH) viewHolder;
            vh.content.setText(message.getContent());
            vh.time.setText(hhmm);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private static class SentVH extends RecyclerView.ViewHolder {
        TextView content, time;
        SentVH(@NonNull View v) {
            super(v);
            content = v.findViewById(R.id.text_message_content);
            time    = v.findViewById(R.id.text_message_time);
        }
    }

    private static class RecvVH extends RecyclerView.ViewHolder {
        TextView content, time;
        RecvVH(@NonNull View v) {
            super(v);
            content = v.findViewById(R.id.text_message_content);
            time    = v.findViewById(R.id.text_message_time);
        }
    }

    // Updated method with auto-scroll functionality
    public void appendList(List<ChatMessageDto> newList) {
        boolean wasAtBottom = isAtBottom();
        int oldSize = messages.size();

        messages.clear();
        messages.addAll(newList);
        messages.sort(Comparator.comparingLong(ChatMessageDto::getTimestamp));

        notifyDataSetChanged();

        // Auto scroll to bottom if:
        // 1. This is the first load (oldSize == 0)
        // 2. User was already at bottom
        // 3. Or we have new messages
        if (oldSize == 0 || wasAtBottom || newList.size() > oldSize) {
            scrollToBottom();
        }

        // Notify listener if set
        if (onNewMessageListener != null && !messages.isEmpty()) {
            onNewMessageListener.onNewMessageAdded(
                    messages.size() - 1,
                    oldSize == 0 || wasAtBottom
            );
        }
    }

    // Method to add a single new message (more efficient for real-time chat)
    public void addNewMessage(ChatMessageDto message) {
        boolean wasAtBottom = isAtBottom();

        messages.add(message);
        messages.sort(Comparator.comparingLong(ChatMessageDto::getTimestamp));

        int newPosition = messages.size() - 1;
        notifyItemInserted(newPosition);

        // Auto scroll to bottom
        if (wasAtBottom) {
            scrollToBottom();
        }

        // Notify listener
        if (onNewMessageListener != null) {
            onNewMessageListener.onNewMessageAdded(newPosition, wasAtBottom);
        }
    }

    // Check if RecyclerView is currently scrolled to bottom
    private boolean isAtBottom() {
        if (recyclerView == null || messages.isEmpty()) {
            return true; // Consider empty list as "at bottom"
        }

        // Check if last item is visible
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) return true;

        // Get the position of the last visible item
        int lastVisiblePosition = -1;
        if (layoutManager instanceof androidx.recyclerview.widget.LinearLayoutManager) {
            androidx.recyclerview.widget.LinearLayoutManager linearLayoutManager =
                    (androidx.recyclerview.widget.LinearLayoutManager) layoutManager;
            lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        }

        // Consider "at bottom" if last item is visible or we're close to it
        return lastVisiblePosition >= messages.size() - 2;
    }

    // Scroll to bottom method
    private void scrollToBottom() {
        if (recyclerView != null && !messages.isEmpty()) {
            recyclerView.post(() -> {
                recyclerView.smoothScrollToPosition(messages.size() - 1);
            });
        }
    }

    // Public method to manually scroll to bottom
    public void scrollToBottomManually() {
        scrollToBottom();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSenderId().equals(userId) ? TYPE_SENT : TYPE_RECEIVED;
    }
}