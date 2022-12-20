import java.util.ArrayList;
import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public abstract class MultimediaFile{
	
	protected String multimediaFileName;
	protected String profileName;
	protected String dateCreated;
	protected String[] multimediaFileChunk;
	
    
	MultimediaFile(File multimediaFile, String profileName){
		this.multimediaFileName = multimediaFile.getName();
		this.profileName = profileName;
		try{
			Path path = Paths.get(multimediaFile.getName());
			BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
			this.dateCreated = attr.creationTime().toString();
		}
		catch(IOException e){
		}
		
	}

	//Getters
	public String getMultimediaFileName(){
		return multimediaFileName;
	}
	
	public String getProfileName(){
		return profileName;
	}
	
	public String getDateCreated(){
		return dateCreated;
	}
	
	public String[] getMultimediaFileChunk(){
		return multimediaFileChunk;
	}
	
	//Setters
	public void setMultmediaFileName(String multimediaFileName){
		this.multimediaFileName = multimediaFileName ; 
	}
	
	public void setProfileName(String profileName){
		this.profileName = profileName; 
	}
	
	public void setDateCreated(String dateCreated){
		this.dateCreated = dateCreated; 
	}
	
	public void setMultimediaFileChunk(String[] multimediaFileChunk){
		this.multimediaFileChunk = multimediaFileChunk; 
	}
	
	public synchronized void partition(String path) {
		try {
            File file = new File(path +  "\\" + multimediaFileName);
            if (file.exists()) {
                    String cleanFileName = file.getName().substring(0, file.getName().lastIndexOf(".")); // Name of the fragment without extension
                    File splitFile = new File(path +  "\\Fragments\\" + cleanFileName);//Destination folder to save.
                if (!splitFile.exists()) {
                    splitFile.mkdirs();
                    System.out.println("Directory Created -> "+ splitFile.getAbsolutePath());
                }

                int i = 01;// Files count starts from 1
                InputStream inputStream = new FileInputStream(file);
                String fragment = splitFile.getAbsolutePath() +"\\"+ String.format("%02d", i) +"_"+ file.getName();// Location to save the files which are Split from the original file.
                OutputStream outputStream = new FileOutputStream(fragment);
                System.out.println("File Created Location: "+ fragment);
                int totalPartsToSplit = 10;// Total files to split.
                int splitSize = inputStream.available() / totalPartsToSplit;
                int streamSize = 0;
                int read = 0;
                while ((read = inputStream.read()) != -1) {

                    if (splitSize == streamSize) {
                        if (i != totalPartsToSplit) {
                            i++;
                            String fileCount = String.format("%02d", i); // output will be 1 is 01, 2 is 02
                            fragment = splitFile.getAbsolutePath() +"\\"+ fileCount +"_"+ file.getName();
                            outputStream = new FileOutputStream(fragment);
                            System.out.println("File Created Location: "+ fragment);
                            streamSize = 0;
                        }
                    }
                    outputStream.write(read);
                    streamSize++;
                }

                inputStream.close();
                outputStream.close();
                System.out.println("Total files Split ->"+ totalPartsToSplit);
            } 
            else {
                System.err.println(file.getAbsolutePath() +" File Not Found.");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }	
    }

    public void assemble(String path) {
        try {
            String cleanFileName = multimediaFileName.substring(0, multimediaFileName.lastIndexOf("."));// video fileName without extension
            File splitFiles = new File(path + "\\Fragments\\" + cleanFileName + "\\");// get all files which are to be join
            if (splitFiles.exists()) {
                File[] files = splitFiles.getAbsoluteFile().listFiles();
                if (files.length != 0) {
                    System.out.println("Total files to be join: "+ files.length);

                    File fileJoinPath = new File(path + "\\");// merge video files saved in this location

                    if (!fileJoinPath.exists()) {
                        fileJoinPath.mkdirs();
                        System.out.println("Created Directory -> "+ fileJoinPath.getAbsolutePath());
                    }

                    OutputStream outputStream = new FileOutputStream(fileJoinPath.getAbsolutePath() + "\\" + multimediaFileName);

                    for (File file : files) {
                        System.out.println("Reading the file -> "+ file.getName());
                        InputStream inputStream = new FileInputStream(file);

                        int readByte = 0;
                        while((readByte = inputStream.read()) != -1) {
                            outputStream.write(readByte);
                        }
                        inputStream.close();
                    }

                    System.out.println("Join file saved at -> "+ fileJoinPath.getAbsolutePath());
                    outputStream.close();
                } 
                else {
                    System.err.println("No Files exist in path -> "+ splitFiles.getAbsolutePath());
                }
            } 
            else {
                System.err.println("This path doesn't exist -> "+ splitFiles.getAbsolutePath());
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}