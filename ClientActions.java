import java.util.ArrayList;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ClientActions extends Thread{
    ObjectInputStream in;
	ObjectOutputStream out;
    ObjectInputStream nin;
	ObjectOutputStream nout;
    String ipAddress;
    String serverStubPort;
    String brokerPort;
    Socket connection;
    Socket nconnection;
    File folder;
    Scanner scr;
    String profileName;

    public ClientActions(Socket connection, String ip, int port) {
		try {
			// Initialization of stream.
            this.connection = connection;
            scr = new Scanner(System.in);
            out = new ObjectOutputStream(connection.getOutputStream());
			in = new ObjectInputStream(connection.getInputStream());
            // Initialization of other needed variables.
            //ipAddress = connection.getInetAddress().toString();
            //clientStubPort = String.valueOf(connection.getPort());
            ipAddress = ip;
            serverStubPort = String.valueOf(port);
		} 
        catch (IOException e) {
			e.printStackTrace();
		}
	}

    public void run(){
        try {
            this.folder = new File("Clients\\" + "Client" + serverStubPort);
            if(!this.folder.exists()){
                this.folder.mkdirs();
            }
            System.out.println("Give your username: ");
            String line = scr.nextLine();
            profileName = line;
            Client.profileName = profileName;
            out.writeObject("Client");
            out.flush();
            while(true){
                System.out.println(
                    "**************** Menu ****************"+
                    "\n"+
                    "\n"+
                    "Choose one of the options below (type the number of the option):" +
                    "\n"+
                    "\n"+
                    "1. Create a new topic."+
                    "\n"+
                    "2. Become a member to an existing topic."+
                    "\n"+
                    "3. Post a message to an existing topic."+
                    "\n"+
                    "4. Read all messages from a topic you have subscribed to."+
                    "\n"+
                    "5. Exit."
                );
                line = scr.nextLine();
                out.writeObject(line);
                out.flush();
                if(line.equals("1")){
                    System.out.println();
                    System.out.print("Give the name of the topic:");
                    String topicName = scr.nextLine();
                    out.writeObject(topicName);
                    out.flush();
                    String brokerIp = (String)in.readObject();
                    String brokerPort = (String)in.readObject();
                    nconnection = new Socket(brokerIp, Integer.parseInt(brokerPort));
                    nout = new ObjectOutputStream(nconnection.getOutputStream());
                    nin = new ObjectInputStream(nconnection.getInputStream());
                    nout.writeObject(line);
                    nout.flush();
                    nout.writeObject(topicName);
                    nout.flush();
                    nout.writeObject(ipAddress);
                    nout.flush();
                    nout.writeObject(serverStubPort);
                    nout.flush();
                    Client.messagesPerTopic.put(topicName, new ArrayList<MultimediaFile>());
                    nconnection.close();
                }
                else if(line.equals("2")){
                    System.out.println();
                    System.out.print("Give the name of the topic:");
                    String topicName = scr.nextLine();
                    out.writeObject(topicName);
                    out.flush();
                    String brokerIp = (String)in.readObject();
                    String brokerPort = (String)in.readObject();
                    nconnection = new Socket(brokerIp, Integer.parseInt(brokerPort));
                    nout = new ObjectOutputStream(nconnection.getOutputStream());
                    nin = new ObjectInputStream(nconnection.getInputStream());
                    nout.writeObject(line);
                    nout.flush();
                    nout.writeObject(topicName);
                    nout.flush();
                    nout.writeObject(ipAddress);
                    nout.flush();
                    nout.writeObject(serverStubPort);
                    nout.flush();
                    Client.messagesPerTopic.put(topicName, new ArrayList<MultimediaFile>());
                    //TODO: Receive all messages!
                    int numberOfMessages = Integer.parseInt((String)nin.readObject());
                    
                    int j = 1;
                    while(j<=numberOfMessages){
                        String fileName = (String)nin.readObject();
                        int numberOfFragments = Integer.parseInt((String)nin.readObject());
                        String cleanFileName = fileName.substring(0, fileName.lastIndexOf("."));
                        File folder = new File("Clients\\Client" + Client.serverStubPort + "\\Fragments\\" + cleanFileName);
                        if(!folder.exists()){
                            folder.mkdirs();
                        }
                        int i = 1;
                        String fragmentName = null;
                        String textLine = null;
                        while(i <= numberOfFragments){
                            fragmentName = (String)nin.readObject();
                            FileWriter writer = new FileWriter(new File("Clients\\Client" + Client.serverStubPort + "\\"+ "Fragments" + "\\" + cleanFileName + "\\" + fragmentName));
                            // Read and write line by line the file until the message _Completed_ is sent
                            textLine = (String)nin.readObject();
                            while(!textLine.equals("_Fragment_Completed_")){
                                writer.write(textLine);
                                textLine = (String)nin.readObject();
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
                            message = new MyText(completeFile, Client.profileName);
                        }
                        else if(fileName.contains(".png") || fileName.contains(".jpeg")){
                            message = new MyImage(completeFile, Client.profileName);
                        }
                        else if(fileName.contains(".mp4") || fileName.contains(".wmv")){
                            message = new MyVideo(completeFile, Client.profileName);
                        }
                        Client.messagesPerTopic.get(topicName).add(message);
                        message.assemble("Clients\\Client" + Client.serverStubPort);
                        j++;
                    }

                    nconnection.close();
                }
                else if(line.equals("3")){
                    System.out.println("To which topic do you want to post:");
                    int j = 1;
                    Set<String> keys = Client.messagesPerTopic.keySet();
                    String choosenTopic = null;
                    ArrayList<String> topics = new ArrayList<String>();
                    for(String topic: keys){
                        System.out.println(j + ":" + topic);
                        topics.add(topic);
                        j++;
                    }
                    choosenTopic = topics.get( Integer.parseInt(scr.nextLine()) -1);
                    File[] listOfFiles = folder.listFiles();
                    int i = 1;
                    System.out.println("These are all the files you have, choose one: ");
                    for(File file:listOfFiles){
                        System.out.println(i + ":" + file);
                        i++;
                    }
                    line = scr.nextLine();
                    File choosenFile = listOfFiles[Integer.parseInt(line) - 1];
                    MultimediaFile message = null;
                    if(choosenFile.getName().contains(".txt")){
                        message = new MyText(choosenFile, profileName);
                    }
                    else if(choosenFile.getName().contains(".png") || choosenFile.getName().contains(".jpeg")){
                        message = new MyImage(choosenFile, profileName);
                    }
                    else if(choosenFile.getName().contains(".mp4") || choosenFile.getName().contains(".wmv")){
                        message = new MyVideo(choosenFile, profileName);
                    }
                    message.partition("Clients\\Client" + serverStubPort);
                    
                    Client.messagesPerTopic.get(choosenTopic).add(message);
                    out.writeObject(choosenTopic);
                    out.flush();
                    String brokerIp = (String)in.readObject();
                    String brokerPort = (String)in.readObject();
                    nconnection = new Socket(brokerIp, Integer.parseInt(brokerPort));
                    nout = new ObjectOutputStream(nconnection.getOutputStream());
                    nin = new ObjectInputStream(nconnection.getInputStream());
                    nout.writeObject("3");
                    nout.flush();
                    nout.writeObject(profileName);
                    nout.flush();
                    nout.writeObject(ipAddress);
                    nout.flush();
                    nout.writeObject(serverStubPort);
                    nout.flush();
                    nout.writeObject(choosenTopic);
                    nout.flush();
                    nout.writeObject(choosenFile.getName());
                    nout.flush();
                    nout.writeObject("10");
                    nout.flush();
                    System.out.println("hello1");
                    String cleanFileName = choosenFile.getName().substring(0, choosenFile.getName().lastIndexOf("."));
                    System.out.println("hello2");
                    File[] files = new File("Clients\\Client" + serverStubPort + "\\Fragments\\" + cleanFileName).listFiles();
                    for(File fragment : files){
                        nout.writeObject(fragment.getName());
                        BufferedReader reader = new BufferedReader(new FileReader(new File("Clients\\Client" + serverStubPort + "\\" + "Fragments" + "\\" + cleanFileName + "\\" + fragment.getName())));
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
                }
                else if(line.equals("4")){
                    System.out.println("Give a topic: ");
                    int j = 1;
                    Set<String> keys = Client.messagesPerTopic.keySet();
                    String choosenTopic = null;
                    ArrayList<String> topics = new ArrayList<String>();
                    for(String topic: keys){
                        System.out.println(j + ":" + topic);
                        topics.add(topic);
                        j++;
                    }
                    choosenTopic = topics.get( Integer.parseInt(scr.nextLine()) -1);
                    ArrayList<MultimediaFile> messages = Client.messagesPerTopic.get(choosenTopic);
                    System.out.println("---" + choosenTopic + "---");
                    for(MultimediaFile message:messages){
                        System.out.println(message.getProfileName() + ": " + message.getMultimediaFileName());
                    }
                    System.out.println();
                }
                else if(line.equals("5")){
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println(e);
            //TODO: handle exception
        }
    }

}
