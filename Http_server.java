
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Scanner;
import java.util.stream.Stream;


public class Http_server {


    public Http_server() throws Exception {

        ServerSocket server_socket = new ServerSocket(80);
        while (true) {
            Socket socket = server_socket.accept();
            ServerThread server_thread = new ServerThread(socket, this);
            Thread thread = new Thread(server_thread);
            thread.start();
        }

    }

    public static void main(String[] args) {
        try {
            new Http_server();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}

class ServerThread implements Runnable {
    final Socket socket;
    final Http_server http_server;

    public ServerThread(Socket socket, Http_server http_server) {
        this.socket = socket;
        this.http_server = http_server;
    }
    @Override
    public void run() {
        try {
            BufferedReader in_socket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out_socket = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            out_socket.println("Please Enter the name of the required file");
            String message = in_socket.readLine();
            System.out.println("Request received");


            Stream<Path> walkStream = Files.walk(Paths.get("./")); //make a stream of file names in each file and folder in the directory
            walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {  // filter the stream to make shure it is a file and search the file
                if (f.toString().endsWith(message)) {   // make sure the file has the same name(matched)
                    if (socket.isClosed()) {
                        return;
                    }

                    File obj = new File(f.toString()); //file object created and read the path of the file we searched
                    if (obj.exists()) {
                        Scanner scanner = null;
                        try {
                            scanner = new Scanner(obj);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        String pth =  f.toString().replaceFirst("\\.","").replaceAll("\\\\","/"); // to make the path name right
                        String[] folders = pth.split("/");  //get the host folder name from the root
                        String hostFolder = "";
                        if (folders.length>1){
                            hostFolder = folders[1];
                        }else{
                            hostFolder="./";
                        }
                        System.out.println("GET"+ pth.replaceFirst(hostFolder+"/","") + " HTTP/1.1 \nHost: "+hostFolder); // print the path without the host folder
                        out_socket.println("HTTP/1.1 200 OK"); //resend ack that the file found
                        Date date = new Date();                 //make a now date - time
                        out_socket.println(date.toString());    //send the date - time
                        while (scanner.hasNextLine()) {     //send the file line by line
                            String line = scanner.nextLine();
                            out_socket.println(line);

                        }
                        scanner.close();
                        out_socket.println("-"); // end of the connection
                    } else {
                        out_socket.println("404 Not Found"); // send error not found if the file is not found
                    }
                    try {
                        socket.close();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }

                }
            });

            if (!socket.isClosed()) {
                out_socket.println("404 Not Found"); // close the socket if the file is not found
                socket.close();
            }


        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}

