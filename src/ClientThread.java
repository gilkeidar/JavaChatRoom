import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientThread extends Thread
{
    Server server;
    Socket client;
    DataInputStream in;
    DataOutputStream out;
    String clientName;
    boolean clientConnected = true;
    ServerGame game = null;
    public ClientThread(Socket client, DataInputStream in, DataOutputStream out, Server server, String clientName)
    {
        this.server = server;
        this.client = client;
        this.in = in;
        this.out = out;
        this.clientName = clientName;
    }
    @Override
    public void run()
    {
        server.DebugMessage("Client Thread is alive!");
        synchronized(server.messages)
        {
            server.messages.add(clientName + " joined the chat.");
            server.messages.notify();
        }
        while(clientConnected)
        {

            try
            {
                server.DebugMessage("Client Thread waiting for client input...");
                server.DebugMessage("in: " + in + " | out: " + out);
                if(in == null || out == null)
                {
                    server.DebugMessage("Client disconnected! in or out data streams are null!");
                    clientConnected = false;
                    break;
                }
                String userMessage = in.readUTF();
                server.DebugMessage("Client Thread received client input!!!");
                server.DebugMessage("Client Thread calling ClientMessage method");
                server.ClientMessage(this, userMessage);
                server.DebugMessage("Client Thread successfully called ClientMessage method");
            } catch(SocketException e)
            {
                //client crashes
                server.CloseClientConnection(this);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void SendToClient(String message)
    {
        server.DebugMessage("Attempting to send a server message (which could be a client's message resent) to the client!");
        synchronized(client)
        {
            try
            {
                server.DebugMessage("Attempting to send a server message (which could be a client's message resent) to the client!");
                out.writeUTF(message);
                server.DebugMessage("Succeeded to send the client a message!!!");
            }
            catch(SocketException e)
            {
                //client crashes
                server.CloseClientConnection(this);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
