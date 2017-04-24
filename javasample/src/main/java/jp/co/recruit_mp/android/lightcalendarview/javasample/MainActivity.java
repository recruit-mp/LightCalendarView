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
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

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
        calendarView.setLocale(Locale.US);
        // 前月・翌月を表示する
        calendarView.setDisplayOutside(true);
        // 当日の強調表示固定
        calendarView.setFixToday(true);

        calendarView.setOnMonthSelected(new Function2<Date, MonthView, Unit>() {
            @Override
            public Unit invoke(Date date, final MonthView monthView) {
                getSupportActionBar().setTitle(formatter.format(date));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Calendar cal = Calendar.getInstance();
                        List<Date> dates = new ArrayList<Date>();
                        List<Date> holidays = new ArrayList<Date>();
                        for (int i = 0; i < 31; i++) {
                            if (i % 2 == 0) {
                                cal.set(monthView.getMonth().getYear() + 1900, monthView.getMonth().getMonth(), i);
                                dates.add(cal.getTime());
                            }
                            if (i < 7) {
                                cal.set(monthView.getMonth().getYear() + 1900, monthView.getMonth().getMonth(), i);
                                holidays.add(cal.getTime());
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
                        monthView.setAccents(map);
                        // 祝日を設定
                        monthView.setHolidays(holidays);
                    }
                }, 1000);

                Log.i("JavaMainActivity", "onMonthSelected: date = " + date);
                return null;
            }
        });

        calendarView.setOnDateSelected(new Function1<Date, Unit>() {
            @Override
            public Unit invoke(Date date) {
                Log.i("JavaMainActivity", "onDateSelected: date = " + date);
                return null;
            }
        });


        // change the actionbar title
        getSupportActionBar().setTitle(formatter.format(calendarView.getMonthCurrent()));
    }
}
