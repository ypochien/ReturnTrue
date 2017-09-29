package com.returntrue.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.primitives.Doubles;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.returntrue.application.BaseApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by josephwang on 15/8/26.
 */
public class StringsUtils
{
    public static char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static char AccountIllegalChar[] = {'"', '/', '\\', '[', ']', ':', ';', '|', '=', ',', '+', '*', '?', '<', '>'};

    public static String symbolRg = "[\\sA-Za-z0-9@._-]*";
    public static String HostRg = "[A-Za-z0-9._-]+";
    public static String SpeciaAccountCharRg = "[/ \\\\ \\[ \\] : ; | = , + * ? < >]";
    public static String ChineseRg = "[\u4e00-\u9fa5]+";
    public static String alphabeticAndNumberRg = "[a-zA-Z0-9]";
    public static String SymbolRg = "[@._-]";
    public static String AllLetterRg = "[^p{L}p{Nd}]+";
    public static String SiteNameRg = "^(?:\\p{L}\\p{M}*|[A-Za-z0-9\\-\\s@._-])*$";
    // public static String numberRg = "^[0-9]*[1-9][0-9]*$";
    public static String numberRg = "[0-9]";

    public static String AtLastOneAlphabetAndNumber = "^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]+)$";

    public static boolean atLastOneAlphabetAndNumber(String res)
    {
        return res.matches(AtLastOneAlphabetAndNumber);
    }

    public static String twoDigitString(int number)
    {
        if (number == 0)
        {
            return "00";
        }

        if (number / 10 == 0)
        {
            return "0" + number;
        }
        return String.valueOf(number);
    }

