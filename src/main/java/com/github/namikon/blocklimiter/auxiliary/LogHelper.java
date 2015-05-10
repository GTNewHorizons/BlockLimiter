package com.github.namikon.blocklimiter.auxiliary;


import java.util.ArrayList;
import org.apache.logging.log4j.Logger;
import cpw.mods.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

/**
 * Generic LogHelper to print stuff to the console
 * @author Namikon
 */
public class LogHelper {
	private static ArrayList<String> _mReportedCategories = new ArrayList<String>();
	private static boolean doDebugLogs = false;
	
	/**
	 * Enable/Disable debug logs
	 * @param pEnabled
	 */
	public static void setDebugOutput(boolean pEnabled)
	{
		doDebugLogs = pEnabled;
	}
	
	public static void ResetCategories()
	{
		_mReportedCategories = new ArrayList<String>();
	}
	

	/**
	 * Print a log just one time per run. The category will not be displayed, it is only used to track all messages
	 * Use FullQualified names to differ them, like classname.function.error_no_2
	 * @param pCategory String-value of the category
	 * @param pLogLevel The loglevel
	 * @param pObject The message
	 */
	public static void log(String pCategory, Level pLogLevel, Object pObject)
	{
		if (pLogLevel == Level.DEBUG && !doDebugLogs)
			return;
		
		if (_mReportedCategories.contains(pCategory))
			return;
		else
		{
			_mReportedCategories.add(pCategory);
			FMLLog.log(Reference.MODID.toUpperCase() + " OTM", pLogLevel, String.valueOf(pObject));
		}	
	}	
	
	/**
	 * Just do a regular log to the console
	 * @param pLogLevel The loglevel
	 * @param pObject The message
	 */
	public static void log(Level pLogLevel, Object pObject)
	{
		if (pLogLevel == Level.DEBUG && !doDebugLogs)
			return;
		
		FMLLog.log(Reference.MODID.toUpperCase(), pLogLevel, String.valueOf(pObject));
	}

	public static void all(String pCategory,Object object)
	{
	    log(pCategory, Level.ALL, object);
	}

	public static void debug(String pCategory,Object object)
	{
		log(pCategory, Level.DEBUG, object);
	}

	public static void error(String pCategory,Object object)
	{
		log(pCategory, Level.ERROR, object);
	}

	public static void fatal(String pCategory,Object object)
	{
		log(pCategory, Level.FATAL, object);
	}

	public static void info(String pCategory,Object object)
	{
		log(pCategory, Level.INFO, object);
	}

	public static void off(String pCategory,Object object)
	{
		log(pCategory, Level.OFF, object);
	}

	public static void trace(String pCategory,Object object)
	{
		log(pCategory, Level.TRACE, object);
	}

	public static void warn(String pCategory,Object object)
	{
		log(pCategory, Level.WARN, object);
	}
	
	public static void all(Object object)
	{
	    log(Level.ALL, object);
	}

	public static void debug(Object object)
	{
	    log(Level.DEBUG, object);
	}

	public static void error(Object object)
	{
	    log(Level.ERROR, object);
	}

	public static void fatal(Object object)
	{
	    log(Level.FATAL, object);
	}

	public static void info(Object object)
	{
	    log(Level.INFO, object);
	}

	public static void off(Object object)
	{
	    log(Level.OFF, object);
	}

	public static void trace(Object object)
	{
	    log(Level.TRACE, object);
	}

	public static void warn(Object object)
	{
	    log(Level.WARN, object);
	}
	    
	
	/**
	 * Print stacktrace of 'e'
	 * @param e
	 */
	public static void DumpStack(Exception e)
	{
		FMLLog.severe(e.getMessage(), new Object[0]);
	}
	
	
	/**
	 * Print a stacktrace of 'e'. But do it only once for given function identifier 'pCategory'
	 * @param pCategory
	 * @param e
	 */
	public static void DumpStack(String pCategory, Exception e)
	{
		if (_mReportedCategories.contains(pCategory))
			return;
		else
		{
			_mReportedCategories.add(pCategory);
			DumpStack(e);
		}	
	}
}
