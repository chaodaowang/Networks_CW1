import java.net.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * @Author chaodao wang
 * StudentNumber 201581879
 * I've tried to implement the callable interface to get a better performance
 * but failed
 * May be the best ClientHandler in this class
 */
public class ClientHandler extends Thread {

    private Socket socket = null;
    private NameList nameList = null;

    // Command string to instore the command line for more flexible coding
    private static String[] command = {"totals", "list", "join"};

    //Constructor
    public ClientHandler(Socket connection, NameList nameList) {
        super("ClientHandler");
        this.socket = connection;
        this.nameList = nameList;
    }

    public static String getCommand(int index) {
        return command[index];
    }

    // to log every client request
    public void log(String userCommand) {
            try {
                // Chain stream
                String pathName = "log.txt";
                File logFile = new File(pathName);
                FileWriter writename = new FileWriter(logFile.getName(),true);
                BufferedWriter logwriter = new BufferedWriter(writename);

                // get information about date and IP address
                InetAddress inet = socket.getInetAddress();
                SimpleDateFormat f = new SimpleDateFormat ("yyyy-MM-dd | HH:mm:ss");

                // Assemble the log line
                String logRecord = f.format(new Date())+" | " + inet + " | "+ userCommand;

                logwriter.write(logRecord);
                logwriter.newLine();
                logwriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void run() {
        try {
            // Initialize input and output stream
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // read first command line to decide what to do next
            String userCommand = in.readLine();

            // If it is "totals" or "list" or "join"
            if(userCommand.equals(getCommand(0))){

                // Return the number of lists and the maximum per list
                int listNumber = nameList.getListNumber();
                int memberMax = nameList.getMemberMax();
                out.println(listNumber);
                out.println(memberMax);

                // Return the current number of members of each list
                for(int i=0;i<nameList.getListNumber();i++) {
                    out.println(nameList.getMemberNumber(i));
                }
            } else if(userCommand.equals(getCommand(1))) {

                // Check which list to return
                int listIndex = Integer.parseInt(in.readLine());
                if(listIndex <= nameList.getListNumber())
                    out.println(nameList.getAllMemberNameInString(listIndex-1));
                else {
                    // tell the client index out of bound
                    out.println("32767");
                }
            } else if(userCommand.equals(getCommand(2))) {

                // Check which list to return
                int listIndex = Integer.parseInt(in.readLine());
                if(listIndex > nameList.getListNumber())
                    // tell the client index out of bound
                    out.println("32767");
                
                // Add the given name to the namelist
                String name = in.readLine();
                if(nameList.addName(listIndex-1,name))
                    out.println("success");
                else
                    out.println("failed");
            }

            // Write log file
            log(userCommand);

            // Free up resources for this connection.
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }
}

