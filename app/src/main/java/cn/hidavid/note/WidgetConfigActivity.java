package cn.hidavid.note;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;

public class WidgetConfigActivity extends AppCompatActivity {


    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private EditText etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);

        Intent configIntent = getIntent();
        Bundle extras = configIntent.getExtras();
        if(extras != null ){
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.i("dvlee","appWidgetId:" + appWidgetId);
        }

        Intent resultVaule = new Intent();
        resultVaule.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_CANCELED, resultVaule);

        if(appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID){
            finish();
        }

        etContent = findViewById(R.id.etContent);
    }

    public void onClickBtn(View view) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        String txt = etContent.getText().toString();

        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.note_widget);
        views.setOnClickPendingIntent(R.id.btnRefresh, pendingIntent);
        views.setCharSequence(R.id.btnRefresh, "setText", txt);

        appWidgetManager.updateAppWidget(appWidgetId, views);

        SharedPreferences sp = getSharedPreferences(AppDef.SP_NAME, MODE_PRIVATE);
        sp.edit().putString("test", txt).apply();

        Intent resultVaule = new Intent();
        resultVaule.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultVaule);
        finish();
    }
}