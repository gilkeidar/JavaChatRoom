import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Client
{
    public static int id;
    public static String name; //name of user
    public static ClientReceivingThread clientReceivingThread;
    public static boolean connectionIsRunning = false;
    public String input;

    public void Run()
    {
        System.out.println("Client online.");
        Scanner sc = new Scanner(System.in);

        //get user's name
        String inputName;
        String ipServer;
        String port;
        boolean ConnectAnotherIP = false;
        do
        {

            do
            {
                System.out.print("Please enter your name: ");
                inputName = sc.nextLine();
                if (inputName.isEmpty())
                {
                    System.out.println("Invalid name.");
                }
            } while (inputName.isEmpty());

            name = inputName;

            do
            {
                System.out.print("Please enter a server's ip address you want to connect to: ");
                ipServer = sc.nextLine();
                if (ipServer.isEmpty())
                {
                    System.out.println("Invalid IP address. Enter something of the form XXX.X.X.X or localhost.");
                }
            } while (ipServer.isEmpty());

            System.out.println(ipServer);

            boolean invalidPortNumber = false;
            do
            {
                invalidPortNumber = false;
                System.out.print("Please enter the server's port that you want to connect to (standard for this application is 5000): ");
                port = sc.nextLine();
                if (port.isEmpty())
                {
                    System.out.println("Invalid port number. Must be only digits.");
                    invalidPortNumber = true;
                } else
                {
                    for (int i = 0; i < port.length(); i++)
                    {
                        if (port.charAt(i) < '0' || port.charAt(i) > '9')
                        {
                            invalidPortNumber = true;
                            break;
                        }
                    }
                }
            } while (invalidPortNumber);

            int portNumber = 0;
            for (int i = port.length() - 1; i >= 0; i--)
            {
                //System.out.println(port.charAt(i));
                portNumber += Math.pow(10, i) * (port.charAt(port.length() - 1 - i) - 48);
                //System.out.println("Port number: " + portNumber);
            }

            //System.out.println(portNumber);

            boolean reConnectCheck = false;
            do
            {
                reConnectCheck = false;
                RunChat(ipServer, portNumber);
                System.out.println("Client offline.");
                boolean inputCheck = false;
                do
                {
                    inputCheck = false;
                    System.out.print("Do you wish to reconnect? (y/n) ");
                    String askReconnect = sc.nextLine();
                    if (!askReconnect.toLowerCase().equals("y") && !askReconnect.toLowerCase().equals("n"))
                    {
                        System.out.println("Please enter y or n.");
                        inputCheck = true;
                    } else if (askReconnect.toLowerCase().equals("y"))
                    {
                        reConnectCheck = true;
                    }
                } while (inputCheck);

            } while (reConnectCheck);

            boolean inputCheck = false;

            do
            {
                inputCheck = false;
                System.out.print("Do you wish to connect to another chat server? ");
                String askReconnect = sc.nextLine();
                if (!askReconnect.toLowerCase().equals("y") && !askReconnect.toLowerCase().equals("n"))
                {
                    System.out.println("Please enter y or n.");
                    inputCheck = true;
                } else if (askReconnect.toLowerCase().equals("y"))
                {
                    ConnectAnotherIP = true;
                }
                else
                {
                    ConnectAnotherIP = false;
                }
            } while (inputCheck);
            System.out.println();
        } while(ConnectAnotherIP);

        System.out.println("Client is offline.");
    }

    public void RunChat(String ipServer, int portNumber)
    {
        Scanner sc = new Scanner(System.in);
        try
        {

            //establish connection
            System.out.println("Attempting connection...");
            Socket socket = new Socket(ipServer, portNumber);
            System.out.println("Connection established!");
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            //send server identification info, for instance name
            SendIdentification(out, name);

            clientReceivingThread = new ClientReceivingThread(socket, in, this);
            connectionIsRunning = true;
            clientReceivingThread.start();


            System.out.println("Waiting for server welcome message...");
            //System.out.println(in.readUTF()); //welcome message

            while(connectionIsRunning)
            {
                //System.out.print("> ");
                input  = sc.nextLine();
                out.writeUTF(input + "");
                out.flush(); //what does this do?
                //System.out.println("Waiting for a reply from the server...");

                //String answer = in.readUTF();
                //System.out.println(answer);
                /*if(answer.equals("Bye!"))
                {
                    connectionIsRunning = false;
                }*/
            }

        }
        catch(SocketException e)
        {
            connectionIsRunning = false;
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //System.out.println("Client offline.");
    }

    public void PrintServerOutput(String message)
    {
        System.out.print(message);
    }

    public void ClientDebugMessage(String message)
    {
        System.out.print(message);
    }

    public void PrintInputCharacter(String message)
    {
        System.out.print(message);
    }

    public void SendIdentification(DataOutputStream out, String name)
    {
        try
        {
            out.writeUTF(name);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
