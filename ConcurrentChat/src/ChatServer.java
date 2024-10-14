import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;


/**
 * ChatServer class to handle incoming client connections and broadcast messages
 */
public class ChatServer {

    public static final int PORT = 9998;
    private static Set<ServerWorker> serverWorkers = new HashSet<>();



    /**
     * Entry pont to start ChatServer
     * @param args
     */
    public static void main(String[] args) {

        init();

    }



    /**
     * Initializes the server, accepts client connection and starts a thread for each client
     */
    public static void init() {

        try {

            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Loading chat...");

            while (true) {

                ChatServer server = new ChatServer();
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection woohoo");

                ServerWorker serverWorker = new ServerWorker(clientSocket, server);
                serverWorkers.add(serverWorker);

                Thread thread = new Thread(serverWorker);
                thread.start();
                System.out.println("Thread server started" + thread.getName());

            }

        } catch (IOException e) {
            System.out.println("no input found");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }



    /**
     * Broadcasts a message to all connected clients except the sender
     * @param message the message to broadcast
     * @param sender the sender of the message
     */
    private void broadcastMessage(String message, ServerWorker sender) {
        synchronized (serverWorkers) {
            for (ServerWorker client : serverWorkers) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }


    private void removeClient(ServerWorker client) throws IOException {
        synchronized (serverWorkers) {
            serverWorkers.remove(client);
            client.socket.close();
            System.out.println(client + " has left the chat");
        }
    }



    /**
     * Runnable class to handle communication with a single client
     */
    private static class ServerWorker implements Runnable {

        private Socket socket;
        private ChatServer server;
        private String clientName;



        /**
         *
         * @param socket client socket
         * @param server ChatServer instance
         */
        public ServerWorker(Socket socket, ChatServer server) {
            this.socket = socket;
            this.server = server;
            setClientName();
            //this.clientName = getClientName();
        }



        /**
         * Handles communication with the client in a separate thread
         */
        @Override
        public void run() {

            try {

                handleMessages();

            } catch (IOException e) {
                e.printStackTrace();

            } finally {

                // socket.close();

            }
        }


        private void handleClient() throws IOException {

            while (true) {


                if (clientLogOut()) {
                    server.removeClient(this);
                    break;

                }

                handleMessages();
            }


        }




        /**
         * Reads messages from the client and broadcasts them
         * @throws IOException if an I/O error occurs
         */
        private void handleMessages() throws IOException {

            String message;

            while ((message = readClientInput()) != null) {
                System.out.println(message);
                server.broadcastMessage(message, this);

            }
        }



        /**
         * Reads a single line of input from the client
         * @return input from the client in a string
         * @throws IOException if an I/O error occurs
         */
        private String readClientInput() throws IOException {

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            return  in.readLine();

        }



        /**
         * Sends a message to the client
         * @param message the message to be sent
         */
        void sendMessage(String message) {

            try {

                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                out.println(clientName + ": " + message);

            } catch (IOException e) {

                e.printStackTrace();
            }

        }


        boolean clientLogOut() {

            try {
                String input = readClientInput();
                if(input != null && input.trim().equals("exit")) {
                //if(readClientInput().contains("quit")) {
                    System.out.println(clientName + " disconnected");
                    //socket.close();
                    return true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        private void setClientName()  {
            clientName = getClientName();
        }

        private String getClientName()  {

            try {
            BufferedReader nameReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

           // Scanner scanner = new Scanner(System.in);

                return nameReader.readLine();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

