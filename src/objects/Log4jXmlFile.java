package objects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


public class Log4jXmlFile
{
	private static final String XML_LOG4J2_FILE = "log4j2.xml";
	
	private static File log4jXmlFile = new File(XML_LOG4J2_FILE);
	
	private static  String LOGGER_NAME;
	
	public static void checkExistLog4jXmlFile(String loggerName) {			
				
		LOGGER_NAME = loggerName;
		if  (!log4jXmlFile.exists()) 
			createLog4jXmlFile();		
	}
	
	public static boolean createLog4jXmlFile() {
		try {
			log4jXmlFile.createNewFile();
			addContentToXmlFile();
			System.out.println("Creating log4j2.xml file is successfull.");
			return true;
		} catch (IOException e) {
			System.out.println("Creating log4j2.xml file is fail.");
			System.exit(0);
			return false;
		}	
	}
	
	public static void addContentToXmlFile () {
		String endString = System.getProperty("line.separator");
		try (BufferedWriter bufferOut = new BufferedWriter(new FileWriter(log4jXmlFile));) {
		   
			bufferOut.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + endString);
			bufferOut.write("<Configuration packages=\"main\">" + endString);
			bufferOut.write("<Appenders>" + endString);
			bufferOut.write("<RollingFile name=\"File\" append=\"true\" fileName=\"logs/ConverterCsvLog.txt\"  filePattern = \"ConverterCsvLog-%i.txt\" >" + endString);
			bufferOut.write("<PatternLayout pattern=\"%d{dd/MM/yy HH:mm:ss:SSS} %-5p [%t] %C{2}.%M (%F:%L) - %m%n\"/>" + endString);
			bufferOut.write("<Policies><SizeBasedTriggeringPolicy size=\"10MB\"/></Policies>" + endString);
			bufferOut.write("<DefaultRolloverStrategy max=\"5\"/>" + endString);
			bufferOut.write("</RollingFile>" + endString);
			bufferOut.write("<Console name=\"STDOUT\" target=\"SYSTEM_OUT\">" + endString);
			bufferOut.write("<PatternLayout pattern=\"%d{dd/MM/yy HH:mm:ss:SSS} %-5p [%t] %C{2}.%M (%F:%L) - %m%n\"/>" + endString);
			bufferOut.write("</Console>" + endString);
			bufferOut.write("</Appenders>" + endString);
			bufferOut.write("<Loggers>" + endString);
			bufferOut.write("<Logger name=\""+ LOGGER_NAME + "\" level=\"debug\" additivity=\"false\">" + endString);
			bufferOut.write("<AppenderRef ref=\"File\" level=\"info\"/>" + endString);
			bufferOut.write("<AppenderRef ref=\"STDOUT\" level=\"error\"/>" + endString);
			bufferOut.write("</Logger>" + endString);
			bufferOut.write("<Root  level=\"error\">" + endString);
			bufferOut.write("<AppenderRef ref=\"STDOUT\"/>" + endString);
			bufferOut.write("</Root>" + endString);
			bufferOut.write(" </Loggers>" + endString);
			bufferOut.write("</Configuration>");
						
		    
		} catch (FileNotFoundException fnfe) {
		    System.out.println(fnfe.getMessage());
		} catch (IOException ioe) {
		    System.out.println(ioe.getMessage());
		}
	    
		
	}
	
}
