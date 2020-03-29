package com.example.chatsignalr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import io.reactivex.Single;

public class MainActivity extends AppCompatActivity {

    private  HubConnection hubConnection;
    private  String user ="Kha";
    //private  String messeger ="";
    private TextView tvHien;
    private EditText etNoiDung;
    private Button btnGui;

    public Handler handler = null;
    public static Runnable runnable = null;

    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyMgr;
    int mNotificationId = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvHien = findViewById(R.id.tvHien);
        etNoiDung = findViewById(R.id.etNoiDung);
        btnGui = findViewById(R.id.btnSend);

        this.mBuilder = new NotificationCompat.Builder(this);
        this.mBuilder.setAutoCancel(true);

        hubConnection = HubConnectionBuilder.create("http://192.168.55.4:63139/chathub").build();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                hubConnection.start();
                Toast.makeText(getApplication(), "Service is still running", Toast.LENGTH_LONG).show();
                handler.postDelayed(runnable, 1000);
            }
        };
      handler.postDelayed(runnable, 1000);

        hubConnection.on("NhanTin",(name, messeger) ->{
            // String t = name + messeger;


            this.mBuilder.setTicker("Đây là Ticker");
            this.mBuilder.setContentTitle("Thông Báo");
            this.mBuilder.setContentText("Bạn Có Tin Nhấn mới");
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = mBuilder.build();
            notificationManager.notify(123456, notification);

            tvHien.setText(name + ": "+ messeger);
       },String.class, String.class);


        btnGui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(hubConnection.getConnectionState() == HubConnectionState.CONNECTED){
                    hubConnection.invoke(String.class,"GuiTinNhan", user, etNoiDung.getText().toString());

                }else{
                    Toast.makeText(getApplication(), "Không thể gủi dữ liệu", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
