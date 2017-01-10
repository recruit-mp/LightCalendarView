# Light Calendar View

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-LightCalendarView-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/4696)
[![Release](https://img.shields.io/github/release/recruit-mp/LightCalendarView.svg?label=maven%20version)](https://github.com/recruit-mp/LightCalendarView)
[![License](https://img.shields.io/hexpm/l/plug.svg)]()

A lightweight monthly calendar view for Android, fully written in Kotlin. Designed to meet the minimum demands for typical calendars.

<img alt="screenshot" src="https://cloud.githubusercontent.com/assets/21093614/18807459/a6692ca4-8282-11e6-921d-1ea46c545ed4.gif" width="300" />

## Requirements
* Target SDK version: 23
* Minimum SDK version: 15

## Usage

1) Add these settings to your app-level **build.gradle**.
```groovy
dependencies {
    compile 'jp.co.recruit_mp:LightCalendarView:1.0.0'
}
```

2) Add `LightCalendarView` into your layouts or view hierarchy.
```xml
<layout xmlns:android="http://schemas.android.com/apk/res/android">
    ...
        <jp.co.recruit_mp.android.lightcalendarview.LightCalendarView
            android:id="@+id/calendarView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />
    ...
</layout>
```

## Customization

### Options

Lightweight Calendar View can be easily customized by setting properties in the layout XML file.
```xml
    <jp.co.recruit_mp.android.lightcalendarview.LightCalendarView
        android:id="@+id/calendarView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:lcv_weekDayTextSize="18sp"
        app:lcv_dayTextSize="18sp"
        app:lcv_textColor="@color/calendar_day_text"
        app:lcv_selectionColor="@color/calendar_selection"
        app:lcv_accentColor="@color/calendar_accent"/>
```

The following properties are available:

| property name              | type              | description                                                         |
| -------------------------- | ----------------- | ------------------------------------------------------------------- |
| app:lcv_weekDayTextSize    | dimension         | The text size of weekdays                                           |
| app:lcv_dayTextSize        | dimension         | The text size of days                                               |
| app:lcv_textColor          | color or resource | The text color of weekdays and days                                 |
| app:lcv_selectionColor     | color or resource | The background color of selections                                  |
| app:lcv_accentColor        | color or resource | The color of accents                                                |
| app:lcv_firstDayOfWeek     | integer           | The first day of the week (0 = Sunday, 1 = Monday, ..., 6 = Friday) |

The customizations of text colors of selected days or today are done by setting &lt;selector /&gt; color resources to `app:lcv_textColor`, `app:lcv_selectionColor`, or `app:lcv_accentColor` with the following resource files for example.

**NOTE** that in all of the color resources, `android:state_*` are used to distinguish the state of each day view as follows:

| state                      | description                                                                   |
| -------------------------- | ----------------------------------------------------------------------------- |
| android:state_active       | indicates whether the day view is for today (_true_) or other days (_false_)  |
| android:state_selected     | indicates whether the day view is selected (_true_) or not (_false_)          |

`/res/color/calendar_day_text.xml`
```xml
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- the color of today when selected -->
    <item android:color="#ffd2d2" android:state_active="true" android:state_selected="true" />

    <!-- the color of today -->
    <item android:color="#c417ce" android:state_active="true" />

    <!-- the color of the selected day -->
    <item android:color="#ffffff" android:state_selected="true" />

    <!-- the color of other days -->
    <item android:color="#7c7c7c" />
</selector>
```

`/res/color/calendar_selection.xml`
```xml
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- the background color of today when selected -->
    <item android:color="#3944ba" android:state_active="true" android:state_selected="true" />

    <!-- the background color of the other days when selected -->
    <item android:color="#565656" android:state_selected="true" />
</selector>
```

`/res/color/calendar_accent.xml`
```xml
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- the color of accents in today when selected -->
    <item android:color="#ffd2d2" android:state_active="true" android:state_selected="true" />

    <!-- the color of accents in today -->
    <item android:color="#c417ce" android:state_active="true" />

    <!-- the color of accents in other days when selected -->
    <item android:color="#ffffff" android:state_selected="true" />

    <!-- the color of accents in other days -->
    <item android:color="#7c7c7c" />
</selector>
```

### OnCalendarStateUpdatedListener
Add `OnCalendarStateUpdatedListener` to the calendar view to detect and handle the following events:

* `onSelectMonth()`: the user swipes and changes the month
* `onSelectDay()`: the user clicks one day in a month view

See [the example implementation in MainActivity.kt](sample/src/main/kotlin/jp/co/recruit_mp/android/lightcalendarview/sample/MainActivity.kt) of this listener on github.

### Decorations

Use `MonthView#setAccents(Date, Collection<Accent>)` or `MonthView#setAccents(Map<Date, Collection<Accent>>)` to add decorations to each day.

Example:
```kotlin
calendarView.setOnStateUpdatedListener(object : LightCalendarView.OnStateUpdatedListener {
    override fun onMonthSelected(date: Date, view: MonthView) {
        val cal = Calendar.getInstance().apply { time = date }
        val monthEvents: Map<Date, List<Event>> = someApi.getMonthEvents(cal[Calendar.YEAR], cal[Calendar.MONTH])
        val monthAccents: Map<Date, List<Accent>> = monthEvents.mapValues { event -> DotAccent(radius = 10f, color = event.color, key = event) }
        view.setAccents(monthAccents)
    }
}
```

### Coloring Day of The Week

Use `LightCalendarView#setWeekDayFilterColor(weekDay: WeekDay, color: Int?)` and `LightCalendarView#setDayFilterColor(weekDay: WeekDay, color: Int?)` to set the color scheme of day of the week in WeekDayView (e.g. Sunday, Monday, ...) and DayView (e.g. 1, 2, ...) respectively.

```kotlin
// coloring "sunday" (day of the week) in red
calendarView.setWeekDayFilterColor(WeekDay.SUNDAY, Color.RED)

// coloring sundays (days) in red
calendarView.setDayFilterColor(WeekDay.SUNDAY, Color.RED)
```

Note the library internally uses these color as ColorFilter, meaning it is overlayed on top of the text color set through `LightCalendarView#setTextColor(color: Int)`.

### Further Customizations

See [the Wiki](https://github.com/recruit-mp/LightCalendarView/wiki) for more information and descriptions on futher customizations.

## Contributing
Contributions to this library, including bug-reporting, bug-fixes, and enhancements are always welcomed. Just create an issue, fork us and send a pull request.

### License
```
Copyright 2016 RECRUIT MARKETING PARTNERS CO., LTD.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

### Maintainers
Light Calendar View is owned and maintained by Recruit Marketing Partners Co., Ltd, and was originally created by

* [Masayuki Hayashi](https://github.com/tglovernuppy)
* [Yuki Mima](https://github.com/amyu)