    public static boolean isBiggerThanZero(String res)
    {
        if (!TextUtils.isEmpty(res) &&
                TextUtils.isDigitsOnly(res) &&
                Integer.parseInt(res) > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    // 將字符串中的每個字符轉換為十六進制
    private static String toHexString(byte[] bytes, String separator)
    {

        StringBuilder hexstring = new StringBuilder();
        for (byte b : bytes)
        {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1)
            {
                hexstring.append('0');
            }
            hexstring.append(hex);

        }

        return hexstring.toString();
    }

    public static int StringToInteger(String input)
    {
        input = input.replace(".", "");
        if (input != null && !input.equals(""))
        {
            BigDecimal decimal = new BigDecimal(input);
            decimal.setScale(0);
            return decimal.intValue();
        }
        else
        {
            return 0;
        }
    }

    public static int getOmitIndexinString(String text, TextView view)
    {
        view.measure(0, 0);
        int index = 0;
        boolean find = false;
        for (int i = text.length(); i >= 0; i--)
        {
            int computeWidth = computeStringWidth(text.substring(0, i), view.getTextSize()) + view.getPaddingLeft() + view.getPaddingRight();
            if (Math.abs(computeWidth - view.getLayout().getWidth()) <= view.getTextSize())
            {
                find = true;
                index = i;
            }
            if (find)
            {
                break;
            }
        }
        return index;
    }

    public static String getValidatecode()
    {
        return md5(TimeUtil.calendarToString(Calendar.getInstance(), "yyyyMMdd"));
    }

    public static String md5(String string)
    {
        try
        {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            // md5.update(s.getBytes(), 0, s.length());
            md5.reset();
            md5.update(string.getBytes(Charset.forName("UTF-8")));
            // String signature = new BigInteger(1, md5.digest()).toString(16);
            // // JLog.d(JLog.JosephWang, "Signature original " + s + " md5 " +
            // // signature);
            //
            // return signature;
            String result = toHexString(md5.digest(), "");
            JLog.d(JLog.JosephWang, "md5 " + result.toUpperCase());
            return result.toUpperCase();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * @param string
     * @param size
     * @return width of text
     */
    public static int computeStringWidth(String string, float size)
    {
        Rect rect = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(size);
        paint.getTextBounds(string, 0, string.length(), rect);
        return rect.width();
    }

    public static String convert(String str)
    {
        StringBuffer ostr = new StringBuffer();
        for (int i = 0; i < str.length(); i++)
        {
            char ch = str.charAt(i);
            /** Does the char need to be converted to unicode? **/
            if ((ch >= 0x0020) && (ch <= 0x007e))
            {
                ostr.append(ch); // No.
            }
            else
            {
                /** Yes. **/
                ostr.append("\\u"); // standard unicode format.
                /** Get hex value of the char. **/
                String hex = Integer.toHexString(str.charAt(i) & 0xFFFF); //
                for (int j = 0; j < 4 - hex.length(); j++)
                {
                    /** Prepend zeros because unicode requires 4 digits. **/
                    ostr.append("0");
                }
                ostr.append(hex.toLowerCase());
                /** standard unicode format. **/
                // ostr.append(hex.toLowerCase(Locale.ENGLISH));
            }
        }
        return (new String(ostr)); // Return the stringbuffer cast as a string.
    }

    public static boolean isRightInput(EditText edit, String regular)
    {
        return edit.getText().toString().matches(regular);
    }

    public static boolean isRightNameInput(EditText edit)
    {
        return edit.getText().toString().matches(AllLetterRg) || edit.getText().toString().matches(SymbolRg);
    }


    public static InputFilter[] numberFilter(final int maxLimit, final int restrict)
    {
        InputFilter filter = new InputFilter()
        {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
            {
                if (dest.toString().matches(numberRg) && source.length() <= restrict && dest.length() <= restrict && Integer.parseInt(source.toString()) <= maxLimit)
                {
                    JLog.d(JLog.JosephWang, "numberFilter is right source " + source.toString() + " maxLimit " + maxLimit);
                    return source.toString();
                }
                else if (source.toString().matches(numberRg) && Integer.parseInt(source.toString()) > maxLimit)
                {
                    JLog.d(JLog.JosephWang, "numberFilter more than maxLimit source " + source.toString() + " maxLimit " + maxLimit);
                    return String.valueOf(maxLimit);
                }
                else if (dest.length() >= restrict)
                {
                    JLog.d(JLog.JosephWang, "numberFilterdest length >= restrict " + source.toString());
                    return "";
                }
                else
                {
                    JLog.d(JLog.JosephWang, "numberFilterdest else " + source.toString());
                    return "";
                }
            }
        };
        return new InputFilter[]{filter};
    }


    public static boolean hasChinese(EditText edit)
    {
        boolean hasError = false;
        for (int i = 0; i < edit.getText().toString().length(); i++)
        {
            hasError = String.valueOf(edit.getText().toString().charAt(i)).matches(ChineseRg);
            if (hasError)
            {
                break;
            }
        }
        return hasError;
    }

    public static String getInvalidAccountFormatMsg(String res)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(res);
        buffer.append(" \" / \\ [ ] : ; | = , + * ? < > ");
        System.out.println(" buffer " + buffer.toString());
        return buffer.toString();
    }

    public static boolean checkAccountChar(String res)
    {
        for (int i = 0; i < res.length(); i++)
        {
            /*
             * char acsii = res.charAt(i); //a:97 //z:122 //A:65 //Z:90 //0:48
			 * //9:57
			 *
			 * if(!(acsii >= 97 && acsii <= 122) &&!(acsii >= 65 && acsii <= 90)
			 * &&!(acsii>=48 && acsii<=57)) {
			 * if(String.valueOf(acsii).matches(SpeciaAccountCharRg)) return
			 * false; }
			 */

            String s = res.substring(i, i + 1);
            if (s.matches(SpeciaAccountCharRg) || (s.charAt(0) < 0 || s.charAt(0) > 255))
            {
                return false;
            }
        }

        return true;
    }

    public static boolean isRightInput(String res, String regular)
    {
        for (int i = 0; i < res.length(); i++)
        {
            if (!res.substring(i, i + 1).matches(regular))
            {
                return false;
            }
        }

        return true;
    }

    public static boolean notNullOrEmpty(String res)
    {
        if (res != null && !"".equals(res))
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    public static boolean isEmailValid(String email)
    {
        boolean isValid = false;
        String expression = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches())
        {
            isValid = true;
        }
        return isValid;
    }

    public static String byteToHex(byte b)
    {
        return new String(new char[]{hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f]});
    }


    public static boolean isEmptyEdit(EditText edit)
    {
        return (edit.getText().toString().length() == 0);
    }

    public static boolean isMatch(EditText res, String rightRg)
    {
        return res.getText().toString().matches(rightRg);
    }

    public static String charToHex(char c)
    {
        // Returns hex String representation of char c
        byte hi = (byte) (c >>> 8);
        byte lo = (byte) (c & 0xff);
        return byteToHex(hi) + byteToHex(lo);
    }

    public static String slurp(final InputStream is, final int bufferSize)
    {
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        try
        {
            final Reader in = new InputStreamReader(is, "UTF-8");
            try
            {
                for (; ; )
                {
                    int rsz = in.read(buffer, 0, buffer.length);
                    if (rsz < 0)
                    {
                        break;
                    }
                    out.append(buffer, 0, rsz);
                }
            }
            finally
            {
                in.close();
            }
        }
        catch (UnsupportedEncodingException ex)
        {
            /* ... */
        }
        catch (IOException ex)
        {
            /* ... */
        }
        return out.toString();
    }

    public static String ByteToString(byte[] b, int start, int end)
    {
        int size = end - start;
        char[] theChars = new char[size];
        for (int i = 0, j = start; i < size; )
        {
            theChars[i++] = (char) (b[j++] & 0xff);
        }
        return new String(theChars);
    }

    public static String Byte2Hex(byte[] b)
    { // �???��?16?��?�?��
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++)
        {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
            {
                hs = hs + "0" + stmp;
            }
            else
            {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }

    public static String getStringFromJson(JsonObject jsonObject, String key)
    {
        return getStringFromJson(jsonObject, key, "");
    }

    public static String getUTF8StringFromJson(JsonObject jsonObject, String key)
    {
        return unicodeToUtf8(getStringFromJson(jsonObject, key, ""));
    }

    public static String getStringFromJson(JsonObject jsonObject, String key, String defaultString)
    {
        if (jsonObject != null && jsonObject.get(key) != null && !jsonObject.get(key).isJsonNull() && !TextUtils.isEmpty(jsonObject.get(key).getAsString()))
        {
            return jsonObject.get(key).getAsString();
        }
        return defaultString;
    }

    public static float getFloatFromJson(JsonObject jsonObject, String key)
    {
        return getFloatFromJson(jsonObject, key, 0f);
    }

    public static float getFloatFromJson(JsonObject jsonObject, String key, float defaultFloat)
    {
        if (jsonObject != null &&
                jsonObject.get(key) != null &&
                !jsonObject.get(key).isJsonNull())
        {
            return jsonObject.get(key).getAsFloat();
        }
        return defaultFloat;
    }

    public static int getIntegerFromJson(JsonObject jsonObject, String key)
    {
        return getIntegerFromJson(jsonObject, key, -1);
    }

    public static boolean notJsonNull(JsonElement jsonObject)
    {
        return jsonObject != null && !jsonObject.isJsonNull();
    }

    public static int getIntegerFromJson(JsonObject jsonObject, String key, int defaultString)
    {
        if (jsonObject != null && jsonObject.get(key) != null && !jsonObject.get(key).isJsonNull())
        {
            return jsonObject.get(key).getAsInt();
        }
        return defaultString;
    }

    public static String filterHtmlString(String orignal)
    {
        if (!TextUtils.isEmpty(orignal))
        {
            return orignal.replace("\n", "").trim();
        }
        else
        {
            return "";
        }
    }

    public static String getString(int stringId)
    {
        return BaseApplication.getContext().getString(stringId);
    }

    public static String[] getStringArray(int stringArrayId)
    {
        return BaseApplication.getContext().getResources().getStringArray(stringArrayId);
    }

    /**********
     * Need To Recycle
     ******************/
    public static TypedArray getTypedArray(int stringId)
    {
        TypedArray imgList = BaseApplication.getContext().getResources().obtainTypedArray(stringId);
        return imgList;
    }

    /**
     * 檢�?�?��?��??��?�? *
     *
     * @param checkString 欲檢?��?�? * @return boolean
     *                    ?��?串�?????��????false,?��?�???�true
     */
    public static boolean isNumber(String checkString)
    {
        return checkString.matches("\\d*");
    }
    //
    // public static String getMD5Hex(final String inputString) throws
    // NoSuchAlgorithmException {
    //
    // MessageDigest md = MessageDigest.getInstance("MD5");
    // md.update(inputString.getBytes());
    //
    // byte[] digest = md.digest();
    //
    // return convertByteToHex(digest);
    // }
    //
    // private static String convertByteToHex(byte[] byteData) {
    //
    // StringBuilder sb = new StringBuilder();
    // for (int i = 0; i < byteData.length; i++) {
    // sb.append(Integer.toString((byteData[i] & 0xff) + 0x100,
    // 16).substring(1));
    // }
    //
    // return sb.toString();
    // }

    /**
     * unicode轉換成utf-8
     *
     * @param theString
     * @return
     * @author fanhui
     * 2007-3-15
     */
    public static String unicodeToUtf8(String theString)
    {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; )
        {
            aChar = theString.charAt(x++);
            if (aChar == '\\')
            {
                aChar = theString.charAt(x++);
                if (aChar == 'u')
                {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++)
                    {
                        aChar = theString.charAt(x++);
                        switch (aChar)
                        {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed    \\uxxxx    encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                }
                else
                {
                    if (aChar == 't')
                    {
                        aChar = '\t';
                    }
                    else if (aChar == 'r')
                    {
                        aChar = '\r';
                    }
                    else if (aChar == 'n')
                    {
                        aChar = '\n';
                    }
                    else if (aChar == 'f')
                    {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            }
            else
            {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }


    public static float getTextFloat(float dp, Context ctx)
    {
        return dp * ctx.getResources().getDisplayMetrics().density;
    }

    public static float StringToFloat(String values)
    {
        if (!TextUtils.isEmpty(values))
        {
            return new BigDecimal(values).setScale(4, BigDecimal.ROUND_HALF_UP).floatValue();
        }
        else
        {
            return 0;
        }
    }

    public static double StringToDouble(String values)
    {
        if (!TextUtils.isEmpty(values))
        {

            double result = 0d;
            try
            {
                result = Double.parseDouble(values);
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
            return result;
        }
        else
        {
            return 0;
        }
    }

    public static int StringToInt(String values)
    {
        if (!TextUtils.isEmpty(values) && TextUtils.isDigitsOnly(values))
        {
            return Integer.parseInt(values);
        }
        else
        {
            return 0;
        }
    }

    public static float StringToFloat(String values, float defautValue)
    {
        return StringToFloat(values, defautValue, 4);
    }


    public static float StringToFloat(String values, float defautValue, int scale)
    {
        if (!TextUtils.isEmpty(values))
        {
            return new BigDecimal(values).setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
        }
        else
        {
            return defautValue;
        }
    }

    public static float StringToNoneZeroFloat(String values, float defautValue)
    {
        if (!TextUtils.isEmpty(values))
        {
            float result = new BigDecimal(values).setScale(4, BigDecimal.ROUND_HALF_UP).floatValue();
            if (result > 0)
            {
                return result;
            }
            else
            {
                return 1f;
            }
        }
        else
        {
            return defautValue;
        }
    }

    /**
     * BigDecimal.ROUND_CEILING　正數無條件進入,負數無條件捨去
     * BigDecimal.ROUND_DOWN 　無條件捨去到 scale 位
     * BigDecimal.ROUND_FLOOR　正數無條件捨去，負數無條件進入
     * BigDecimal.ROUND_HALF_DOWN　四捨五捨六入
     * BigDecimal.ROUND_HALF_EVEN　四捨六入,五入捨後該scale位數值必需為偶數
     * BigDecimal.ROUND_HALF_UP　四捨五入
     * BigDecimal.ROUND_UP　無條件進入到 scale 位
     */
    public static float StringToFloat(String values, int digital)
    {
        if (!TextUtils.isEmpty(values))
        {
            return new BigDecimal(values).setScale(digital, BigDecimal.ROUND_HALF_UP).floatValue();
        }
        else
        {
            return 0f;
        }
    }

    public static String handleString(String values)
    {
        if (!TextUtils.isEmpty(values))
        {
            return values;
        }
        return "";
    }

    public static long fib(int n)
    {
        if (n <= 1)
        {
            return n;
        }
        else
        {
            return fib(n - 1) + fib(n - 2);
        }
    }

    public static String ratioFormat(float input, int position)
    {
        NumberFormat ratioFormat = NumberFormat.getInstance();
        ratioFormat.setMaximumFractionDigits(position);    //小數後兩位
        return ratioFormat.format(input);
    }

    public static String dollerAndRatioFormat(String input, int position)
    {
        double value = 0d;
        if (!TextUtils.isEmpty(input))
        {
            Double result = Doubles.tryParse(input);
            if (result != null)
            {
                value = result;
            }
        }
        return dollerAndRatioFormat(value, position).replace(".0", "");
    }

    public static String dollerFormat(String input)
    {
        double value = 0d;
        if (!TextUtils.isEmpty(input))
        {
            Double result = Doubles.tryParse(input);
            if (result != null)
            {
                value = result;
            }
            else
            {
                try
                {
                    value = Integer.parseInt(input);
                }
                catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return dollerAndRatioFormat(value, 0).replace(".0", "");
    }

    public static String dollerAndRatioFormat(double input, int position)
    {
        String inputString = "" + input;
        StringBuffer result = new StringBuffer();
        NumberFormat ratioFormat = NumberFormat.getInstance();
        ratioFormat.setMaximumFractionDigits(0);    //小數後兩位

        if (inputString.contains(".") &&
                inputString.substring(inputString.indexOf(".")).length() > 1)//有小數
        {
            String number = inputString.substring(0, inputString.indexOf("."));
            result.append(ratioFormat.format(Integer.parseInt(number)));
            int decimalSize = inputString.substring(inputString.indexOf(".") + 1).length();
            if (decimalSize < position)
            {
                String rate = inputString.substring(inputString.indexOf(".") + 1);
                if (decimalSize < position)
                {
                    for (int i = 0; i < Math.abs(decimalSize - position); i++)
                    {
                        rate = rate + "0";
                    }
                }
                result.append(".");
                result.append(rate);
            }
            else
            {
                String rate = inputString.substring(inputString.indexOf(".") + 1);
                result.append(".");
                result.append(rate);
            }
        }
        else
        {
            result.append(ratioFormat.format(input));
        }
        //小數後兩位
        return result.toString();
    }

    public static String getAppendString(String... res)
    {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < res.length; i++)
        {
            result.append(res[i]);
        }
        return result.toString();
    }

    public static String getEarningRate(String res)
    {
        if (Const.NONE_VALUE_TAG.equals(res))
        {
            return res;
        }
        else
        {
            return res + "%";
        }
    }
}
