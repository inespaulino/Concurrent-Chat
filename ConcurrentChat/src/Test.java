import java.io.*;
import java.net.Socket;

public class Test {

    public static void main(String[] args) {




    class ServerWorker implements Runnable {

        private Socket clientSocket;

        private String messageReceived; //??

        //private ServerSocket serverSocket;


        public ServerWorker(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public String getAddress() {

            System.out.println(clientSocket.getInetAddress().getHostAddress() + " : " + clientSocket.getLocalPort());

            return clientSocket.getInetAddress().getHostAddress() + " : " + clientSocket.getLocalPort();

        }


        //reads from server and writes to console
        public void read() throws IOException {


            BufferedReader readServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            messageReceived = readServer.readLine();

            PrintWriter printMessage = new PrintWriter(new OutputStreamWriter(System.out));

            printMessage.write(messageReceived);
            printMessage.flush();

            System.out.println(" new message : " + messageReceived);

        }


        //gets message from user input and sends to server
        public void write() throws IOException {

           BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));

            String userMessage = userIn.readLine();

           PrintWriter printUser = new PrintWriter(new OutputStreamWriter(System.out), true);

            //System.out.println(userMessage);
            printUser.write(userMessage);
            printUser.flush();

            if (userMessage != null) {

                PrintWriter writeServer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                writeServer.write(userMessage);
                writeServer.flush();

            } else {
                System.out.println("no message");
            }
        }


        @Override
        public void run() {

            try {
                while(messageReceived != null) { //para estar sempre à escuta?
                    read();
                }
                write();


            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        }
    }

}

}

