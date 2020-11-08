package cn.hidavid.note;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;

import java.io.File;
import java.io.FileOutputStream;

import cn.hidavid.util.FileUtil;

import static cn.hidavid.note.AppDef.SP_KEY_MSG;
import static cn.hidavid.note.AppDef.SP_NAME;

public class MainActivity extends AppCompatActivity {
    SharedPreferences mSp;
    Context mContext;

    EditText etMsg;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        verifyStoragePermissions(this);
        mSp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        initView();
    }

    private void initView() {
        etMsg = findViewById(R.id.etMsg);

        String msg = mSp.getString(SP_KEY_MSG, "");
        if(TextUtils.isEmpty(msg)){
            String data = FileUtil.readFromLocal(AppDef.FILE_PATH_OF_DATA);
            if(!TextUtils.isEmpty(data)){
                msg = data;
            } else {
                msg = "Hello，Welcome to HiNote！";
            }
        }

        etMsg.setText(msg);
    }

    public static void verifyStoragePermissions(Activity activity) {
        try {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickBtn(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnSave:
                saveMsg();
                break;
        }
    }

    private void saveMsg() {
        String msg = etMsg.getText().toString();

        mSp.edit().putString(SP_KEY_MSG, msg).commit();

        Intent intent = new Intent(this, NoteWidget.class);
        intent.setAction(AppDef.ACTION_SAVE_NOTE);
        this.sendBroadcast(intent);

        FileUtil.saveToLocal("/sdcard/hinote.txt", msg);

        finish();
    }



    private void updateWidget() {
//        AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
//        ComponentName componentName = new ComponentName(getApplicationContext(),NoteWidget.class);
//        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.note_widget);
//        manager.updateAppWidget(componentName,remoteViews);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.note_widget);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(new ComponentName(this, NoteWidget.class), remoteViews);
    }
}