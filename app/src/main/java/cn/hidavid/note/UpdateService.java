package cn.hidavid.note;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

public class UpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("dvlee","onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("dvlee","onStartCommand");

//        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.note_widget);
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
//        appWidgetManager.updateAppWidget(new ComponentName(this, NoteWidget.class), remoteViews);

        return super.onStartCommand(intent, flags, startId);
    }
}
