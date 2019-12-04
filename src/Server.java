import com.intellij.pom.java.AcceptedLanguageLevelsSettings;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server
{
    ServerSocket socket;
    AcceptClientsThread acceptClientsThread;
    List<ClientThread> clientThreads = new ArrayList<ClientThread>();
    boolean isRunning = false;
    List<String> messages = new ArrayList<String>();
    List<String> commands = new ArrayList<String>();

    public void Run()
    {
        System.out.println("Server online.");
        isRunning = true;
        try
        {
            int port = 5000;
            socket = new ServerSocket(port);
            System.out.println("Server port established, port " + port + ".");
            System.out.println("Launching Accept Clients Thread.");
            acceptClientsThread = new AcceptClientsThread(socket, clientThreads, this);
            acceptClientsThread.start();
            while(isRunning)
            {
                synchronized(messages)
                {
                    messages.wait();
                    String temp;
                    //System.out.println("Server looped!");
                    while (messages.size() > 0)
                    {
                        for (int i = 0; i < clientThreads.size(); i++)
                        {
                            System.out.println("Sending message " + '"' + messages.get(0) + '"' + " to client " + i);
                            clientThreads.get(i).SendToClient(messages.get(0));
                        }
                        messages.remove(0);
                    }

                }
            }
        } catch (IOException | InterruptedException | IllegalMonitorStateException e)
        {
            e.printStackTrace();
        }

    }

    public void ClientMessage(ClientThread thread, String message)
    {
        System.out.println("Server received client's message!");
        System.out.println("Client said " + message);
        if(thread.game != null)
        {
            //send input from user to the game object... how?
            thread.game.currentPlayerThread = thread.game.ReceiveClientThread(thread);
            synchronized(thread.game.currentPlayerInput)
            {
                thread.game.currentPlayerInput = thread.game.ReceiveClientMessage(message);
                System.out.println("Current player input: " + thread.game.currentPlayerInput);
                thread.game.currentPlayerInput.notifyAll();
            }
        }
        if(message.startsWith("~")) //user sends a command
        {
            String messageWithoutTilda = message.substring(1);
            String[] commandArray = messageWithoutTilda.split(" ");
            Command(thread, commandArray);
            //quit command
            //thread.SendToClient("Bye!");
            //CloseClientConnection(thread);
        }
        else
        {
            synchronized(messages)
            {
                messages.add(thread.clientName + ": " + message);
                messages.notify();
            }
            System.out.println("Client's message has been added to the message list.");
            System.out.println("Message queue size: " + messages.size());
            System.out.println("Client thread list size: " + clientThreads.size());
        }
    }

    public void DebugMessage(String message)
    {
        System.out.println(message);
    }

    public void CloseClientConnection(ClientThread thread)
    {
        String clientName = thread.clientName;
        thread.clientConnected = false;
        clientThreads.remove(thread);
        try
        {
            thread.out.close();
            thread.client.close();
            clientThreads.remove(thread);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        synchronized(messages)
        {
            messages.add(clientName + " left the chat.");
            messages.notify();
        }
    }

    public void Command(ClientThread thread, String[] userCommand)
    {
        //switch case over all commands(?) maybe have some sorting algorithm going by each letter, comparing way faster perhaps?
        //Then, based on the command found, use the index of the command in the command list to run the command...? Switch case over the command index...?

        switch(userCommand[0].toLowerCase())
        {
            case "numberofusers":
                if(clientThreads.size() <= 1)
                {
                    thread.SendToClient("There is " + clientThreads.size() + " user online.");
                }
                else{
                    thread.SendToClient("There are " + clientThreads.size() + " users online.");
                }
                break;
            case "listusers":
                String list = "";
                if(clientThreads.size() <= 1)
                {
                    list += "There is " + clientThreads.size() + " user online, that is: \n";
                }
                else{
                    list += "There are " + clientThreads.size() + " users online, that are: \n";
                }

                for(int i = 0; i < clientThreads.size(); i++)
                {
                    list += clientThreads.get(i).clientName + "\n";
                }
                thread.SendToClient(list);
                break;
            case "play":
                if(userCommand.length >= 2)
                {
                    switch(userCommand[1].toLowerCase())
                    {
                        case "tic-tac-toe":
                            if(userCommand.length >=3)
                            {
                                switch(userCommand[2].toLowerCase())
                                {
                                    case "computer":
                                        PlayTicTacToe(thread, true, "Computer");
                                        break;
                                    default:
                                        thread.SendToClient("Currently you can only play against the computer.");
                                        break;
                                }
                            }
                            else
                            {
                                thread.SendToClient("Please specify if you wish to play against me or against another user.");
                            }
                            break;
                        default:
                            thread.SendToClient("That's not a game I can play.");
                            break;
                    }
                }
                else
                {
                    thread.SendToClient("Please specify the game you wish to play.");
                }
                break;
            case "quit":
                thread.SendToClient("Bye!");
                CloseClientConnection(thread);
                break;
            default:
                thread.SendToClient("That is not a command I recognize.");
                break;
        }
    }

    public void PlayTicTacToe(ClientThread thread, boolean opponentIsComputer, String opponentName)
    {
        //allows user to play against computer or against another player.
        if(opponentIsComputer)
        {
            thread.game = new TicTacToe(true, new ClientThread[]{thread}, this);
            thread.game.PlayGame(1, new ClientThread[] {thread}, this, true);
            //play against computer
        }
        else
        {
            //play against a user
        }
    }

    //when server stops, isrunning = false and also acceptClientsThread.isRunning = false;
}
