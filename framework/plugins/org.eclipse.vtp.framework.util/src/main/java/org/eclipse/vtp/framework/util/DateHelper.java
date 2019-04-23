package org.eclipse.vtp.framework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DateHelper
{
	private static String[] datePatterns = new String[] { "M/d/yyyy",
														  "M-d-yyyy",
														  "M.d.yyyy"};
	private static String[] timePatterns = new String[] { "h:mm:ss a z",
														  "H:mm:ss z",
														  "h:mm:ss a",
														  "H:mm:ss",
														  "h:mm a z",
														  "H:mm z",
														  "h:mm a",
														  "H:mm"};
	
	public static Calendar parseDate(String dateString)
	{
		Calendar cal = parseDate0(dateString);
		if(cal != null)
		{
			int index = dateString.indexOf("GMT");
			if(index >= 0)
			{
				System.out.println("found GMT");
				String tzOffsetString = dateString.substring(index);
				TimeZone tzOffset = TimeZone.getTimeZone(tzOffsetString);
				cal.setTimeZone(tzOffset);
			}
		}
		System.out.println("in parseDate : "+ cal);
		return cal;
	}
	
	private static Calendar parseDate0(String dateString)
	{
		System.out.println("parsing date and time patterns");
		for(String datePattern : datePatterns)
		{
			System.out.println(datePattern);
			for(String timePattern : timePatterns)
			{
				System.out.println(timePattern);
				SimpleDateFormat sdf = new SimpleDateFormat(datePattern + " " + timePattern);
				try
				{
					sdf.parse(dateString);
					return sdf.getCalendar();
				}
				catch (ParseException e)
				{
				}
			}
		}
		System.out.println("parsing date patterns");
		for(String datePattern : datePatterns)
		{
			System.out.println(datePattern);
			SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
			try
			{
				sdf.parse(dateString);
				return sdf.getCalendar();
			}
			catch (ParseException e)
			{
			}
		}
		System.out.println("parsing time patterns");
		for(String timePattern : timePatterns)
		{
			System.out.println(timePattern);
			SimpleDateFormat sdf = new SimpleDateFormat(timePattern);
			try
			{
				sdf.parse(dateString);
				return sdf.getCalendar();
			}
			catch (ParseException e)
			{
			}
		}
		return null;
	}
	
	public static String toDateString(Calendar cal)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(datePatterns[0] + " " + timePatterns[0]);
		sdf.setTimeZone(cal.getTimeZone());
		return sdf.format(cal.getTime());
	}
}
