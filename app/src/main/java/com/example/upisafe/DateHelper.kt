package com.example.upisafe

import java.sql.Time
import java.util.Calendar
import java.util.Date

class DateHelper {
    companion object {
        fun getDate(Day: Int, Month: Int, Year: Int, Hour: Int, Minute: Int): Date {
            val cor_Month = Month - 1;
            val cal:Calendar = Calendar.getInstance()
            cal.set(Year,cor_Month,Day)
            cal.set(Calendar.HOUR_OF_DAY,Hour)
            cal.set(Calendar.MINUTE,Minute)
            return cal.time
        }
    }
}
