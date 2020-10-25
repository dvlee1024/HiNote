package cn.hidavid.note;

import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;

import static cn.hidavid.note.AppDef.SP_KEY_MSG;
import static cn.hidavid.note.AppDef.SP_NAME;

public class MainActivity extends AppCompatActivity {
    SharedPreferences mSp;

    EditText etMsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        initView();
    }

    private void initView() {
        etMsg = findViewById(R.id.etMsg);

        String msg = mSp.getString(SP_KEY_MSG, "");
        etMsg.setText(msg);
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