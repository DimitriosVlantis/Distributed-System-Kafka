import java.util.ArrayList;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ClientService extends Thread{
    ObjectInputStream in;
	ObjectOutputStream out;


    public ClientService(Socket connection) {
		try {
			// Initialization of stream.
            out = new ObjectOutputStream(connection.getOutputStream());
			in = new ObjectInputStream(connection.getInputStream());
            
		} 
        catch (IOException e) {
			e.printStackTrace();
		}
	}

    public void run(){
		try {
			String topicName = (String)in.readObject();
			String profileName = (String)in.readObject();
			int numberOfFragments = (int)in.readObject();
			String fileName = (String)in.readObject();
			String cleanFileName = fileName.substring(0, fileName.lastIndexOf("."));
			File folder = new File("Clients\\Client" + Client.serverStubPort + "\\Fragments\\" + cleanFileName);
			if(!folder.exists()){
				folder.mkdirs();
			}
			int i = 1;
			String fragmentName = null;
			String textLine = null;
			while(i <= numberOfFragments){
				fragmentName = (String)in.readObject();
				FileWriter writer = new FileWriter(new File("Clients\\Client" + Client.serverStubPort + "\\"+ "Fragments" + "\\" + cleanFileName + "\\" + fragmentName));
				// Read and write line by line the file until the message _Completed_ is sent
				textLine = (String)in.readObject();
				while(!textLine.equals("_Fragment_Completed_")){
					writer.write(textLine);
					textLine = (String)in.readObject();
				}
				i++;
				writer.close();
			}
			MultimediaFile message = null;
			File completeFile = new File("Clients\\Client" + Client.serverStubPort + "\\" + fileName);
			if(!completeFile.exists()){
			completeFile.createNewFile();
			}
			if(fileName.contains(".txt")){
				message = new MyText(completeFile, profileName);
			}
			else if(fileName.contains(".png") || fileName.contains(".jpeg")){
				message = new MyImage(completeFile, profileName);
			}
			else if(fileName.contains(".mp4") || fileName.contains(".wmv")){
				message = new MyVideo(completeFile, profileName);
			}
			Client.messagesPerTopic.get(topicName).add(message);
			message.assemble("Clients\\Client" + Client.serverStubPort);

		} catch (Exception e) {
			System.err.println(e);
			//TODO: handle exception
		}
    }
}
