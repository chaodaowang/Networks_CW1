import java.io.*;
import java.net.*;

/**
 * @Author chaodao wang
 * StudentNumber 201581879
 * May be the best Client in this class
 */
public class Client 
{
	private Socket socket = null;
	private PrintWriter socketOut = null;
	private BufferedReader socketIn = null;
	private static String[] command = {"totals", "list", "join"};
	public final static int PORT = 9518;

	public static String getCommand(int index) {
		return command[index];
	}

	// Determine whether the arguments is integer
	public static boolean isInteger(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

	public Client(int port) {
		try {
			// Try and create the socket. This assumes the server is running on the same machine, "localhost".
			socket = new Socket("localhost", port );
			// To prevent the socket from running too long time
			socket.setSoTimeout(15000);
			// Chain a writing stream
			socketOut = new PrintWriter(socket.getOutputStream(), true);
			// Chain a reading stream
			socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Couldn't find server.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to server.");
			System.exit(1);
		}
	}

	public void processTotals(String[] args) {
		try {
			//Output the command 'list'
			socketOut.println(getCommand(0));

			int listNumber = Integer.parseInt(socketIn.readLine());
			System.out.println("the number of lists : " + listNumber);
			int memberMax = Integer.parseInt(socketIn.readLine());
			System.out.println("the maximum per list : " + memberMax);
			System.out.println("the current number of members of each list : ");
			for(int i=1;i<=listNumber;i++) {
				System.out.println("list " + i + " : " + socketIn.readLine());
			}
			// Free resources
			socketOut.close();
			socketIn.close();
			socket.close();
		}
		catch (IOException e) {
			System.err.println("I/O exception during execution");
			System.exit(1);
		}
	}

	public void processList(String[] args) {
		try {
			String inputLine;
			//Output the command 'list'
			socketOut.println(getCommand(1));
			// Check whether the args[1] is an integer
			if(isInteger(args[1])){
				socketOut.println(args[1]);
			} else {
				System.err.println("Not an Integer");
				System.exit(1);
			}
			while( (inputLine = socketIn.readLine())!=null ) {
				if(inputLine.equals("32767")) {
					System.err.println("Index out of bound");
					System.exit(1);
				} else
					System.out.println(inputLine);
			}
			// Free resources
			socketOut.close();
			socketIn.close();
			socket.close();
		}
		catch (IOException e) {
			System.err.println("I/O exception during execution");
			System.exit(1);
		}
	}

	public void processJoin(String[] args) {
		try {
			String inputLine;

			socketOut.println(getCommand(2));

			if(isInteger(args[1])){
				socketOut.println(args[1]);
			} else {
				System.err.println("The second command should be an integer");
				System.exit(1);
			}
			// Already processed in the main method, just send without check
			socketOut.println(args[2]);

			while( (inputLine = socketIn.readLine())!=null ) {
				if(inputLine.equals("32767")) {
					System.err.println("Index out of bound");
					System.exit(1);
				} else
					System.out.println(inputLine);
			}
			// Free resources
			socketOut.close();
			socketIn.close();
			socket.close();
		}
		catch (IOException e) {
			System.err.println("I/O exception during execution\n");
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		if(args.length < 1){
			System.err.println("Need at least 1 arguments");
			System.exit(1);
		}
		if(args[0].equals(getCommand(0))) {
			if(args.length == 1) {
				Client client = new Client(PORT);
				client.processTotals(args);
			} else {
				System.err.println("Please enter 'totals'");
				System.exit(1);
			}
		} else if(args[0].equals(getCommand(1)) ) {
			if(args.length == 2 && !args[1].equals("0")) {
				Client client = new Client(PORT);
				client.processList(args);
			} else {
				System.err.println("Please enter 'list index' and index start at 1");
				System.exit(1);
			}
		} else if(args[0].equals(getCommand(2))) {
			if(args.length > 2 && !args[1].equals("0")) {
				// process the "" 
				String temp = args[2];
				for(int i=3; i<args.length; i++)
					temp += " "+args[i];
				args[2] = temp.replace("\"","");

				Client client = new Client(PORT);
				client.processJoin(args);
			} else {
				System.err.println("Please enter 'join index name' and index start at 1");
				System.exit(1);
			}
		} else {
			System.err.println("please enter right command and in right format");
			System.exit(1);
		}
	}
}