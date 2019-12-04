public class TicTacToe extends ServerGame
{
    int size = 3;
    int currentPlayer = 1;
    int moveCounter = 0;
    int amountToWin = 3;

    char[][] board = new char[size][];
    int[] moveCoordinates;

    boolean isPlayingComputer;


    ClientThread[] clientsPlaying;
    Server server;

    public TicTacToe(boolean isPlayingComputer, ClientThread[] clientsPlaying, Server server)
    {
        this.isPlayingComputer = isPlayingComputer;
        this.clientsPlaying = clientsPlaying;
        this.server = server;
        for (int i = 0; i < size; i++)
        {
            board[i] = new char[size];
            for (int j = 0; j < size; j++)
            {
                board[i][j] = '.';
            }

        }
    }

    @Override
    public void PlayGame(int currentPlayer, ClientThread[] clientsPlaying, Server server, boolean isPlayingComputer)
    {
        int[] moveCoordinates = null;
        String messageSend = "";
        messageSend += PrintBoard(board);
        do
        {
            if(currentPlayer == 2 && isPlayingComputer)
            {
                //computer move
                moveCoordinates = ComputerMove();
            }
            else
            {
                //player move
                do
                {
                    messageSend += "\nPlayer " + currentPlayer + ", please make your move. (e.g., 2,2)";
                    clientsPlaying[currentPlayer - 1].SendToClient(messageSend);
                    synchronized(currentPlayerInput)
                    {
                        try
                        {
                            currentPlayerInput.wait();
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    if (!currentPlayerInput.isEmpty() && currentPlayerThread == clientsPlaying[currentPlayer - 1])
                    {

                        //player made a move
                        moveCoordinates = PlayerMove(currentPlayerInput);
                        if (moveCoordinates == null)
                        {
                            messageSend += "\nInvalid move.";
                            clientsPlaying[currentPlayer - 1].SendToClient(messageSend);
                        }
                    }
                } while (moveCoordinates == null);
            }
            moveCounter++;
            if(currentPlayer == 1)
            {
                board[moveCoordinates[0] -1][moveCoordinates[1] - 1] = 'X';
                currentPlayer = 2;
            }
            else
            {
                board[moveCoordinates[0] -1][moveCoordinates[1] - 1] = 'O';
                currentPlayer = 1;
            }
            messageSend += PrintBoard(board);
        } while(!CheckIfGameEnded(board, moveCounter, moveCoordinates[1] - 1, moveCoordinates[0] - 1, amountToWin));
        clientsPlaying[0].game = null;
    }

    public String PrintBoard(char[][] board)
    {
        String boardPrintOut = "";
        //print board
        boardPrintOut += "  ";
        for(int i = 0; i < board.length; i++)
        {
            boardPrintOut += i + 1 + " ";
        }
        boardPrintOut += "\n";
        int amountOfSpaces = board.length / 10;
        //System.out.println("  1 2 3");
        for(int i = 0; i < board.length; i++)
        {
            //fix this
            if(i + 1 >= 10)
            {
                amountOfSpaces--;
            }
            if(i + 1 >= 100)
            {
                amountOfSpaces--;
            }
            System.out.print(i + 1);
            for(int c = 0; c <= amountOfSpaces; c++)
            {
                boardPrintOut +=" ";
            }
            for(int j = 0; j < board[i].length; j++)
            {
                boardPrintOut += board[i][j] + " ";
            }
            boardPrintOut += "\n";
        }
        return boardPrintOut;
    }

    public int[] ComputerMove()
    {
        //if the previous player move made a string of 2 with an empty one at the end, block it.
        //else if there is an empty spot where you can put another O so that it makes a 2 and has an empty one at the end, then put one there
        //else put an O at a random open space
        return new int[0];
    }

    public boolean CheckIfGameEnded(char[][] board, int moveCounter, int moveX, int moveY, int amountToWin)
    {
        for(int i = -1; i <= 0; i++)
        {
            for(int j = -1; j <= 1; j++) //amount to find all combinations
            {
                if(j == 0 && i == 0)
                {
                    continue;
                }
                int amountInARow = 1;
                int distanceMultiplier = 1;
                boolean NoAmountInARow;
                //player's move = board[boardIndex[moveX] -1][moveX]
                while(true)
                {
                    NoAmountInARow = true;
                    if (moveX + distanceMultiplier * j >= 0 && moveX + distanceMultiplier * j < board[0].length && moveY + distanceMultiplier * i >= 0 && moveY + distanceMultiplier * i < board.length)
                    {
                        if (board[moveY][moveX] == board[moveY + distanceMultiplier * i][moveX + distanceMultiplier * j])
                        {
                            amountInARow++;
                            if(amountInARow >= amountToWin)
                            {
                                if (board[moveY][moveX] == 'X')
                                {
                                    clientsPlaying[0].SendToClient("Player 1 won!");
                                    if(!isPlayingComputer)
                                    {
                                        clientsPlaying[1].SendToClient("Player 1 won!");
                                    }
                                    return true;
                                } else if (board[moveY][moveX] == 'O')
                                {
                                    clientsPlaying[0].SendToClient("Player 2 won!");
                                    if(!isPlayingComputer)
                                    {
                                        clientsPlaying[1].SendToClient("Player 2 won!");
                                    }
                                    return true;
                                }
                            }
                            NoAmountInARow = false;
                            distanceMultiplier++;
                        }

                    }
                    if (moveX - distanceMultiplier * j >= 0 && moveX - distanceMultiplier * j < board[0].length && moveY - distanceMultiplier * i >= 0 && moveY - distanceMultiplier * i < board.length)
                    {
                        if (board[moveY][moveX] == board[moveY - distanceMultiplier * i][moveX - distanceMultiplier * j])
                        {
                            amountInARow++;
                            if(amountInARow >= amountToWin)
                            {
                                if (board[moveY][moveX] == 'X')
                                {
                                    clientsPlaying[0].SendToClient("Player 1 won!");
                                    if(!isPlayingComputer)
                                    {
                                        clientsPlaying[1].SendToClient("Player 1 won!");
                                    }
                                    return true;
                                } else if (board[moveY][moveX] == 'O')
                                {
                                    clientsPlaying[0].SendToClient("Player 2 won!");
                                    if(!isPlayingComputer)
                                    {
                                        clientsPlaying[1].SendToClient("Player 2 won!");
                                    }
                                    return true;
                                }
                            }
                            NoAmountInARow = false;
                            distanceMultiplier++;
                        }
                    }
                    if(NoAmountInARow)
                    {
                        break;
                    }
                }
            }
        }
        if(moveCounter >= board.length * board[0].length)
        {
            clientsPlaying[0].SendToClient("Game is drawn.");
            if(!isPlayingComputer)
            {
                clientsPlaying[1].SendToClient("Game is drawn.");
            }
            return true;
        }
        return false;
    }

    public int[] PlayerMove(String input)
    {
        int[] playerMove = new int[2];
        if(input.length() != 3)
        {
            return null;
        }
        else
        {
            int firstCoordinate = (int)input.charAt(0) - 48;
            int secondCoordinate = (int)input.charAt(2) - 48;
            if(firstCoordinate >= 1 && firstCoordinate <= board.length)
            {
                playerMove[1] = firstCoordinate;
            }
            else
            {
                return null;
            }
            if(secondCoordinate >= 1 && secondCoordinate <= board.length)
            {
                playerMove[0] = secondCoordinate;
                if(board[playerMove[0] - 1][playerMove[1] - 1] != '.')
                {
                    return null;
                }
                return playerMove;
            }
            else
            {
                return null;
            }
        }
    }

}
