package util.kits;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


public class SimpleCalendar extends GregorianCalendar {

	public SimpleCalendar() {
	}

	public SimpleCalendar(TimeZone zone) {
		super(zone);
	}

	public SimpleCalendar(Locale aLocale) {
		super(aLocale);
	}

	public SimpleCalendar(TimeZone zone, Locale aLocale) {
		super(zone, aLocale);
	}
	
	public SimpleCalendar(int year) {
		super(year, JANUARY, 1);
	}

	public SimpleCalendar(int year, int month) {
		super(year, month, 1);
	}
	
	public SimpleCalendar(int year, int month, int dayOfMonth) {
		super(year, month, dayOfMonth);
	}

	public SimpleCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
		super(year, month, dayOfMonth, hourOfDay, minute);
	}

	public SimpleCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		super(year, month, dayOfMonth, hourOfDay, minute, second);
	}
	
	public SimpleCalendar(Date date) {
		setTime(date);
	}
	
	
	// simple helpers
	public int year() { return get(YEAR); }
	public int month() { return get(MONTH) + 1; }
	public int day() { return get(DAY_OF_MONTH); }
	public int hour() { return get(HOUR_OF_DAY); }
	public int minute() { return get(MINUTE); }
	public int second() { return get(SECOND); }
	public int millisecond() { return get(MILLISECOND); }
	public int dayOfYear() { return get(DAY_OF_YEAR); }
	public int dayOfWeek() { return get(DAY_OF_WEEK); }
	
	
	public boolean isWeekend() {
		return isSaturday() || isSunday();
	}
	
	public boolean isWeekday() {
		return !isSaturday() && !isSunday();
	}
	
	public boolean isSunday() { return dayOfWeek() == SUNDAY; }
	public boolean isMonday() { return dayOfWeek() == MONDAY; }
	public boolean isTuesday() { return dayOfWeek() == TUESDAY; }
	public boolean isWednesday() { return dayOfWeek() == WEDNESDAY; }
	public boolean isThursday() { return dayOfWeek() == THURSDAY; }
	public boolean isFriday() { return dayOfWeek() == FRIDAY; }
	public boolean isSaturday() { return dayOfWeek() == SATURDAY; }
	
	public boolean isFirstDayOfMonth() { return day() == 1; }
	public boolean isLastDayOfMonth() { return day() == getLastDayInMonth(); }

	public int getLastDayInMonth() { return getMaximum(DAY_OF_MONTH); }
	
	public void advanceHour(int n) { add(HOUR_OF_DAY, n); }
	public void advanceMinute(int n) { add(MINUTE, n); }
	public void advanceSecond(int n) { add(SECOND, n); }
	public void advanceDay(int n) { add(DAY_OF_MONTH, n); }
	public void advanceMonth(int n) { add(MONTH, n); }
	public void advanceYear(int n) { add(YEAR, n); }
	
	public void setDay(int dayOfMonth) { 
		if (dayOfMonth <= 0) { dayOfMonth= 1; }
		if (dayOfMonth > getLastDayInMonth()) { dayOfMonth= getLastDayInMonth(); }
		set(DAY_OF_MONTH, dayOfMonth);
	}
	
	public void setMonth(int month) { 
		if (month <= 0) { month= 1; }
		if (month > 12) { month= 12; }
		set(MONTH, month - 1);
	}
	
	public void setYear(int year) { 
		if (year <= 0) { year= 1; }
		if (year > 10000) { year= 10000; }
		set(YEAR, year);
	}
	
	public boolean isToday() {
		SimpleCalendar today= new SimpleCalendar();
		
		if (today.year() == year()) {
			if (today.month() == month()) {
				if (today.day() == day()) { 
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean isYesterday() {
		SimpleCalendar yesterday= new SimpleCalendar();
		yesterday.advanceDay(-1);
		
		if (yesterday.year() == year()) {
			if (yesterday.month() == month()) {
				if (yesterday.day() == day()) { 
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	public boolean isTomorrow() {
		SimpleCalendar tomorrow= new SimpleCalendar();
		tomorrow.advanceDay(1);
		
		if (tomorrow.year() == year()) {
			if (tomorrow.month() == month()) {
				if (tomorrow.day() == day()) { 
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void zeroTime() { 
		set(HOUR_OF_DAY, 0);
		set(MINUTE, 0);
		set(SECOND, 0);
		set(MILLISECOND, 0);
	}
	
	public SimpleCalendar getPreviousMonth() {
		SimpleCalendar month= new SimpleCalendar(get(YEAR), get(MONTH), 1);
		month.zeroTime();
		month.advanceMonth(-1);
		return month;
	}
	
	public int getYear() {
		return get(YEAR);
	}
	
	public int getMonth() {
		return get(MONTH) + 1;
	}
	
	public SimpleCalendar getNextMonth() {
		SimpleCalendar month= new SimpleCalendar(get(YEAR), get(MONTH), 1);
		month.zeroTime();
		month.advanceMonth(1);
		return month;
	}
	
	public String toString() {
		return DateKit.format(this);
	}
	
	public SimpleCalendar getFirstDateOfMonth() {
		SimpleCalendar date= new SimpleCalendar(getYear(), getMonth(), 1);
		return date;
	}
	public SimpleCalendar getLastDateOfMonth() {
		SimpleCalendar date= new SimpleCalendar(getYear(), getMonth(), getLastDayInMonth());
		return date;
	}

}
