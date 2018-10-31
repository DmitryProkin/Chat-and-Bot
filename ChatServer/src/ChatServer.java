/**
 * Created by Dmitry on 23.02.2018.
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    ArrayList clientOutputSteram;
    public class ClientHandler implements Runnable{
        BufferedReader reader;
        Socket sock;
        public ClientHandler(Socket clientSocket){
            try{
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);

            }catch(Exception ex){ex.printStackTrace();}
        }

        @Override
        public void run() {
            String message;
            try{
                while ((message = reader.readLine()) != null){
                    System.out.println("read "+message);
                    tellEveryone(message);
                }
            }catch(Exception ex){ex.printStackTrace();}


        }

    }

    public static void main(String[] args) {
        new ChatServer().go();
    }

    public void go(){
        clientOutputSteram = new ArrayList();
        try{
            ServerSocket serverSock = new ServerSocket(5000);

            while(true){
                Socket clientSocket = serverSock.accept();
                PrintWriter writer = new PrintWriter( clientSocket.getOutputStream());
                clientOutputSteram.add(writer);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                System.out.println("got a connection");
            }
        }catch (Exception ex){ex.printStackTrace();}
    }

    public void tellEveryone(String messag){
        Iterator it = clientOutputSteram.iterator();
        while (it.hasNext()){
            try{

                PrintWriter writer = (PrintWriter)it.next();
                writer.println(messag);
                writer.flush();

            }catch (Exception ex){ex.printStackTrace();}
        }
    }

}
