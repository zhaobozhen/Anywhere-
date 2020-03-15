package com.absinthe.anywhere_.model;

import androidx.annotation.Nullable;

import com.absinthe.anywhere_.adapter.gift.ChatAdapter;

import java.util.LinkedList;

import timber.log.Timber;

public class ChatQueue extends LinkedList<String> {

    private IChatQueueListener mListener;
    private Thread offerThread;
    private boolean interrupt = false;

    public ChatQueue(IChatQueueListener listener) {
        mListener = listener;
    }

    public boolean offer(String s, int type) {
        boolean result = super.offer(s);
        Timber.d("Chat Enqueue: %s", s);
        mListener.onEnqueue(type);
        return result;
    }

    public void offer(String[] strs) {
        offerThread = new Thread(() -> {
            try {
                for (String str : strs) {
                    if (interrupt) {
                        break;
                    }

                    String prefix = str.substring(0, 3);
                    Timber.d(prefix);
                    switch (prefix) {
                        case GiftChatString.L:
                            offer(str.substring(3), ChatAdapter.TYPE_LEFT);
                            break;
                        case GiftChatString.R:
                            offer(str.substring(3), ChatAdapter.TYPE_RIGHT);
                            break;
                        case GiftChatString.I:
                            offer(str.substring(3), ChatAdapter.TYPE_INFO);
                            break;
                    }
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        offerThread.start();
    }

    @Nullable
    @Override
    public String poll() {
        String head = super.poll();
        mListener.onDequeue(head);
        Timber.d("Chat Dequeue: %s", head);
        return head;
    }

    public void stopOffer() {
        interrupt = true;
        offerThread.interrupt();
    }

    public interface IChatQueueListener {
        void onEnqueue(int type);
        void onDequeue(String head);
    }
}
