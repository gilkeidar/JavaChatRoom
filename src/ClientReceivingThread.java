import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientReceivingThread extends Thread
{
    Client client;
    Socket socket;
    DataInputStream in;
    //boolean isConnected = false;

    public ClientReceivingThread(Socket socket, DataInputStream in, Client client)
    {
        this.socket = socket;
        this.in = in;
        this.client = client;
    }

    @Override
    public void run()
    {
        String message;
        while(client.connectionIsRunning)
        {
            try
            {
                //client.ClientDebugMessage("Waiting for server output...");
                message = in.readUTF();
                FormatSpaces(client.name + ": " + client.input, message); //improve this! Make sure it's the client's message, and not anyone else's! Even if someone else sends exactly the same message!
                client.PrintServerOutput("" + message);
                client.PrintInputCharacter("\n" + "> ");
                if(message.equals("Bye!"))
                {
                    client.connectionIsRunning = false;
                }
            }
            catch(SocketException e)
            {
                client.connectionIsRunning = false;
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void FormatSpaces(String messageClientOutput, String messageServerInput)
    {
        if(messageClientOutput != null && messageServerInput != null)
        {
            if (!messageClientOutput.equals(messageServerInput)) //if client sent the message, kind of... what happens if a bunch of people say the same thing?
            {
                client.ClientDebugMessage("\n");
            }
        }
    }
}
