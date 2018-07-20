/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Shashkov Andrey
 */
public class ConverterInCsv {
    //private static String fileExtension;
    private static String inputFileName,outputFileName;
    private static String readlineFromFile = "";
    private static long count = 0;
    private static long worktime = 0;
    private static Properties property;
    private static String[] inputFieldsArray;
    private static String inputFileFieldsDelimiter;
    private static String[] outputFieldsIndexArray;
    private static boolean  insertCustoms = false;
    private static int numFieldsInConfigFile;
    private static SimpleDateFormat dateTimeFormat;
    
    private static ArrayList<Integer> contactIdList = new ArrayList<Integer>();
    private static ArrayList<String> timeStampList = new ArrayList<String>();
    
    private static HashSet<Integer> duplicatedIdHashSet = new HashSet<Integer>();
    private static HashMap<Integer,String> map = new HashMap<Integer,String>();
    
    private static final String TEMP_UNIQUE_ID_FILENAME = "tempUniqOut.csv";
    private static final String TEMP_NON_UNIQUE_ID_FILENAME = "tempDuplicateOut.csv";
    
    public static void main(String[] args) throws IOException {

        File configFile;
        System.out.println("Start script ConverterCsv...");
        worktime = System.currentTimeMillis();      
        
        
        
        if (args.length >= 1) {
        	configFile = new File(args[0]);
        	if (configFile.exists())
        		loadProperty(args[0]);    	
        	else {
        		System.out.println("Configuration file [" + args[0] + "] not found. Exit program.");
        		System.exit(0);
        	}
        } else {
        	System.out.println("Missing mandatory program attributes: <config_file_name>. Exit program.");
        	System.out.println("Note: Program attributes is <config_file_name> <input_file_name> <output_file_name>");
        	System.exit(0);
        }
        
        
        if (args.length >= 2)
        	inputFileName = args[1];
        else {
        	System.out.println("Missing optional program attributes: <input_file_name>.");
        	System.out.println("This value will be used from the configuration file.");
        	
        	if (!getProperty("inputfile.name").isEmpty()){
        		inputFileName = getProperty("inputfile.name");
        		System.out.println("Input file name:["+ inputFileName +"]");
        	} else {
         		System.out.println("Property [inputfile.name] has NOT value in configuration file. Exit program.");
         		System.exit(0);
        	}
        }
        
        if (args.length >= 3)
        	outputFileName = args[2];
        else {
        	System.out.println("Missing optional program attributes: <output_file_name>.");
        	System.out.println("This value will be used from the configuration file.");
        	if (!getProperty("outputfile.name").isEmpty()){
        		outputFileName = getProperty("outputfile.name");
        		System.out.println("Output file name:["+ outputFileName +"]");
        	} else {
        		System.out.println("Property [outputfile.name] has NOT value in configuration file. Exit program.");
         		System.exit(0);
        	}		
        }
        	                   
        File inputFile = new File(inputFileName);
        if (!inputFile.exists()) {
             System.out.println("Input file [" + inputFileName + "]  not found. Exit programm.");
             System.exit(0);
        }
        
        File outputFile = new File(outputFileName);
        if (!outputFile.exists())
            createNewFile(outputFile);
        else {
            System.out.print("File [" +outputFileName+ "] is alredy exist in current directory. ");
            if (!getProperty("outputfile.save.old").isEmpty() && getProperty("outputfile.save.old").equals("true")) 
            	renameOldConvertedFileAndCreateNewFileOne(outputFile);
            else {
            	System.out.println("It was recreated.");
            	outputFile.delete();
            	createNewFile(outputFile);
            }
        }
        
        if (!getProperty("outputfile.fields.dateformat").isEmpty())  	
        	dateTimeFormat = new SimpleDateFormat(getProperty("outputfile.fields.dateformat")); 
    	 else 
    		 dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); 
        
