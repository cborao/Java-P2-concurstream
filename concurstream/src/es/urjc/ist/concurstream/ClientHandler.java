package es.urjc.ist.concurstream;

import java.io.*;
import java.util.Scanner;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;


/**
 * This class implements a client handler to process our connected clients concurrently.
 * In  this case, the class implements {@link Callable} interface to manage the execution result.
 * 
 * @author César Borao
 *
 * @version 2.0
 */
public class ClientHandler implements Callable<String> {

	private static final String CHARSET_NAME = "UTF-8";

	// We create two constants to represent all the errors that can occur in the
	// client Handler
	private static final int ACTION_ERROR = 1;
	private static final int EMPTY_ERROR = 2;

	// We establish the maximum and minimum time that our thread could wait
	private static final int MAXMILIS = 3000;
	private static final int MINMILIS = 1000;

	private Socket clientSocket;
	private UserTable userTable;

	/**
	 * Constructor with arguments to build a Client Handler.
	 * 
	 * @param clientSocket
	 * @param userTable
	 */
	public ClientHandler(Socket clientSocket, UserTable userTable) {
		this.clientSocket = clientSocket;
		this.userTable = userTable;
	}

	/**
	 * This method process a client
	 */
	@Override
	public String call() {
		Scanner scanner;
		PrintWriter out;
		int failed = 0;

		// IO configuration
		try {
			InputStream is = clientSocket.getInputStream();
			scanner = new Scanner(new BufferedInputStream(is), CHARSET_NAME);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		} catch (IOException ioe) {
			throw new IllegalArgumentException("Impossible to create: " + clientSocket, ioe);
		}

		// User name petition to the client
		out.println("Introduce username: ");

		// blocked on nextLine() until a client response is received
		String user = null;
		try {
			user = scanner.nextLine();
		} catch (NoSuchElementException e) {
			user = null;
		}

		// Testing if the user is included in UserTable
		if (!(this.userTable.getUsers().containsKey(user))) {
			System.out.println("unknown user: " + user + ", creating...");
			userTable.getUsers().put(user, userTable.getDefaultPlaylist());
		}

		// Reference to the user playlist
		// ConcurrentPlaylist playlist = this.userTable.getUsers().get(user);
		ConcurrentPlaylist playlist = this.userTable.getUsers().get(user);
		
		// Executing actions...

		// Send the playlist to the user
		out.println(playlist.toString());

		// Server asking for a client action: add,remove or exit
		String action = null;
		String film = null;
		String response = null;

		do {
			out.println("add,remove or exit?: ");
			try {
				action = scanner.nextLine();
			} catch (NoSuchElementException e) {
				action = null;
				break;
			}

			// if is add or remove, the server request a film.
			if (action.equals("add") || action.equals("remove")) {
				
				// Server asking for a film
				out.println("which film?: ");
				
				// Server receiving a film
				try {
					film = scanner.nextLine();
				} catch (NoSuchElementException e) {
					film = null;
				}
			}

			// Processing action
			switch (action) {
			case "exit":
				response = "Exiting...";
				break;

			case "add":
				playlist.add(film);
				response = "Film " + film + " added successfully";
				break;

			case "remove":
				if (!(playlist.remove(film))) {
					response = "Cannot remove: film " + film + " is not included in playlist";
					failed = EMPTY_ERROR;
				} else {
					response = "Film " + film + " removed successfully";
				}
				break;

			default:
				response = "'" + action + "' is not a valid action: use [add], [remove] or [exit]";
				failed = ACTION_ERROR;
			}
			out.println(response);
			
		} while (!(action.equals("exit")));

		// Waiting random time between MINMILIS and MAXMILIS

		int time = (int) (Math.random() * MAXMILIS + MINMILIS);
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Send the updated playlist to the user
		out.println(playlist.toString());

		// Closing I/O resources and client socket
		System.out.println("Closing I/O resources...");
		out.close();
		scanner.close();
		try {
			clientSocket.close();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// We return the task status to the server
		switch (failed) {
			case ACTION_ERROR:
				return "User: " + user + ", tried invalid comand";
			
			case EMPTY_ERROR:
				return "User: " + user + ", tried to remove film not included in playlist";
				
			default:
				return "User: " + user + " processed successfully";
			}
	}
}
 		