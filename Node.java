import java.util.ArrayList;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Node {
    ServerSocket providerSocket;
	Socket acceptedConnection;
    Socket requestedConnection;
    
    public abstract void connect(int port);

    public abstract void disconnect();

}
