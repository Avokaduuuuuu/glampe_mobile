package com.avocado.glampe_mobile.di;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.avocado.glampe_mobile.model.dto.chat.ChatMessageDto;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;

public class StompManager {
    private static StompManager instance;
    private final StompClient stompClient;
    private final MutableLiveData<ChatMessageDto> incoming = new MutableLiveData<>();
    private final MutableLiveData<Boolean> connectionState = new MutableLiveData<>();
    private final Gson gson = new Gson();
    private boolean isConnected = false;

    private StompManager() {
        OkHttpClient ok = new OkHttpClient.Builder()
                .pingInterval(20, TimeUnit.SECONDS)
                .build();

        // Thử cả 2 URL này:
        // Option 1: SockJS endpoint (nếu backend support)
        String sockJsUrl = "http://10.0.2.2:8080/api/v1/chat/ws";

        // Option 2: Raw WebSocket endpoint (recommend)
        // String sockJsUrl = "ws://10.0.2.2:8080/api/v1/chat/ws";

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP,
                sockJsUrl + "/websocket", null, ok);

        // Handle connection lifecycle
        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    isConnected = true;
                    connectionState.postValue(true);
                    System.out.println("✅ STOMP Connected");
                    break;
                case ERROR:
                    isConnected = false;
                    connectionState.postValue(false);
                    System.out.println("❌ STOMP Error: " + lifecycleEvent.getException());
                    break;
                case CLOSED:
                    isConnected = false;
                    connectionState.postValue(false);
                    System.out.println("🔌 STOMP Disconnected");
                    break;
            }
        });
    }

    public static synchronized StompManager get() {
        if (instance == null) instance = new StompManager();
        return instance;
    }

    public LiveData<ChatMessageDto> incoming() { return incoming; }
    public LiveData<Boolean> connectionState() { return connectionState; }

    public void connect(Long myId) {
        // Bước 1: Connect đến STOMP server trước
        stompClient.connect();

        // Bước 2: Subscribe topic sau khi connected
        stompClient.lifecycle()
                .filter(event -> event.getType() == LifecycleEvent.Type.OPENED)
                .subscribe(event -> {
                    // Subscribe sau khi đã connected
                    stompClient.topic("/topic/chat/" + myId)
                            .subscribe(msg -> {
                                ChatMessageDto dto = gson.fromJson(msg.getPayload(), ChatMessageDto.class);
                                incoming.postValue(dto);
                                System.out.println("📩 Received: " + msg.getPayload());
                            }, throwable -> {
                                System.out.println("⚠️ Subscribe error: " + throwable.getMessage());
                                throwable.printStackTrace();
                            });
                });
    }

    public void send(ChatMessageDto dto) {
        if (isConnected) {
            stompClient.send("/app/chat.send", gson.toJson(dto))
                    .subscribe(
                            () -> System.out.println("📤 Message sent: " + gson.toJson(dto)),
                            error -> System.out.println("⚠️ Send error: " + error.getMessage())
                    );
        } else {
            System.out.println("⚠️ Not connected! Cannot send message.");
        }
    }

    public void disconnect() {
        if (stompClient.isConnected()) {
            stompClient.disconnect();
            isConnected = false;
        }
    }
}
