package jp.co.recruit_mp.android.lightcalendarview.javasample;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jp.co.recruit_mp.android.lightcalendarview.LightCalendarView;
import jp.co.recruit_mp.android.lightcalendarview.MonthView;
import jp.co.recruit_mp.android.lightcalendarview.accent.Accent;
import jp.co.recruit_mp.android.lightcalendarview.accent.DotAccent;

public class MainActivity extends AppCompatActivity {

    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar calFrom = Calendar.getInstance();
        Calendar calTo = Calendar.getInstance();
        Calendar calNow = Calendar.getInstance();
        calFrom.set(Calendar.MONTH, 0);
        calTo.set(Calendar.MONTH, 11);

        LightCalendarView calendarView = (LightCalendarView) findViewById(R.id.calendarView);
        calendarView.setMonthFrom(calFrom.getTime());
        calendarView.setMonthTo(calTo.getTime());
        calendarView.setMonthCurrent(calNow.getTime());

        // set the calendar view callbacks
        calendarView.setOnStateUpdatedListener(new LightCalendarView.OnStateUpdatedListener() {

            @Override
            public void onMonthSelected(@NotNull Date date, @NotNull final MonthView view) {
                getSupportActionBar().setTitle(formatter.format(date));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Calendar cal = Calendar.getInstance();
                        List<Date> dates = new ArrayList<Date>();
                        for (int i = 0; i < 31; i++) {
                            if (i % 2 == 0) {
                                cal.set(view.getMonth().getYear() + 1900, view.getMonth().getMonth(), i);
                                dates.add(cal.getTime());
                            }
                        }
                        HashMap<Date, List<Accent>> map = new HashMap<>();
                        for (Date date : dates) {
                            List<Accent> accents = new ArrayList<>();
                            for (int i = 0; i <= (date.getDate() % 3); i++) {
                                accents.add(new DotAccent(10f, null, formatter.format(date) + "-" + i));
                            }
                            map.put(date, accents);
                        }
                        view.setAccents(map);
                    }
                }, 1000);

                Log.i("JavaMainActivity", "onMonthSelected: date = " + date);
            }

            @Override
            public void onDateSelected(@NotNull Date date) {
                Log.i("JavaMainActivity", "onDateSelected: date = " + date);
            }
        });

        // change the actionbar title
        getSupportActionBar().setTitle(formatter.format(calendarView.getMonthCurrent()));
    }
}
