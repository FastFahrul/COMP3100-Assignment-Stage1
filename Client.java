import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

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
                    int limiter = Integer.parseInt(limiterString[1]);

                    // send OK to ds-server
                    dout.write(("OK\n").getBytes());
                    dout.flush();

                    // arraylist to add array of server info string
                    ArrayList<String> serverInfo = new ArrayList<String>();

                    // print DATA received from ds-server line by line (number of lines provided by limiter)
                    for(int j=0; j<limiter; j++){
                        response = (String) dis.readLine();
                        serverInfo.add(response);
                    }

                    // make a custom comparator to sort items in serverInfo arrayList
                    Comparator<String> lrrComparator = new Comparator<String>() {
                        @Override
                        public int compare(String s1, String s2) {
                            String[] parts1 = s1.split(" ");
                            String[] parts2 = s2.split(" ");
                            int value1 = Integer.parseInt(parts1[4]);
                            int value2 = Integer.parseInt(parts2[4]);
                            int result = Integer.compare(value2, value1); // sort by index 4 in descending order
                            if (result == 0) { // if index 4 values are equal
                                int index2Value1 = Integer.parseInt(parts1[1]);
                                int index2Value2 = Integer.parseInt(parts2[1]);
                                result = Integer.compare(index2Value1, index2Value2); // sort by index 2 in ascending order
                            }
                            return result;
                        }
                    };

                    // call sort on serverInfo with custom
                    Collections.sort(serverInfo, lrrComparator);

                    for(String element: serverInfo){
                        System.out.println(element);
                    }
                    
                    // send OK to ds-server
                    dout.write(("OK\n").getBytes());
                    dout.flush();

                    // read and print out message
                    response = (String) dis.readLine();
                    System.out.println("message: " + response);

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