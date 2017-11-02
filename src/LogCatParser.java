import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

/**
 * 
 */

/**
 * 
 */
public class LogCatParser implements ILogParser
{
    final String TOKEN_KERNEL= "<>[]";
    final String TOKEN_SPACE = " ";
    final String TOKEN_SLASH = "/";
    final String TOKEN       = "/()";
    final String TOKEN_PID   = "/() ";
    final String TOKEN_MESSAGE = "'";
    
    public Color getColor(LogInfo logInfo)
    {
        if(logInfo.m_strLogLV == null) return Color.BLACK;
        
        if(logInfo.m_strLogLV.equals("FATAL") || logInfo.m_strLogLV.equals("F"))
            return new Color(LogColor.COLOR_FATAL);
        if(logInfo.m_strLogLV.equals("ERROR") || logInfo.m_strLogLV.equals("E") || logInfo.m_strLogLV.equals("3"))
            return new Color(LogColor.COLOR_ERROR);
        else if(logInfo.m_strLogLV.equals("WARN") || logInfo.m_strLogLV.equals("W") || logInfo.m_strLogLV.equals("4"))
            return new Color(LogColor.COLOR_WARN);
        else if(logInfo.m_strLogLV.equals("INFO") || logInfo.m_strLogLV.equals("I") || logInfo.m_strLogLV.equals("6"))
            return new Color(LogColor.COLOR_INFO);
        else if(logInfo.m_strLogLV.equals("DEBUG") || logInfo.m_strLogLV.equals("D") || logInfo.m_strLogLV.equals("7"))
            return new Color(LogColor.COLOR_DEBUG);
        else if(logInfo.m_strLogLV.equals("0"))
            return new Color(LogColor.COLOR_0);
        else if(logInfo.m_strLogLV.equals("1"))
            return new Color(LogColor.COLOR_1);
        else if(logInfo.m_strLogLV.equals("2"))
            return new Color(LogColor.COLOR_2);
        else if(logInfo.m_strLogLV.equals("5"))
            return new Color(LogColor.COLOR_5);
        else
            return Color.BLACK;
    }

    public int getLogLV(LogInfo logInfo)
    {
        if(logInfo.m_strLogLV == null) return LogInfo.LOG_LV_VERBOSE;
        
        if(logInfo.m_strLogLV.equals("FATAL") || logInfo.m_strLogLV.equals("F"))
            return LogInfo.LOG_LV_FATAL;
        if(logInfo.m_strLogLV.equals("ERROR") || logInfo.m_strLogLV.equals("E"))
            return LogInfo.LOG_LV_ERROR;
        else if(logInfo.m_strLogLV.equals("WARN") || logInfo.m_strLogLV.equals("W"))
            return LogInfo.LOG_LV_WARN;
        else if(logInfo.m_strLogLV.equals("INFO") || logInfo.m_strLogLV.equals("I"))
            return LogInfo.LOG_LV_INFO;
        else if(logInfo.m_strLogLV.equals("DEBUG") || logInfo.m_strLogLV.equals("D"))
            return LogInfo.LOG_LV_DEBUG;
        else
            return LogInfo.LOG_LV_VERBOSE;
    }
    
//04-17 09:01:18.910 D/LightsService(  139): BKL : 106
    public boolean isNormal(String strText)
    {
        if(strText.length() < 22) return false;

        String strLevel = (String)strText.substring(19, 21);
        if(strLevel.equals("D/")
                || strLevel.equals("V/")
                || strLevel.equals("I/")
                || strLevel.equals("W/")
                || strLevel.equals("E/")
                || strLevel.equals("F/")
                )
            return true;
        return false;
    }

//04-20 12:06:02.125   146   179 D BatteryService: update start    
    public boolean isThreadTime(String strText)
    {
        if(strText.length() < 34) return false;

        if (isThreadTime1(strText)) return true;
        if (isThreadTime2(strText)) return true;
        
        return false;
    }
    
    private boolean isThreadTime1(String strText) {
        
        if(strText.length() < 33) return false;
        
        StringTokenizer stk = new StringTokenizer(strText, TOKEN_SPACE, false);
        
        final int INDEX_OF_LOG_LEVEL = 4;
        
        for (int count = 0
        		; stk.hasMoreElements() && count <= INDEX_OF_LOG_LEVEL
        		; count++) {
        		
        	String tmp = stk.nextToken();
        	
        	if (count == INDEX_OF_LOG_LEVEL) {
	              if(tmp.equals("D")
		              || tmp.equals("V")
		              || tmp.equals("I")
		              || tmp.equals("W")
		              || tmp.equals("E")
		              || tmp.equals("F")
	              )
	            	  return true;        		
        	}
        }

        return false;
    }
    
