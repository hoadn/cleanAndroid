package com.cssweb.android.user.track;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class TrackService extends Service
{
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent)
    {
        super.onRebind(intent);
    }

    public class TrackBinder extends Binder
    {
        public TrackService getService()
        {
            return TrackService.this;
        }
    }

    @Override
    public void onCreate()
    {
        // HandleActivity.handleStack();
        HandleUrl.mainThreadStart();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
    }

    private final IBinder mBinder = new TrackBinder();

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }
}
