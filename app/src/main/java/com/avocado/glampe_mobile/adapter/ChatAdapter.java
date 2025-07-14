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

    public ChatAdapter(Long userId) {
        this.userId = userId;
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

    public void appendList(List<ChatMessageDto> newList) {
        messages.clear();
        messages.addAll(newList);
        messages.sort(Comparator.comparingLong(ChatMessageDto::getTimestamp));
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSenderId().equals(userId) ? TYPE_SENT : TYPE_RECEIVED;
    }
}
