public class ServerGame
{
    ClientThread currentPlayerThread = null;
    String currentPlayerInput = "";
    public ClientThread ReceiveClientThread(ClientThread thread)
    {
        return thread;
    }
    public String ReceiveClientMessage(String message)
    {
        return message;
    }

    public void PlayGame(int currentPlayer, ClientThread[] clientsPlaying, Server server, boolean isPlayingComputer)
    {

    }
}
