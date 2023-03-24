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
            
            //create BufferedReader object to read data sent from the ds-server
            BufferedReader dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch(Exception e){
            // print error message if exception is thrown
            System.out.println(e);
        }
    }
}