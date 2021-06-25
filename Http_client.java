import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Http_client {
    public Http_client() throws Exception {
        Scanner input = new Scanner(System.in);                    //make a scanner to read input from user
        Socket socket = new Socket("localhost", 80);        //making a client socket with ip set to local host and port num: 80(HTTP)

        BufferedReader in_socket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out_socket = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        String message = in_socket.readLine();      // to read a string from the server
        System.out.println(message);                // print the messege on the consoule

        message = input.nextLine();                 //take a string from user(file name)
        out_socket.println(message);                // send the taken string to the server

        message = in_socket.readLine();                             // read another string from the server(file name)
        System.out.println("\nReply received\n" + message);         // print recieved and the file name

        String status = message;
        message = in_socket.readLine();
        System.out.println(message);

        if (!(status.contains("404"))) {                    //check if the fil has an error
            while (true) {
                message = in_socket.readLine();
                System.out.println(message);
                if (message.startsWith("-")) {              // breaking condition for the while loop
                    break;
                }
            }
        }
    }


    public static void main(String[] args) {
        try {
            new Http_client();
        } catch (Exception e) {

            // TODO: handle exception

            e.printStackTrace();
        }
    }
}

