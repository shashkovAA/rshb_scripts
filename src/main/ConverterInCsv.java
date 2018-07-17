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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;


/**
 *
 * @author Shashkov Andrey
 */
public class ConverterInCsv {
    //private static String fileExtension;
    private static String inputFileName,convertedFileName;
    private static long count = 0;
    private static Properties property;
    private static String[] inputFieldsArray;
    private static String[] outputFieldsIndexArray;
    private static boolean  insertCustoms = false;
    public static void main(String[] args) throws IOException {
        
        System.out.println("Start script ConverterCsv...");
              
        if (args.length >= 1)
        	loadProperty(args[0]);
        	
        else 
        	loadProperty("config.properties");
        
        if (args.length >= 2)
        	inputFileName = args[1];
        else if (!property.getProperty("inputfile.name").isEmpty())
        		inputFileName = property.getProperty("inputfile.name");   
         	 else 
         		inputFileName = "input.csv";  
        System.out.println("Input file name:["+ inputFileName +"]");
        
        if (args.length >= 3)
        	convertedFileName = args[2];
        else if (!property.getProperty("outputfile.name").isEmpty())
            	convertedFileName = property.getProperty("outputfile.name");
        	 else 
        		convertedFileName = "output.csv";   
        System.out.println("Output file name:["+ convertedFileName +"]");
                      
        File inputFile = new File(inputFileName);
        if (!inputFile.exists()) {
             System.out.println("Input file [" + inputFileName + "] is not exist. Exit programm.");
             System.exit(0);
        }
        
        File convFile = new File(convertedFileName);
        if (!convFile.exists())
            createNewFile(convFile);
        else {
            System.out.print("File [" +convertedFileName+ "] is alredy exist. ");
            if (!property.getProperty("keep.old.outputfile").isEmpty() && property.getProperty("keep.old.outputfile").equals("true")) 
            	renameOldConvertedFileAndCreateNewFileOne(convFile);
            else {
            	System.out.println("It was recreated.");
            	convFile.delete();
            	createNewFile(convFile);
            }
        } 

        copytext(inputFileName, convertedFileName);
        System.out.println("Processed " + count + " lines");
    }
    
    private static void loadProperty(String filename) {
    	property = new Properties();
    	InputStream input = null;

    	try {

    		input = new FileInputStream(filename);

    		// load a properties file
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
        
		String fileExtension = inputFileName.substring(inputFileName.indexOf('.'));
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy-HH_mm");

        String oldName = mFile.getName().replaceAll(fileExtension, "");
        String newName = oldName + "-" + dateFormat.format(new Date());

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

    private static void copytext(String inputFileName, String convertFileName) {
        String readline;
        String idString = "";
        boolean insertId = false;
        boolean deleteFirstLine = false;
        
        if (property.getProperty("outputfile.insertid").equals("true"))
        	insertId = true;
        
        if (property.getProperty("outputfile.headerstring.delete").equals("true"))
        	deleteFirstLine = true;
        
        if (property.getProperty("outputfile.fields.insert.customs").equals("true"))
        	insertCustoms = true;
        	
        String oFFD = property.getProperty("outputfile.fieldsdelimiter");
        outputFieldsIndexArray = property.getProperty("outputfile.fields.sequence").split(",");
               
        String inputFileFieldsDelimiter = property.getProperty("inputfile.fieldsdelimiter");
        String headerString = property.getProperty("outputfile.headerstring.value");
        
        try (BufferedReader buffer = new BufferedReader(new FileReader(inputFileName));
               BufferedWriter bufferOut = new BufferedWriter(new FileWriter(convertFileName)); ) {
        	
        	if (property.getProperty("outputfile.headerstring.insert").equals("true"))
        		bufferOut.write(headerString + System.getProperty("line.separator"));
        	
        	while ((readline = buffer.readLine()) != null) {
            	if (!readline.isEmpty()) {        
            		inputFieldsArray = readline.split(inputFileFieldsDelimiter);
            		
                    count++;
                    if (insertId) 
                    	idString = String.valueOf(count) + oFFD;
                    	
                    if (!(deleteFirstLine && count == 1))
                    bufferOut.write(idString + checkFieldIsExistAndCustom(Integer.valueOf(outputFieldsIndexArray[0]))+ oFFD +
                    		checkFieldIsExistAndCustom(Integer.valueOf(outputFieldsIndexArray[1])) + oFFD + 
                    		checkFieldIsExistAndCustom(Integer.valueOf(outputFieldsIndexArray[2])) + oFFD +
                    		checkFieldIsExistAndCustom(Integer.valueOf(outputFieldsIndexArray[3])) + oFFD +
                    		checkFieldIsExistAndCustom(Integer.valueOf(outputFieldsIndexArray[4])) + oFFD +
                    		checkFieldIsExistAndCustom(Integer.valueOf(outputFieldsIndexArray[5])) + oFFD +
                    		checkFieldIsExistAndCustom(Integer.valueOf(outputFieldsIndexArray[6])) + oFFD +
                    		checkFieldIsExistAndCustom(Integer.valueOf(outputFieldsIndexArray[7])) + oFFD +
                    		checkFieldIsExistAndCustom(Integer.valueOf(outputFieldsIndexArray[8])) + oFFD +
                    		checkFieldIsExistAndCustom(Integer.valueOf(outputFieldsIndexArray[9])) + oFFD +
                    		checkFieldIsExistAndCustom(Integer.valueOf(outputFieldsIndexArray[10])) + oFFD +
                    		checkFieldIsExistAndCustom(Integer.valueOf(outputFieldsIndexArray[11])) + oFFD +
                    		checkFieldIsExistAndCustom(Integer.valueOf(outputFieldsIndexArray[12])) + oFFD +
                    		checkFieldIsExistAndCustom(Integer.valueOf(outputFieldsIndexArray[13])) + oFFD +
                    		checkFieldIsExistAndCustom(Integer.valueOf(outputFieldsIndexArray[14])) + 
                    		System.getProperty("line.separator"));
                    
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
				if (property.getProperty("outputfile.field.insert." + index).isEmpty())
					return "";
				else 
					return property.getProperty("outputfile.field.insert." + index);
			else 
				return "";
		else if (insertCustoms)
				if (property.getProperty("outputfile.field.insert." + index).isEmpty())
					return inputFieldsArray[index];
				else 
					return property.getProperty("outputfile.field.insert." + index);
			else 	
				return inputFieldsArray[index];
			
		
	}
   
    
}
