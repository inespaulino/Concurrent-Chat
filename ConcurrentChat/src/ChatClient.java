import java.io.*;
import java.net.Socket;
import java.util.Locale;
import java.util.Scanner;

/**
 * ChatClient class to manage the connection to the server and handle input/output streams
 */
public class ChatClient {

    public static final int PORT = 9998;
    public static final String SERVER_ADDRESS = "localhost";

    Socket socket;
    //String userName;


    /**
     * Entry point to start ChatClient
     *
     * @param args
     */
    public static void main(String[] args) {

        init();

    }


    /**
     * @param socket the socket connected to the server
     */
    private ChatClient(Socket socket) {
        this.socket = socket;
        //getUserName();
        //sendUserNameToServer();

    }


    /**
     * Initializes the chat client by connecting to the server
     * Starts the thread to handle server messages and begin communication
     */
    public static void init() {

        try {

            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            ChatClient client = new ChatClient(socket);

            Thread clientThread = new Thread(new ClientThread(client.socket));
            clientThread.start();

            client.sendUserNameToServer();

            client.communicateWithServer();

        } catch (IOException e) {
            System.out.println("Failed to connect to server");
            e.printStackTrace();
        }
    }

    /**
     * Communicates with server
     */
    private void communicateWithServer() {

        try {

            writeToServer();

        } catch (IOException e) {

            System.out.println("Error setting up I/O streams");
            e.printStackTrace();
        }
    }


    /**
     * Reads client input from the console
     *
     * @return client input as a string
     * @throws IOException IOException if and I/O error occurs
     */
    private String consoleInput() throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        return in.readLine();
    }


    /**
     * Sends client input to server
     *
     * @throws IOException IOException if and I/O error occurs
     */
    private void writeToServer() throws IOException {


        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String userInput;

        while ((userInput = consoleInput()) != null) {
            out.println(userInput);

            //if (disconnect()) {
            //  out.println("disconecting...");

        }
        //break;
    }


    private void disconnect() throws IOException {

        consoleInput().trim().equals("exit");
        System.out.println("Disconnecting from server...");
        socket.close();
        //out.println("disconecting...");


    }


    private String getUserName() {

        System.out.println("Enter Username: ");

        Scanner nameReader = new Scanner(System.in);

        String name = nameReader.nextLine();

        //nameReader.close();

        return name;

    }

    //private void setUserName () {
    //this.userName = getUserName();
    //}

    private void sendUserNameToServer() throws IOException {

        PrintWriter nameOutput = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        String name = getUserName();

        nameOutput.println(name);

        }






    /**
     * Runnable class to handle server messages in a separate thread
     */
    private static class ClientThread implements Runnable {

        private Socket socket;


        /**
         *
         * @param socket the socket connected to the server
         * @throws IOException IOException if and I/O error occurs
         */
        public ClientThread(Socket socket) throws IOException {

            this.socket = socket;
        }



        /**
         * Listens to server messages
         */
        @Override
        public void run() {
            try {

                listenServer();

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }



        /**
         * Reads and prints messages from server
         * @throws IOException IOException if and I/O error occurs
         */
        private void listenServer() throws IOException {

            BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String messageReceived;

            while ((messageReceived = serverIn.readLine()) != null) {
                System.out.println(messageReceived);

            }
        }
    }
}



