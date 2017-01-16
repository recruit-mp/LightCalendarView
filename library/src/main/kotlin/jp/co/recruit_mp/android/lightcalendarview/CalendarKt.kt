package jp.co.recruit_mp.android.lightcalendarview

import java.util.*

/**
 * Created by recruit-mahayash on 2017/01/13.
 */
class CalendarKt : GregorianCalendar() {
    companion object {
        fun getInstance(settings: CalendarSettings): Calendar = getInstance(settings.timeZone, settings.locale)
    }
}