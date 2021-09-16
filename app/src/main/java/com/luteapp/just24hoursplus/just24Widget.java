package com.luteapp.just24hoursplus;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class just24Widget extends AppWidgetProvider
{

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId)
    {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.just24_widget);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds)
        {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context)
    {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context)
    {
        // Enter relevant functionality for when the last widget is disabled
    }


    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions){

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,newOptions);

        // this only works for versions of android since Jelly Bean, however Oreo and above do this resizing on their own
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.O)
        {
            // get the dimensions of the widget
            int maxWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
            int maxHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
            int pxSizeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, maxWidth, context.getResources().getDisplayMetrics());
            int pxSizeHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, maxHeight, context.getResources().getDisplayMetrics());

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.just24_widget);

            // create a string with the time in 24 hour format
            String currTime = new SimpleDateFormat("kk:mm").format(new Date());

            // determine the maximum size the clock text can be without spilling over the widget bounds
            float newSize = refitClockText(currTime, pxSizeHeight, pxSizeWidth);

            // set the new text size in the widget
            remoteViews.setTextViewTextSize(R.id.textClock, TypedValue.COMPLEX_UNIT_PX, newSize * context.getResources().getDisplayMetrics().density/2);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

    }


    // resize the clock text so that it fills the widget without spilling over the edges
    private float refitClockText(String timeText, int widgetHeight, int widgetWidth)
    {
        if (widgetWidth <= 0 || widgetHeight <= 0)
            return 0;

        // start with a very small text size
        float newTextSize = 2;

        // a raster verson of the clock to determine the size of the clock text
        Paint clockPaint = new Paint();
        // dimensions of the clock text
        Rect clockBounds = new Rect();

        // flag to check that the text size can still be increased
        boolean tooSmall = true;

        // increase the text size until it now longer fits into the widget
        while(tooSmall)
        {
            clockPaint.setTextSize(newTextSize);
            clockPaint.getTextBounds(timeText, 0, timeText.length(), clockBounds);

            // check both the height and width of the clock text are still smaller than the widget
            if (clockBounds.width() < widgetWidth && clockBounds.height() < widgetHeight)
            {
                newTextSize = newTextSize + 2;
            }
            else
            {
                tooSmall = false;
            }
        }

        // Log.d("Widget Size: ", widgetHeight + " - " + widgetWidth);
        // Log.d("Clock Size: ", clockBounds.height() + " - " + clockBounds.width());

        return newTextSize;
    }

}

