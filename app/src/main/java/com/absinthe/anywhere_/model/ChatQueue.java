package com.absinthe.anywhere_.model;

import androidx.annotation.Nullable;

import com.absinthe.anywhere_.utils.manager.Logger;

import java.util.LinkedList;

public class ChatQueue extends LinkedList<String> {

    private IChatQueueListener mListener;

    public ChatQueue(IChatQueueListener listener) {
        mListener = listener;
    }

    @Override
    public boolean offer(String s) {
        boolean result = super.offer(s);
        Logger.d("Chat Enqueue:", s);
        mListener.onEnqueue();
        return result;
    }

    @Nullable
    @Override
    public String poll() {
        String head = super.poll();
        mListener.onDequeue(head);
        Logger.d("Chat Dequeue:", head);
        return head;
    }

    public interface IChatQueueListener {
        void onEnqueue();
        void onDequeue(String head);
    }
}
