package cn.hidavid.note;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import static cn.hidavid.note.AppDef.SP_KEY_MSG;
import static cn.hidavid.note.AppDef.SP_NAME;

/**
 * Implementation of App Widget functionality.
 */
public class NoteWidget extends AppWidgetProvider {

    private RemoteViews mRemoteViews;
    private ComponentName mComponentName;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.note_widget);
        mComponentName = new ComponentName(context, NoteWidget.class);

        readCacheContent(context);

//        Intent intent = new Intent(context, NoteWidget.class);
//        intent.setAction(AppDef.ACTION_CLICK_COVER);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//        mRemoteViews.setOnClickPendingIntent(R.id.imgCover, pendingIntent);

        Intent lvIntent = new Intent(context, ListViewService.class);
        mRemoteViews.setRemoteAdapter(R.id.lvData, lvIntent);
        mRemoteViews.setEmptyView(R.id.lvData,android.R.id.empty);

//        msg = sp.getString("test","");
//        Intent intent = new Intent(context, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//        mRemoteViews.setOnClickPendingIntent(R.id.btnRefresh, pendingIntent);
//        mRemoteViews.setCharSequence(R.id.btnRefresh, "setText", msg);


        appWidgetManager.updateAppWidget(appWidgetId, mRemoteViews);
    }

    private void readCacheContent(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String msg = sp.getString(SP_KEY_MSG,"Hello，Welcome to HiNote！");
        mRemoteViews.setTextViewText(R.id.tvContent, msg);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("dvlee","onUpdate");
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.d("dv","onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        Log.d("dv","onDisabled");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d("dv","onReceive " + intent.getAction());
        String action = intent.getAction();

        if(TextUtils.equals(AppDef.ACTION_SAVE_NOTE, action)){
            refresh(context);
        } else if (TextUtils.equals(AppDef.ACTION_CLICK_COVER, action)){
//            mRemoteViews.setViewVisibility(R.id.imgCover, View.GONE);
            refresh(context);
        } else if (TextUtils.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE, action)){
            refresh(context);
        }


    }

    private void refresh(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context, NoteWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }

}

