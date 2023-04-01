import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.RepaintManager;

public class Client {
    public static void main(String[] args) {
        try {
            // create a new socker object that connects to ds-server on localhost port 50000
            Socket socket = new Socket("localhost", 50000);

            // create a new DataOutputStream object to write/send data to the ds-server
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());

            // create BufferedReader object to read data sent from the ds-server
            BufferedReader dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // contains reponse from ds-server
            String response;

            // Job ID variable
            int jobID;

            // Server ID variable
            int serverID;

            // total number of largest server types
            int totalServers;

            // name of server type
            String serverType;

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

            while (true) {
                // send REDY to ds-server
                dout.write(("REDY\n").getBytes());
                dout.flush();

                // read and print out message
                response = (String) dis.readLine();
                System.out.println("message: " + response);

                while (!response.equals("NONE")) {
                    // string array that contains JOBN command reponse from ds-sever
                    String[] jobnParts = response.split(" ");
                    // index number for Job ID in the JOBN command is 2
                    jobID = Integer.parseInt(jobnParts[2]);
                    System.out.println("The Job ID is: " + jobID);

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
                    ArrayList<String[]> serverInfo = new ArrayList<String[]>();

                    // print DATA received from ds-server line by line (number of lines provided by
                    // limiter)
                    for (int j = 0; j < limiter; j++) {
                        response = (String) dis.readLine();
                        serverInfo.add(response.split(" "));
                    }

                    // send OK to ds-server
                    dout.write(("OK\n").getBytes());
                    dout.flush();

                    // read and print out message
                    response = (String) dis.readLine();
                    System.out.println("message: " + response);

                    // array list of all the servers with the largest cores
                    ArrayList<String[]> largestServers = new ArrayList<String[]>();

                    // find largest number of cores in the server list
                    int maxValue = 0;
                    for (String[] element : serverInfo) {
                        int result = Integer.parseInt(element[4]);
                        if (result > maxValue) {
                            maxValue = result;
                        }
                    }

                    System.out.println("The largest number of cores in a server is: " + maxValue);

                    // add the severs into largestServer arrayList that have the largest number of
                    // cores
                    for (String[] element : serverInfo) {
                        String maxString = String.valueOf(maxValue);
                        if (element[4].equals(maxString)) {
                            largestServers.add(element);
                        }
                    }

                    // index of largestServers arrayList
                    int index;
                    serverType = largestServers.get(0)[0];

                    while (!response.equals("NONE")) {
                        totalServers = largestServers.size();
                        index = jobID % totalServers;

                        serverID = Integer.parseInt(largestServers.get(index)[1]);
                        System.out.println(serverID);

                        // send SCHD to ds-server
                        dout.write(("SCHD " + String.valueOf(jobID) + " " + serverType + " " + String.valueOf(serverID)
                                + "\n").getBytes());

                        // read and print out message from ds-server
                        response = (String) dis.readLine();
                        System.out.println("message: " + response);

                        dout.write(("REDY\n").getBytes());
                        dout.flush();
                        
                        response = (String) dis.readLine();
                        System.out.println("message: " + response);

                        String[] tester = response.split(" ");

                        if(tester[0].equals("JOBN")){
                            jobnParts = response.split(" ");
                            jobID = Integer.parseInt(jobnParts[2]);
                            System.out.println("The Job ID is: " + jobID);
                        }
                    }

                }
                
                // break from loop if reponse is none
                if (response.equals("NONE")) {
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

        } catch (Exception e) {
            // print error message if exception is thrown
            System.out.println(e);
        }
    }
}