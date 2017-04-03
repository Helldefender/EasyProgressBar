package com.helldefender.zoroprogressbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.helldefender.library.HorizontalProgressBar;
import com.helldefender.library.RoundProgressBar;
import com.helldefender.library.XLDownloadProgressBar;

public class MainActivity extends AppCompatActivity implements Runnable {

    private XLDownloadProgressBar xlDownloadProgressBar;

    private HorizontalProgressBar horizontalProgressBar;

    private RoundProgressBar roundProgressBar;

    private Button startBtn;

    private Thread thread;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            xlDownloadProgressBar.setProgress(msg.arg1);
            horizontalProgressBar.setProgress(msg.arg1);
            roundProgressBar.setProgress(msg.arg1);
            if (msg.arg1 == 100) {
                xlDownloadProgressBar.finishDownload();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xlDownloadProgressBar = (XLDownloadProgressBar) findViewById(R.id.xlDownloadProgressBar);
        horizontalProgressBar = (HorizontalProgressBar) findViewById(R.id.horizontalProgressBar);
        roundProgressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
        startBtn = (Button) findViewById(R.id.start_btn);

        thread = new Thread(this);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread.start();
            }
        });


    }


    @Override
    public void run() {
        while (!thread.isInterrupted()) {
            try {
                float progress = xlDownloadProgressBar.getProgress();
                progress += 1;
                Thread.sleep(100);
                Message message = handler.obtainMessage();
                message.arg1 = (int) progress;
                handler.sendMessage(message);
                if (progress == 100) {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
