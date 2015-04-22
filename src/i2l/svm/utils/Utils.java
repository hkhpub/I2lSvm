package i2l.svm.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class Utils {

	public static double atof(String s) {
		double d = Double.valueOf(s).doubleValue();
		if (Double.isNaN(d) || Double.isInfinite(d)) {
			System.err.print("NaN or Infinity in input\n");
			System.exit(1);
		}
		return (d);
	}

	public static int atoi(String s) {
		return Integer.parseInt(s);
	}

	public static void info(String s) {
		System.out.print(s);
		System.out.flush();
	}
	
	public static String getTimeTag() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(System.currentTimeMillis()));
		String year = String.format("%4d", calendar.get(Calendar.YEAR));
		String month = String.format("%02d", calendar.get(Calendar.MONTH)+1);
		
		String date = String.format("%02d", calendar.get(Calendar.DATE));
		String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
		String minute = String.format("%02d", calendar.get(Calendar.MINUTE));
		String second = String.format("%02d", calendar.get(Calendar.SECOND));
		
		return year+month+date+"-"+hour+minute+second;
	}
	
	public static void removeVector(Vector<Integer> vector, int fromIndex, int toIndex) {
		for (int i=0; i<vector.size(); i++) {
			if (i >= fromIndex && i < toIndex) {
				vector.remove(i);
			}
		}
	}
	public static Vector<Integer> subVector(Vector<Integer> vector, int fromIndex, int toIndex) {
		Vector<Integer> sub = new Vector<Integer>();
		for (int i=0; i<vector.size(); i++) {
			if (i >= fromIndex && i < toIndex) {
				sub.add(vector.get(i));
			}
		}
		return sub;
	}
}
