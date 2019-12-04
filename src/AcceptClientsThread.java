import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AcceptClientsThread extends Thread
{
    Server server;
    boolean isRunning = false;
    ServerSocket socket;
    List<ClientThread> clientThreads = new ArrayList<ClientThread>();
    List<Socket> clients = new ArrayList<Socket>();
    public AcceptClientsThread(ServerSocket socket, List<ClientThread> clientThreads, Server server)
    {
        this.server = server;
        this.socket = socket;
        this.clientThreads = clientThreads;
        this.isRunning = true;
    }
    @Override
    public void run()
    {
        DataInputStream in;
        String userName;
        while(isRunning)
        {
            try
            {
                server.DebugMessage("Waiting for new client to connect...");
                Socket newUser = socket.accept();
                server.DebugMessage("New client connection established!");

                //should get user's id
                in = new DataInputStream(new BufferedInputStream(newUser.getInputStream()));
                userName = in.readUTF();

                this.clients.add(newUser);
                server.DebugMessage("New client added to clients list.");
                this.clientThreads.add(new ClientThread(newUser, new DataInputStream(new BufferedInputStream(newUser.getInputStream())), new DataOutputStream(newUser.getOutputStream()), server, userName));
                server.DebugMessage("New client thread created.");
                server.DebugMessage("New client thread run method called.");
                server.DebugMessage("Sending client a welcome message...");
                this.clientThreads.get(clientThreads.size() - 1).SendToClient("Welcome to the chat!");
                this.clientThreads.get(clientThreads.size() - 1).start();


            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }
}
