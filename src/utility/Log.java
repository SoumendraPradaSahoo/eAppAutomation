package utility;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log {
	static{
		init();
	}
	private static Logger log = Logger.getLogger(Log.class.getName());

	public static void info(String message) {

		log.info(message);

	}
	
	public static void info(String[] message) {
		String temp="";
		for (int i=0;i<message.length;i++)
			temp = temp + "  " + message[i];
		log.info(temp);

	}

	public static void warn(String message) {

		log.warn(message);

	}

	public static void error(String message) {

		log.error(message);

	}

	public static void fatal(String message) {

		log.fatal(message);

	}

	public static void debug(String message) {
		log.debug(message);
	}
	
	 public static void error(Exception e) {
		    String sb = "";
		    for (StackTraceElement element : e.getStackTrace()) {
		        sb.concat(element.toString());
		        sb.concat("\n");
		    }
		    error(sb);
		}

	private static void init() {
		PropertyConfigurator.configure("C:/Users/ssahoo43/D_Drive/eAppAutomation/Object/log4j.properties");
	}
}
