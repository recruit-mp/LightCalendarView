package jp.co.recruit_mp.android.lightcalendarview.kotlinsample

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import jp.co.recruit_mp.android.lightcalendarview.LightCalendarView
import jp.co.recruit_mp.android.lightcalendarview.accent.Accent
import jp.co.recruit_mp.android.lightcalendarview.accent.DotAccent
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var calendarView: LightCalendarView

    private val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarView = findViewById(R.id.calendarView) as? LightCalendarView ?: throw IllegalStateException("calendarView not found")

        calendarView.apply {
            monthFrom = Calendar.getInstance().apply { set(Calendar.MONTH, 0) }.time
            monthTo = Calendar.getInstance().apply { set(Calendar.MONTH, 11) }.time
            monthCurrent = Calendar.getInstance().time

            // set the calendar view callbacks
            onMonthSelected = { date, view ->
                supportActionBar?.apply {
                    title = formatter.format(date)
                }

                // add accents to some days with 1 second delay (simulating I/O delay)
                Handler().postDelayed({
                    val cal = Calendar.getInstance()
                    val dates = (1..31).filter { it % 2 == 0 }.map {
                        cal.apply {
                            set(view.month.year + 1900, view.month.month, it)
                        }.time
                    }
                    val map = mutableMapOf<Date, Collection<Accent>>().apply {
                        dates.forEach { date ->
                            val accents = (0..date.date % 3).map { DotAccent(10f, key = "${formatter.format(date)}-$it") }
                            put(date, accents)
                        }
                    }
                    view.setAccents(map)
                }, 1000)

                Log.i("MainActivity", "onMonthSelected: date = $date")
            }

            onDateSelected = { date -> Log.i("MainActivity", "onDateSelected: date = $date") }
        }


        // change the actionbar title
        supportActionBar?.title = formatter.format(calendarView.monthCurrent)
    }
}
