import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client{
    public static void main(String[] args){
        try{
            // create a new socker object that connects to ds-server on localhost port 50000
            Socket socket = new Socket("localhost",50000);

            // create a new DataOutputStream object to write/send data to the ds-server
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            
            // create BufferedReader object to read data sent from the ds-server
            BufferedReader dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // contains reponse from ds-server
            String response;

            // send HELO to ds-server
            dout.write(("HELO\n").getBytes());
            dout.flush();

            // read and print out message
            response = (String) dis.readLine();
            System.out.println("message: " + response);

            // send AUTH info to ds-server
            dout.write(("AUTH fahrul\n").getBytes());
            dout.flush();

            // read and print out message
            response = (String) dis.readLine();
            System.out.println("message: " + response);

            // send REDY to ds-server
            dout.write(("REDY\n").getBytes());
            dout.flush();

            // read and print out message
            response = (String) dis.readLine();
            System.out.println("message: " + response);

            int i = 0;

            while(i<1){
                if(!response.equals("NONE")){
                    // string array that contains JOBN command reponse from ds-sever
                    String[] jobnParts = response.split(" ");
                    // index number for Job ID in the JOBN command is 2
                    String JobID = jobnParts[2];
                    System.out.println("The Job ID is: " + JobID);

                    // send GETS All to ds-server
                    dout.write(("GETS All\n").getBytes());
                    dout.flush();

                    // read and print out message
                    response = (String) dis.readLine();
                    System.out.println("message: " + response);

                    // workout loop delimeter for DATA provided
                    String[] limiterString = response.split(" ");
                    int limiter = Integer.parseInt(limiterString[1]) + 1;
                    System.out.println(limiter);

                    // send OK to ds-server
                    dout.write(("OK\n").getBytes());
                    dout.flush();

                    // print DATA received from ds-server line by line (number of lines provided by limiter)
                    for(int j=0; j<limiter; j++){
                        response = (String) dis.readLine();
                        System.out.println("Server info is: " + response);
                    }

                    i++;
                }

                else{
                    // exit loop is NONE received from ds-server
                    break;
                }
            }

            // send QUIT to ds-server
            dout.write(("QUIT\n").getBytes());
            dout.flush();

            // read and print out message
            response = (String) dis.readLine();
            System.out.println("message: " + response);

            // close input stream, output stream and socket.
            dis.close();
            dout.close();
            socket.close();
        }
        catch(Exception e){
            // print error message if exception is thrown
            System.out.println(e);
        }
    }
}