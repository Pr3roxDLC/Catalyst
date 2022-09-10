package com.krazzzzymonkey.catalyst.managers.accountManager.tools;

import com.krazzzzymonkey.catalyst.managers.accountManager.legacySupport.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class JavaTools {
	private static double getJavaVersion(){
		String version = System.getProperty("java.version");
		int pos = version.indexOf('.');
		pos = version.indexOf('.', pos+1);
		return Double.parseDouble(version.substring(0, pos));
	}
	public static ILegacyCompat getJavaCompat(){
		if(getJavaVersion() >= 1.8){
			return new NewJava();
		}else{
			return new OldJava();
		}
	}
    public static String getFormattedDate(int[] date) {
        DateTimeFormatter format = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        LocalDate Date = LocalDateTime.now().withDayOfMonth(date[1]).withMonth(date[0]).withYear(date[2]).toLocalDate();
        return Date.format(format);
    }
}
