import java.util.ArrayList;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.math.*;

public class MasterBrokerService extends Thread{
    
    ObjectInputStream in;
	ObjectOutputStream out;

    public MasterBrokerService(Socket connection) {
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

    public void run(){
		try {
			String line = (String)in.readObject();
			
			if(line.equals("Client")){
				while(true){
					line = (String)in.readObject();
					if(line.equals("1")){
						String topicName = (String)in.readObject();
						out.writeObject(getNextAvailableBrokerIp());
						out.flush();
						out.writeObject(getNextAvailableBrokerPort());
						out.flush();
						putTopicsPerBroker(topicName);
						updateBrokerUtilization();
						updateNextAvailableBroker();
					}
					else if(line.equals("2")){
						String topicName = (String)in.readObject();
						int tempIndex = MasterBroker.topicsPerBroker.get(topicName);
						out.writeObject(MasterBroker.availableBrokerPorts.get(tempIndex).get(0));
						out.flush();
						out.writeObject(MasterBroker.availableBrokerPorts.get(tempIndex).get(1));
						out.flush();
						//MasterBroker.brokerUtilization.replace(MasterBroker.topicsPerBroker.get(topicName), MasterBroker.brokerUtilization.get(MasterBroker.topicsPerBroker.get(topicName)) + 1);
					}
					else if(line.equals("3")){
						String topicName = (String)in.readObject();
						int tempIndex = MasterBroker.topicsPerBroker.get(topicName);
						out.writeObject(MasterBroker.availableBrokerPorts.get(tempIndex).get(0));
						out.flush();
						out.writeObject(MasterBroker.availableBrokerPorts.get(tempIndex).get(1));
						out.flush();
					}
					else if(line.equals("4")){
						
					}
				}
			}
			if(line.equals("Broker")){
				System.out.println("Broker added!");
				String ip = (String)in.readObject();
				String port = (String)in.readObject();
				System.out.println(ip);
				System.out.println(port);
				ArrayList<String> tempList = new ArrayList<String>();
				tempList.add(ip);
				tempList.add(port);
				addAvailableBrokerPorts(tempList);
				addBrokerUtilization();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public synchronized String getNextAvailableBrokerIp(){
		return MasterBroker.availableBrokerPorts.get(MasterBroker.nextAvailableBroker).get(0);
	}

	public synchronized String getNextAvailableBrokerPort(){
		return MasterBroker.availableBrokerPorts.get(MasterBroker.nextAvailableBroker).get(1);
	}

	public synchronized void putTopicsPerBroker(String topicName){
		MasterBroker.topicsPerBroker.put(topicName, MasterBroker.nextAvailableBroker);
	}

	public synchronized void addBrokerUtilization(){
		MasterBroker.brokerUtilization.add(0);
	}

	public synchronized void updateBrokerUtilization(){
		MasterBroker.brokerUtilization.set(MasterBroker.nextAvailableBroker, MasterBroker.brokerUtilization.get(MasterBroker.nextAvailableBroker)+1);
	}

	public synchronized void updateNextAvailableBroker(){
		if(MasterBroker.nextAvailableBroker != 2){
			MasterBroker.nextAvailableBroker++;
		}
		else{
			MasterBroker.nextAvailableBroker -= 2;
		}
	}

	public synchronized void addAvailableBrokerPorts(ArrayList<String> list){
		MasterBroker.availableBrokerPorts.add(list);
	}
}