    // To Support Logback-android style
    // Support Log Level - TRACE, DEBUG, INFO, WARN, ERROR
    private boolean isThreadTime2(String strText) {
        
        if(strText.length() < 36) return false;
        
        String strLevel = (String)strText.substring(31, 36);
        
        return strLevel.equals("DEBUG") ? true
                : strLevel.equals("TRACE") ? true
                : strLevel.equals("INFO ") ? true
                : strLevel.equals("WARN ") ? true
                : strLevel.equals("ERROR") ? true : false;
    }
    
//    <4>[19553.494855] [DEBUG] USB_SEL(1) HIGH set USB mode 
    public boolean isKernel(String strText)
    {
        if(strText.length() < 18) return false;

        String strLevel = (String)strText.substring(1, 2);
        if(strLevel.equals("0")
                || strLevel.equals("1")
                || strLevel.equals("2")
                || strLevel.equals("3")
                || strLevel.equals("4")
                || strLevel.equals("5")
                || strLevel.equals("6")
                || strLevel.equals("7")
                )
            return true;
        return false;
    }
    
    // Aha radio client log
    // 2015-05-15 09:31:58.581    305 AHA-BINARY-QueryContentParametersCommand Param code : 400f paramValue :
    // 2015-05-15 10:05:28.903  14588 StationManagerImpl addStation
    public boolean isAhaClientAndroid(String strText)
    {
        boolean ret = false;
        
        if(strText.length() < 31) return false;

        String strLevel = (String)strText.substring((31-1));
        if(strLevel != null && !strLevel.equals(""))
            ret = true;
        
        return ret;
    }
    
