package com.example.kosta.serviceexamcount02;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class CountService extends Service {

    private int count;

    private Thread countThread;

    ICountInterface.Stub myBinder = new ICountInterface.Stub() {
        @Override
        public int getCurrentNumber() throws RemoteException {
            return count;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setContentTitle("Count")
                .setContentText("Running Count Service")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1111, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(countThread == null) {
            countThread = new Thread() {
                @Override
                public void run() {
                    while(true) {
                        count++;

                        Log.d("count", count + "");

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            };
            countThread.start();
        }

//        LowMemoryKiller 또는 의도적으로 서비스가 종료되었을 경우 메모리가 확보되면 다시 실행된다.
//        return START_STICKY;

//        다시 실행되지 않는다.
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if(countThread != null) {
            countThread.interrupt();
            countThread = null;
            count = 0;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
}
