package com.hyunseok.android.threadbasic;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btn_start, btn_stop;
    TextView tv_time;

    Thread thread;
    boolean flag = false;

    // 핸들러 메세지에 담겨오는 what에 대한 정의를 해둔다.
    public static final int SET_TEXT = 100;
    // 메세지를 받는 서버, 핸들러는 메세지를 받기위해 계속 돌고있다.
    // runOnUiThread보다 Handler가 더 빠르다.
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_TEXT:
                    tv_time.setText(msg.arg1+"");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        tv_time = (TextView) findViewById(R.id.tv_time);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == true) {
                    Toast.makeText(MainActivity.this, "실행중이다.", Toast.LENGTH_SHORT).show();
                } else {
                    flag = true;
                    //Thread thread = new customThread();
                    thread = new HandlerThread(); // Garbage Collector
                    thread.start();
                }
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopProgram();
            }
        });
    }

    public void runProgram() {

    }

    public void stopProgram() {
        flag = false;
    }


    class customThread extends Thread {
        @Override
        public void run() {
            int i = 0;
            while(flag) {
                i++;
                if(i % 100 == 0) {
                    // 메인쓰레드에서 동작
                    final int sec = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_time.setText( sec / 100 + "" );
                        }
                    });
                }
            }

        }
    }

    class HandlerThread extends Thread {
        @Override
        public void run() {
            int sec = 0;

            // Thread 안에서 무한루프 돌때는 반드시 flag를 줘서 중단시킬 수 있도록 해야한다.
            while(flag) {

                Message msg = new Message();
                msg.what = SET_TEXT;
                msg.arg1 = sec;

                handler.sendMessage(msg);
                sec++;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 반드시 Thread를 중단 시켜줘야함. flag, interrupt()둘다 사용해서.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = false;
        thread.interrupt();
    }
}
