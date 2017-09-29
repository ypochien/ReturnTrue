package com.returntrue.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.TextView;

import com.returntrue.R;
import com.returntrue.application.BaseApplication;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralUtil
{

	public enum DecimalType
	{
		Positive , Negative, Zero, NoneDecimal;
	}

	public static int getRandomInRange(int min, int max)
	{
		int diff = max - min;
		SecureRandom rn = new SecureRandom();
		int result = rn.nextInt(diff + 1);
		result += min;
		return result;
	}

	public static boolean getBoolean(String res)
	{
		boolean isRight = false;
		if (!TextUtils.isEmpty(res))
		{
			if (res.equals("1") || res.equals("true"))
			{
				isRight = true;
			}
		}
		return isRight;
	}

	public static boolean hasInternet(Context context)
	{
		if (null == context)
		{
			JLog.d(JLog.JosephWang, ("Null context"));
			return false;
		}
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getActiveNetworkInfo();
		JLog.d(JLog.JosephWang, "hasInternet info null? " + (info == null));
		if (info != null)
		{
			JLog.d(JLog.JosephWang, "hasInternet info isConnected? " + (info.isConnected()));
			JLog.d(JLog.JosephWang, "hasInternet info isAvailable? " + (info.isAvailable()));
		}
		if (info == null || !info.isConnected() || !info.isAvailable())
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public static Calendar stringToCalendar(String dateString)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try
		{
			date = sdf.parse(dateString);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.setTimeInMillis(date.getTime());
		return calendar;
	}


	public static DecimalType setFloatValues(TextView view, String testStr)
	{
		DecimalType type = DecimalType.NoneDecimal;
		if (!TextUtils.isEmpty(testStr))
		{
			float yield = Float.parseFloat(testStr);
			if (yield < 0)
			{
				type = DecimalType.Negative;
				view.setTextColor(getColor(R.color.Green));
			}
			else if (yield > 0)
			{
				type = DecimalType.Positive;
				view.setTextColor(getColor(R.color.LogoRed));
			}
			else if (yield == 0)
			{
				type = DecimalType.Zero;
				view.setTextColor(getColor(R.color.Gray));
			}
			view.setText("" + yield);
		}
		else
		{
			type = DecimalType.NoneDecimal;
			view.setText("");
		}
		return type;
	}

	public static DecimalType setFloatValues(TextView view, String yieldStr, int position, boolean changeColor)
	{
		DecimalType type = DecimalType.NoneDecimal;
		if (!TextUtils.isEmpty(yieldStr))
		{
			float yield = Float.parseFloat(yieldStr);
			int color = JUtil.getColor(R.color.Black);
			String rate = StringsUtils.ratioFormat(yield, position);
			String result = String.valueOf(yield);
			if (rate.contains("-"))
			{
				result = String.valueOf(yield).substring(1);
			}

			if (rate.contains(".") && rate.substring(rate.indexOf(".")).length() > 1 && rate.substring(rate.indexOf(".")).length() <=position)
			{
				int decimalSize = result.substring(result.indexOf(".") + 1).length();
				if (decimalSize < position)
				{
					for (int i = 0; i < Math.abs(decimalSize - position); i++)
					{
						rate = rate + "0";
					}
				}
			}

			if (yield < 0)
			{
				type = DecimalType.Negative;
				color = (JUtil.getColor(R.color.Green));
			}
			else if (yield > 0)
			{
				type = DecimalType.Positive;
				color = (JUtil.getColor(R.color.LogoRed));
			}
			else if (yield == 0)
			{
				type = DecimalType.Zero;
				color = (JUtil.getColor(R.color.Black));
			}

			if (changeColor)
			{
				view.setTextColor(color);
			}
			if (rate.contains("-"))
			{
				view.setText("" + rate);
			}
			else
			{
				view.setText(BaseApplication.getContext().getString(R.string.white_space) + rate);
			}
		}
		else
		{
			type = DecimalType.NoneDecimal;
			view.setText("");
		}
		return type;
	}


	public static void setText(TextView view, String testStr)
	{
		if (!TextUtils.isEmpty(testStr))
		{
			view.setText(testStr);
		}
		else
		{
			view.setText("");
		}
	}

	public static int getRandIntInRange(int max)
	{

		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - 0) + 1) + 0;

		return randomNum;
	}

	public static boolean isRightInputs(String input, String pattern)
	{
		Pattern resular =
				Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = resular.matcher(input);
		return matcher.find();
	}

	public static int getColor(int color)
	{
		return BaseApplication.getContext().getResources().getColor(color);
	}
}