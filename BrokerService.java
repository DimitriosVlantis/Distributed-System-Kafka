import java.util.ArrayList;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class BrokerService extends Thread{
    ObjectInputStream in;
	ObjectOutputStream out;
	ObjectInputStream nin;
	ObjectOutputStream nout;
	Socket nconnection;

    public BrokerService(Socket connection) {
		try {
			// Initialization of stream.
            out = new ObjectOutputStream(connection.getOutputStream());
			in = new ObjectInputStream(connection.getInputStream());
            // Initialization of other needed variables.

		} 
        catch (IOException e) {
			e.printStackTrace();
		}
	}

    public synchronized void run(){
		try {
			String line = (String)in.readObject();
			if(line.equals("1")){
				String topicName = (String)in.readObject();
				String clientIp = (String)in.readObject();
				String clientPort = (String)in.readObject();
				System.out.println(clientIp+", "+clientPort);
				Broker.usersPerTopic.put(topicName, new ArrayList<ArrayList<String>>());
				ArrayList<String> tempList = new ArrayList<>();
				tempList.add(clientIp);
				tempList.add(clientPort);
				Broker.usersPerTopic.get(topicName).add(tempList);
				Broker.messagesPerTopic.put(topicName, new ArrayList<MultimediaFile>());
			}
			else if(line.equals("2")){
				String topicName = (String)in.readObject();
				String clientIp = (String)in.readObject();
				String clientPort = (String)in.readObject();
				ArrayList<String> tempList = new ArrayList<>();
				tempList.add(clientIp);
				tempList.add(clientPort);
				Broker.usersPerTopic.get(topicName).add(tempList);
				ArrayList<MultimediaFile> topicMessages = Broker.messagesPerTopic.get(topicName);
				out.writeObject(String.valueOf(topicMessages.size()));
				out.flush();
				for(MultimediaFile message:topicMessages){
					String cleanFileName = message.getMultimediaFileName().substring(0, message.getMultimediaFileName().lastIndexOf("."));
					out.writeObject(message.getMultimediaFileName());
					out.flush();
					out.writeObject("10");
					out.flush();
					File[] files = new File("Server"  + "\\Fragments\\" + cleanFileName).listFiles();
					for(File fragment : files){
						out.writeObject(fragment.getName());
						BufferedReader reader = new BufferedReader(new FileReader(new File("Server"  + "\\Fragments\\" + cleanFileName + "\\" + fragment.getName())));
						String fileLine = reader.readLine();
						// Read and write line by line the file and at EOF send _Completed_
						while(fileLine != null){
							out.writeObject(fileLine);
							out.flush();
							fileLine = reader.readLine();
						}
						out.writeObject("_Fragment_Completed_");
						out.flush();
						reader.close();
					}
					System.out.println("Inform succeeded!");
				}
			}
			else if(line.equals("3")){
				String profileName = (String)in.readObject();
				String clientIp = (String)in.readObject();
				String clientPort = (String)in.readObject();
				String topicName = (String)in.readObject();
				String fileName = (String)in.readObject();
				int numberOfFragments = Integer.parseInt((String)in.readObject());
				String cleanFileName = fileName.substring(0, fileName.lastIndexOf("."));
				File folder = new File("Server" + "\\" + "Fragments" + "\\" + cleanFileName);
				if(!folder.exists()){
					folder.mkdirs();
				}
				int i = 1;
				String fragmentName = null;
				String textLine = null;
				while(i <= numberOfFragments){
					fragmentName = (String)in.readObject();
					FileWriter writer = new FileWriter(new File("Server" + "\\"+ "Fragments" + "\\" + cleanFileName + "\\" + fragmentName));
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
				File completeFile = new File("Server" + "\\" + fileName);
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
				Broker.messagesPerTopic.get(topicName).add(message);

				//pull function
				System.out.println("Automatic pull started!");
				ArrayList<ArrayList<String>> subscribers = Broker.usersPerTopic.get(topicName);

				for(ArrayList<String> sub:subscribers){
					String subIp = sub.get(0);
					String subPort = sub.get(1);
					if(!subPort.equals(clientPort) || !subIp.equals(clientIp)){
						System.out.println("Informing subscriber!");
						nconnection = new Socket(subIp, Integer.parseInt(subPort));
                    	nout = new ObjectOutputStream(nconnection.getOutputStream());
                    	nin = new ObjectInputStream(nconnection.getInputStream());
						nout.writeObject(topicName);
						nout.flush();
						nout.writeObject(profileName);
						nout.flush();
						nout.writeObject(numberOfFragments);
						nout.flush();
						nout.writeObject(fileName);
						nout.flush();
						System.out.println("Initialization succeeded!");
						File[] files = new File("Server"  + "\\Fragments\\" + cleanFileName).listFiles();
						for(File fragment : files){
							nout.writeObject(fragment.getName());
							BufferedReader reader = new BufferedReader(new FileReader(new File("Server"  + "\\Fragments\\" + cleanFileName + "\\" + fragment.getName())));
							String fileLine = reader.readLine();
							// Read and write line by line the file and at EOF send _Completed_
							while(fileLine != null){
								nout.writeObject(fileLine);
								nout.flush();
								fileLine = reader.readLine();
							}
							nout.writeObject("_Fragment_Completed_");
							nout.flush();
							reader.close();
						}
						System.out.println("Inform succeeded!");
						nconnection.close();
					}
				}
			}
		} catch (Exception e) {
			//TODO: handle exception
		}
    }
}
