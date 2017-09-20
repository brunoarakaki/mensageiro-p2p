package br.com.mobile2you.m2ybase.data.local;

/**
 * Created by mayerlevy on 9/17/17.
 */

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import br.com.mobile2you.m2ybase.data.remote.models.MessageResponse;
import br.com.mobile2you.m2ybase.ui.chat.ChatPresenter;

public class ReceiverThread implements Runnable {

    private DHT dht;
    final private Handler handler;
    private ChatPresenter mPresenter;
    private Timer t;

    public ReceiverThread(DHT dht, ChatPresenter presenter) {
        this.dht = dht;
        this.mPresenter = presenter;
        handler = new Handler(Looper.getMainLooper());
    }

    public void run() {
        t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    final MessageResponse message = dht.get();
                    if (message != null) {
                        Log.d("DHT", message.getSender().getName() + ": " + message.getText());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("DHT", "update");
                                mPresenter.sendMessage(message);
                            }
                        });
                    }
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }

    public void cancel() {
        t.cancel();
    }

}