    // Aha radio client log, after 5.4
    // [ 2015-07-25 22:27:59.204 <   344> < INFO  > ] AHA_BINARY-BPService :: BTLINK Before Read buff.length = 2, offset = 0, size = 2
    public boolean isAhaClientAndroid2(String strText)
    {
        
        if(strText.length() < 44) return false;
        if(!strText.startsWith("[")) return false;
        
        String strLevel = (String)strText.substring(35, 44);
        
        return    strLevel.equals("<VERBOSE>") ? true
                : strLevel.equals("<WARNING>") ? true
                : strLevel.equals("< DEBUG >") ? true
                : strLevel.equals("< INFO  >") ? true
                : strLevel.equals("< ERROR >") ? true : false;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    public LogInfo getNormal(String strText)
    {
        LogInfo logInfo = new LogInfo();
        
        StringTokenizer stk = new StringTokenizer(strText, TOKEN_PID, false);
        if(stk.hasMoreElements())
            logInfo.m_strDate = stk.nextToken();
        if(stk.hasMoreElements())
            logInfo.m_strTime = stk.nextToken();
        if(stk.hasMoreElements())
            logInfo.m_strLogLV = stk.nextToken().trim();
        if(stk.hasMoreElements())
            logInfo.m_strTag = stk.nextToken();
        if(stk.hasMoreElements())
            logInfo.m_strPid = stk.nextToken().trim();
        if(stk.hasMoreElements())
        {
            logInfo.m_strMessage = stk.nextToken(TOKEN_MESSAGE);
            while(stk.hasMoreElements())
            {
                logInfo.m_strMessage += stk.nextToken(TOKEN_MESSAGE);
            }
            logInfo.m_strMessage = logInfo.m_strMessage.replaceFirst("\\): ", "");
        }
        logInfo.m_TextColor = getColor(logInfo);
        return logInfo;
    }

    public LogInfo getThreadTime(String strText)
    {
        LogInfo logInfo = new LogInfo();
        
        StringTokenizer stk = new StringTokenizer(strText, TOKEN_SPACE, false);
        if(stk.hasMoreElements())
            logInfo.m_strDate = stk.nextToken();
        if(stk.hasMoreElements())
            logInfo.m_strTime = stk.nextToken();
        if(stk.hasMoreElements())
            logInfo.m_strPid = stk.nextToken().trim();
        if(stk.hasMoreElements())
            logInfo.m_strThread = stk.nextToken().trim();
        if(stk.hasMoreElements())
            logInfo.m_strLogLV = stk.nextToken().trim();
        if(stk.hasMoreElements())
            logInfo.m_strTag = stk.nextToken();
        if(stk.hasMoreElements())
        {
            logInfo.m_strMessage = stk.nextToken(TOKEN_MESSAGE);
            while(stk.hasMoreElements())
            {
                logInfo.m_strMessage += stk.nextToken(TOKEN_MESSAGE);
            }
            logInfo.m_strMessage = logInfo.m_strMessage.replaceFirst("\\): ", "");
        }
        logInfo.m_TextColor = getColor(logInfo);
        return logInfo;
    }

//  <4>[19553.494855] [DEBUG] USB_SEL(1) HIGH set USB mode 
    public LogInfo getKernel(String strText)
    {
        LogInfo logInfo = new LogInfo();
        
        StringTokenizer stk = new StringTokenizer(strText, TOKEN_KERNEL, false);
        if(stk.hasMoreElements())
            logInfo.m_strLogLV = stk.nextToken();
        if(stk.hasMoreElements())
            logInfo.m_strTime = stk.nextToken();
        if(stk.hasMoreElements())
        {
            logInfo.m_strMessage = stk.nextToken(TOKEN_KERNEL);
            while(stk.hasMoreElements())
            {
                logInfo.m_strMessage += " " + stk.nextToken(TOKEN_SPACE);
            }
            logInfo.m_strMessage = logInfo.m_strMessage.replaceFirst("  ", "");
        }
        logInfo.m_TextColor = getColor(logInfo);
        return logInfo;
    }
    
    private static boolean isStringDateYYYYMMdd(String s) {
        
        boolean ret = false;
        try {
            SimpleDateFormat date = new SimpleDateFormat("YYYY-MM-dd");
            date.parse(s);
            
            ret = true;
        } catch (ParseException e) {
            ret = false;
        }
        
        return ret;
    }
    
    private static boolean isStringDateHHmmssSSS(String s) {
        
        boolean ret = false;

        try {
            SimpleDateFormat date = new SimpleDateFormat("hh:mm:ss.SSS");
            date.parse(s);
            ret = true;
        } catch (ParseException e) {
            ret = false;
        }
        
        return ret;
    }
    
    private static boolean isStringDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    // aha client
    // 2015-05-15 10:05:28.903  14588 StationManagerImpl addStation
    // 04-20 12:06:02.125   146   179 D BatteryService: update start
    public LogInfo getAhaClientAndroid(String strText)
    {
        LogInfo logInfo = new LogInfo();
        
//        System.out.println("text full: " + strText);
        
        // check date
        if (LogCatParser.isStringDouble(strText.substring(0, 2)) == false) {
            logInfo.m_strMessage = "" + strText;
            return logInfo;
        }
        
        StringTokenizer stk = new StringTokenizer(strText, TOKEN_SPACE, false);
        
        if(stk.hasMoreElements()) {
            
            String s = stk.nextToken();
            
            if (LogCatParser.isStringDateYYYYMMdd(s))
                logInfo.m_strDate = s;
            else {
                logInfo.m_strMessage = "" + strText;
                return logInfo;
            }
        }
        
        System.out.println("date: " + logInfo.m_strDate);
        
        if(stk.hasMoreElements()) {
            String s = stk.nextToken();
            
            if (LogCatParser.isStringDateHHmmssSSS(s))
                logInfo.m_strTime = s;
            else {
                logInfo.m_strMessage = "" + strText;
                return logInfo;
            }
        }
        
        System.out.println("time: " + logInfo.m_strTime);
        
        if(stk.hasMoreElements())
            logInfo.m_strPid = stk.nextToken().trim();
        
        System.out.println("pid: " + logInfo.m_strPid);
        
//        if(stk.hasMoreElements())
//            logInfo.m_strThread = stk.nextToken().trim();
//        if(stk.hasMoreElements())
//            logInfo.m_strLogLV = stk.nextToken().trim();
        if(stk.hasMoreElements())
            logInfo.m_strTag = stk.nextToken();
        
        System.out.println("tag: " + logInfo.m_strTag);
        
        if(stk.hasMoreElements())
        {
            logInfo.m_strMessage = stk.nextToken(TOKEN_MESSAGE);
            while(stk.hasMoreElements())
            {
                logInfo.m_strMessage += stk.nextToken(TOKEN_MESSAGE);
            }
            logInfo.m_strMessage = logInfo.m_strMessage.replaceFirst("\\): ", "");
        }
        logInfo.m_TextColor = getColor(logInfo);
        return logInfo;
    }
    
    // Aha radio client log, after 5.4
    // [ 2015-07-25 22:27:59.204 <   344> < INFO  > ] AHA_BINARY-BPService :: BTLINK Before Read buff.length = 2, offset = 0, size = 2
    public LogInfo getAhaClientAndroid2(String _strText)
    {
        LogInfo logInfo = new LogInfo();
        
        System.out.println("1. text full: " + _strText);
        
        String strText = new String(_strText);
        
        strText = strText.replace("[", "");
        strText = strText.replace("<", "");
        strText = strText.replace(">", "");
        strText = strText.replace("]", "");
        strText = strText.replace("::", "");
        strText = strText.substring(1);
        
        // 2015-07-25 22:27:59.204    344  INFO    AHA_BINARY-BPService  BTLINK Before Read buff.length = 2, offset = 0, size = 2
        System.out.println("2. text full: " + strText);
        
        // check date
        if (LogCatParser.isStringDouble(strText.substring(0, 2)) == false) {
            logInfo.m_strMessage = "" + strText;
            return logInfo;
        }
        
        StringTokenizer stk = new StringTokenizer(strText, TOKEN_SPACE, false);
        
     // 2015-07-25 22:27:59.204    344  INFO    AHA_BINARY-BPService  BTLINK Before Read buff.length = 2, offset = 0, size = 2
        if(stk.hasMoreElements()) {
            
            String s = stk.nextToken();
            
            if (LogCatParser.isStringDateYYYYMMdd(s))
                logInfo.m_strDate = s;
            else {
                logInfo.m_strMessage = "" + strText;
                return logInfo;
            }
        }
        
        System.out.println("date: " + logInfo.m_strDate);
        
     // 2015-07-25 22:27:59.204    344  INFO    AHA_BINARY-BPService  BTLINK Before Read buff.length = 2, offset = 0, size = 2
        if(stk.hasMoreElements()) {
            String s = stk.nextToken();
            
            if (LogCatParser.isStringDateHHmmssSSS(s))
                logInfo.m_strTime = s;
            else {
                logInfo.m_strMessage = "" + strText;
                return logInfo;
            }
        }
        
        System.out.println("time: " + logInfo.m_strTime);
        
     // 2015-07-25 22:27:59.204    344  INFO    AHA_BINARY-BPService  BTLINK Before Read buff.length = 2, offset = 0, size = 2
//        if(stk.hasMoreElements())
//            logInfo.m_strPid = stk.nextToken().trim();
        
        if(stk.hasMoreElements())
            logInfo.m_strThread = stk.nextToken().trim();
        
        System.out.println("thread: " + logInfo.m_strThread);
        
        if(stk.hasMoreElements())
            logInfo.m_strLogLV = stk.nextToken().trim();
        
     // 2015-07-25 22:27:59.204    344  INFO    AHA_BINARY-BPService  BTLINK Before Read buff.length = 2, offset = 0, size = 2
        if(stk.hasMoreElements())
            logInfo.m_strTag = stk.nextToken();
        
        System.out.println("tag: " + logInfo.m_strTag);
        
        
        if(stk.hasMoreElements())
        {
            logInfo.m_strMessage = stk.nextToken(TOKEN_MESSAGE);
            while(stk.hasMoreElements())
            {
                logInfo.m_strMessage += stk.nextToken(TOKEN_MESSAGE);
            }
            logInfo.m_strMessage = logInfo.m_strMessage.replaceFirst("\\): ", "");
        }
        logInfo.m_TextColor = getColor(logInfo);
        return logInfo;
    }

    public LogInfo parseLog(String strText)
    {
        if(isNormal(strText))
            return getNormal(strText);
        else if(isThreadTime(strText))
            return getThreadTime(strText);
        // Sequence is important!
        else if (isAhaClientAndroid2(strText))
            return getAhaClientAndroid2(strText);
        else if (isAhaClientAndroid(strText))
            return getAhaClientAndroid(strText);
        else if(isKernel(strText))
            return getKernel(strText);
        else
        {
            LogInfo logInfo = new LogInfo();
            logInfo.m_strMessage = strText;
            return logInfo;
        }
    }
}
