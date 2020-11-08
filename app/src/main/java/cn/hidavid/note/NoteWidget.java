package cn.hidavid.note;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.io.File;
import java.util.ArrayList;

import cn.hidavid.util.FileUtil;

import static cn.hidavid.note.AppDef.SP_KEY_MSG;
import static cn.hidavid.note.AppDef.SP_NAME;

/**
 * Implementation of App Widget functionality.
 */
public class NoteWidget extends AppWidgetProvider {

    private RemoteViews mRemoteViews;
    private ComponentName mComponentName;

    private static boolean bShowCover = true;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Log.d("dv", "updateAppWidget: " + appWidgetId);

        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.note_widget);
        mComponentName = new ComponentName(context, NoteWidget.class);

        if(bShowCover){
            mRemoteViews.setViewVisibility(R.id.ivPainting, View.VISIBLE);
        } else {
            mRemoteViews.setViewVisibility(R.id.ivPainting, View.GONE);
        }

        // 刷新内容
        readCacheContent(context);
        if(bShowCover){
            updateImage(context);
        }

        initClickCoverEvent(context);
        initButtonEvent(context);

        Intent lvIntent = new Intent(context, ListViewService.class);
        mRemoteViews.setRemoteAdapter(R.id.lvData, lvIntent);
        mRemoteViews.setEmptyView(R.id.lvData,android.R.id.empty);

        appWidgetManager.updateAppWidget(appWidgetId, mRemoteViews);
    }

    private void initClickCoverEvent(Context context) {
        Intent intent = new Intent(context, NoteWidget.class);
        intent.setAction(AppDef.ACTION_CLICK_COVER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.ivPainting, pendingIntent);
    }

    private void initButtonEvent(Context context) {
        Intent intent = new Intent(context, NoteWidget.class);
        intent.setAction(AppDef.ACTION_CLICK_BUTTON);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.btnRefresh, pendingIntent);
    }

    private void readCacheContent(Context context){

        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String msg = sp.getString(SP_KEY_MSG,"");

        // 若sp里数据为空，则从内存卡里读取数据
        if(TextUtils.isEmpty(msg)){
            String data = FileUtil.readFromLocal(AppDef.FILE_PATH_OF_DATA);
            if(!TextUtils.isEmpty(data)){
                msg = data;
            } else {
                msg = "Hello，Welcome to HiNote！";
            }
        }

        mRemoteViews.setTextViewText(R.id.tvContent, msg);
    }

    private static int imageCount = 0;
    private static int curImageId = -1;
    private void updateImage(Context context){
        if(mRemoteViews == null){
            mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.note_widget);
        }

        File folder = new File("/sdcard/Pictures/艺术/");
        if(!folder.exists())
            return;

        ArrayList<File> fileList = new ArrayList<>();

        File[] subFile = folder.listFiles();
        for (File file : subFile){
            String name = file.getName().toLowerCase();
            if(name.endsWith("jpg") || name.endsWith("png")){
                fileList.add(file);
            }
        }
        imageCount = fileList.size();

        if(curImageId < 0 || curImageId >= imageCount - 1) {
            curImageId = 0;
        } else {
            curImageId ++;
        }

        String path = fileList.get(curImageId).getAbsolutePath();
        Log.d("dv", "updateImage: " + curImageId + " " + path);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        int inSampleSize = 1;
        if (srcWidth > 1080) {
            inSampleSize = Math.round(srcWidth / 1080);
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);

        mRemoteViews.setImageViewBitmap(R.id.ivPainting, bmp);
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

        if(mRemoteViews == null){
            mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.note_widget);
        }

        if(TextUtils.equals(AppDef.ACTION_SAVE_NOTE, action)){
            refresh(context);
        } else if (TextUtils.equals(AppDef.ACTION_CLICK_COVER, action)){
            refresh(context);
        } else if (TextUtils.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE, action)){
            refresh(context);
        } else if (TextUtils.equals(AppDef.ACTION_CLICK_BUTTON, action)){
            onClickShownBtn(context);
        }
    }

    private void onClickShownBtn(Context context){
        bShowCover = !bShowCover;

        refresh(context);
    }

    private void refresh(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context, NoteWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }

}