        if (!getProperty("outputfile.remove.duplicate").isEmpty())  	
        	if (getProperty("outputfile.remove.duplicate").equals("true"))
        		createOutputFileWithRemoveDuplicate();
        	else
        		createOutputFile(inputFileName, outputFileName);	
    	else {
    		System.out.println("Property [outputfile.remove.duplicate] has NOT value in configuration file.");
    		System.out.println("This value will be used as 'false'.");
    		createOutputFile(inputFileName, outputFileName);
    	}
        //createOutputFile(inputFileName, outputFileName);
        //System.out.println("Processed " + count + " lines at " + (System.currentTimeMillis() - worktime) + " ms.");
        //createExportFile();
    }
    
    private static void loadProperty(String filename) {
    	property = new Properties();
    	InputStream input = null;

    	try {
    		input = new FileInputStream(filename);
    		property.load(input);
    		System.out.println("Properties file [" + filename +"] loaded succefully...");

    	} catch (IOException except) {
    		System.out.println(except.getMessage());
    	} finally {
    		if (input != null) {
    			try {
    				input.close();
    			} catch (IOException e) {
    				System.out.println(e.getMessage());
    			}
    		}
    	}	
	}

	private static void renameOldConvertedFileAndCreateNewFileOne(File mFile) {
        
		String fileExtension = inputFileName.substring(inputFileName.lastIndexOf('.'));
		
		SimpleDateFormat fileAppenderdateFormat = new SimpleDateFormat("dd_MM_yyyy-HH_mm");

        String oldName = mFile.getName().replaceAll(fileExtension, "");
        String newName = oldName + "-" + fileAppenderdateFormat.format(new Date());

        System.out.println("It was renamed to [" + newName + fileExtension + "] and created new one.");

        File newCdrFile = new File(newName + fileExtension);
        mFile.renameTo(newCdrFile);

        File oldCdrFile = new File(oldName + fileExtension);
        createNewFile(oldCdrFile);
    }
    
    private static void createNewFile(File file) {
    	try {
    		file.createNewFile();
    	} catch (IOException except) {
    		System.out.println(except.getMessage());
    	}
    }

    private static void createOutputFile(String inputFileName, String convertFileName) {
        
        String idString = "";
        String ouputString = "";
        boolean insertId = false;
        boolean deleteFirstLine = false;
        
        if (getProperty("outputfile.insertid").equals("true"))
        	insertId = true;
        
        if (getProperty("outputfile.headerstring.delete").equals("true"))
        	deleteFirstLine = true;
        
        if (getProperty("outputfile.fields.insert.customs").equals("true"))
        	insertCustoms = true;
        	
        String oFFD = getProperty("outputfile.fieldsdelimiter");
        outputFieldsIndexArray = getProperty("outputfile.fields.sequence").split(",");
        numFieldsInConfigFile = outputFieldsIndexArray.length;
              
        inputFileFieldsDelimiter = getProperty("inputfile.fieldsdelimiter");
        String headerString = getProperty("outputfile.headerstring.value");
        
        try (BufferedReader buffer = new BufferedReader(new FileReader(inputFileName));
               BufferedWriter bufferOut = new BufferedWriter(new FileWriter(convertFileName)); ) {
        	
        	if (getProperty("outputfile.headerstring.insert").equals("true"))
        		bufferOut.write(headerString + System.getProperty("line.separator"));
        	
        	while ((readlineFromFile = buffer.readLine()) != null) {
            	if (!readlineFromFile.isEmpty()) {        
            		inputFieldsArray = readlineFromFile.split(inputFileFieldsDelimiter);
            		
                    count++;
                    if (insertId) 
                    	idString = String.valueOf(count) + oFFD;
            
                    ouputString = idString;
                    
                    for (int i = 0; i < numFieldsInConfigFile - 1; i++)
                    	ouputString+= checkFieldIsExistAndCustom(Integer.valueOf(outputFieldsIndexArray[i]))+ oFFD;
                    
                    ouputString+= checkFieldIsExistAndCustom(Integer.valueOf(outputFieldsIndexArray[numFieldsInConfigFile-1])) + 
                    		System.getProperty("line.separator");
                    
                    if (!(deleteFirstLine && count == 1))
                        bufferOut.write(ouputString);
            	}
        	}
        } catch (FileNotFoundException except) {
            System.out.println(except.getMessage());
        } catch (IOException except) {
            System.out.println(except.getMessage());
        }        
        	
        }
	private static String checkFieldIsExistAndCustom(int index){
		if (inputFieldsArray.length <= index)
			if (insertCustoms)
				if (getProperty("outputfile.field.insert." + index).isEmpty())
					return "";
				else 
					return getProperty("outputfile.field.insert." + index);
			else 
				return "";
		else if (insertCustoms)
				if (getProperty("outputfile.field.insert." + index).isEmpty())
					return inputFieldsArray[index];
				else 
					return getProperty("outputfile.field.insert." + index);
			else 	
				return inputFieldsArray[index];	
		
	}
	
	private static void createOutputFileWithRemoveDuplicate(){
		
		getHashSetOfDuplicatedContactId();
		splitIncomingFile();
		getMapForPairDuplicatedIDLastAttemptTimeStamp();     
		mergeTempFilesToOneWithNoDuplicates();
		createOutputFile(TEMP_UNIQUE_ID_FILENAME, outputFileName);
	}


	private static void getHashSetOfDuplicatedContactId () {
		int ContactID;	
		int count = 0;
		HashSet<Integer> tempUniqueIdSet = new HashSet<Integer>();

		inputFileFieldsDelimiter = getProperty("inputfile.fieldsdelimiter");
		
		try (BufferedReader buffer = new BufferedReader(new FileReader(inputFileName));) {
        		        	
        	while ((readlineFromFile = buffer.readLine()) != null) {
            	if (!readlineFromFile.isEmpty()) {        
            		ContactID = 0;
            		inputFieldsArray = readlineFromFile.split(inputFileFieldsDelimiter);
            		            		
            		if (isIdFieldContainsOnlyDigits(inputFieldsArray[0]))
            			ContactID = Integer.valueOf(inputFieldsArray[0]);
            			
            		if (ContactID!=0 && !tempUniqueIdSet.contains(ContactID)) {
            			tempUniqueIdSet.add(ContactID);
            			//System.out.println(" Add unique Id of Contact to HashSet : " + ContactID);
            		} else if (ContactID!=0 && !duplicatedIdHashSet.contains(ContactID)) {
            			duplicatedIdHashSet.add(ContactID);
            			count++;
            			//System.out.println("Duplicated ID :" + ContactID);
            		}	
            	}
        	}
        	System.out.println("Found "+ count + " contacts with multiple call attempt." );
        } catch (FileNotFoundException except) {
            System.out.println(except.getMessage());
        } catch (IOException except) {
            System.out.println(except.getMessage());
        }	
	}
	
	private static void splitIncomingFile() {
		
		int ContactID;
				
		try (BufferedReader buffer = new BufferedReader(new FileReader(inputFileName));
	               BufferedWriter bufferOut = new BufferedWriter(new FileWriter(TEMP_UNIQUE_ID_FILENAME));
						BufferedWriter bufferOut2 = new BufferedWriter(new FileWriter(TEMP_NON_UNIQUE_ID_FILENAME));) {        	
	        		        	
	        	while ((readlineFromFile = buffer.readLine()) != null) {
	            	if (!readlineFromFile.isEmpty()) {        
	            		inputFieldsArray = readlineFromFile.split(inputFileFieldsDelimiter);
	            		ContactID = 0;
	            		
	            		if (isIdFieldContainsOnlyDigits(inputFieldsArray[0]))
	            			ContactID = Integer.valueOf(inputFieldsArray[0]);
	                    
	            		if (ContactID == 0) 
	            			bufferOut.write(readlineFromFile + System.getProperty("line.separator"));
	            		else if (duplicatedIdHashSet.contains(ContactID)) {
	            			contactIdList.add(ContactID);
	            			timeStampList.add(inputFieldsArray[15]);            			
	            			bufferOut2.write(readlineFromFile + System.getProperty("line.separator"));
	            		} else 
	            			bufferOut.write(readlineFromFile + System.getProperty("line.separator"));
	            	}
	        	}
		} catch (FileNotFoundException except) {
			System.out.println(except.getMessage());
	    } catch (IOException except) {
	        System.out.println(except.getMessage());
	    } 
	
	}

	private static void getMapForPairDuplicatedIDLastAttemptTimeStamp() {
		Date contactAttemptTimeStamp = null; 
		int i;
		
		for(int duplicatedId : duplicatedIdHashSet){
			contactAttemptTimeStamp = getDateFromString("01.01.1970 00:00:00");
			
			for (i = 0; i<contactIdList.size(); i++)
            if (duplicatedId == contactIdList.get(i))
            	if ((contactAttemptTimeStamp.compareTo(getDateFromString(timeStampList.get(i))) <= 0)) {        			     		
            		contactAttemptTimeStamp = getDateFromString(timeStampList.get(i));
            		//System.out.println(contactAttemptTimeStamp.toString());
            	}
			map.put(duplicatedId, dateTimeFormat.format(contactAttemptTimeStamp));
		}
	}
	
	private static void mergeTempFilesToOneWithNoDuplicates() {
		int ContactID;
		File outfile = new File(TEMP_UNIQUE_ID_FILENAME);
		
		try (BufferedReader buffer = new BufferedReader(new FileReader(TEMP_NON_UNIQUE_ID_FILENAME));
	               BufferedWriter bufferOut = new BufferedWriter(new FileWriter(outfile,true)); ) {
	        		        	
	        	while ((readlineFromFile = buffer.readLine()) != null) {
	            	if (!readlineFromFile.isEmpty()) {        
	            		inputFieldsArray = readlineFromFile.split(inputFileFieldsDelimiter);
	            		ContactID = 0;
	            		
	            		if (isIdFieldContainsOnlyDigits(inputFieldsArray[0]))
	            			ContactID = Integer.valueOf(inputFieldsArray[0]);
	            		
	            		for (Map.Entry<Integer, String> entry : map.entrySet()) {
	            		    
	            		    if (ContactID == entry.getKey() && (inputFieldsArray[15]).equals(entry.getValue())) {
	            		    	bufferOut.append(readlineFromFile + System.getProperty("line.separator"));
	            		    	//System.out.println("Append row:" + readlineFromFile);
	            		    }
	            		}
	            	}
	        	}
	        } catch (FileNotFoundException except) {
	            System.out.println(except.getMessage());
	        } catch (IOException except) {
	            System.out.println(except.getMessage());
	        }        
	}
	
	private static boolean isIdFieldContainsOnlyDigits (String id) {
		String regex = "\\d+";
		if (id.matches(regex))
			return true;
		else 
			return false;
	}
	
	private static Date getDateFromString (String dateTimeString) {
			
		Date dateTime = null;
		try {
		    dateTime = dateTimeFormat.parse(dateTimeString);
		} catch (ParseException except) {
			System.out.println(except.getMessage());
		}		
		return dateTime;
	}
	
	private static String getProperty(String propName) {
		String prop = null;
		prop = property.getProperty(propName);
		if (prop == null) {
			System.out.println("Property name [" + propName + "] is invalid or missing in configuration file. Check this and try again ..." );
			System.exit(0);
		}
		return prop;	
		
	}
	
   
    
}
