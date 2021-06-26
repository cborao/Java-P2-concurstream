package es.urjc.ist.concurstream;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * This is the main method to create a user that will be able to insert or
 * delete content into a playlist. Running client program concurrently is
 * allowed.
 * 
 * @author César Borao
 *
 * @version 2.0
 */
public class Client {

	private static final String CHARSET_NAME = "UTF-8";

	public static void main(String[] args) throws Exception {

		// We establish the client parameters
		String host = "localhost";
		int port = 19000;
		Scanner scanner;
		PrintWriter out;

		// We create a client socket, binded to the same port as the server.
		Socket socket = new Socket(host, port);

		// IO Configuration
		Scanner stdin = new Scanner(new BufferedInputStream(System.in), CHARSET_NAME);
		try {
			InputStream is = socket.getInputStream();
			scanner = new Scanner(new BufferedInputStream(is), CHARSET_NAME);
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException ioe) {
			socket.close();
			stdin.close();
			throw new IllegalArgumentException("Impossible create " + socket, ioe);
		}

		System.err.println("Connected to server " + host + " at port " + port);

		// Communication with server

		// Waiting for a server message (requesting the user name)
		String line = null;
		String s;

		try {
			line = scanner.nextLine();
		} catch (NoSuchElementException e) {
			line = null;
		}
		System.out.println(line);

		// Reading the user name typed by the client
		try {
			s = stdin.nextLine();
		} catch (NoSuchElementException e) {
			s = null;
		}
		// Send the user name to the server
		out.println(s);

		// Waiting for a server message (list content)
		try {
			line = scanner.nextLine();
		} catch (NoSuchElementException e) {
			line = null;
		}
		System.out.println(line);

		// Waiting for a server message (action requested: add, remove or exit)
		do {
			try {
				line = scanner.nextLine();
			} catch (NoSuchElementException e) {
				line = null;
			}
			System.out.println(line);

			// Reading the action typed by the client
			try {
				s = stdin.nextLine();
			} catch (NoSuchElementException e) {
				s = null;
			}
			// Send the action to the server
			out.println(s);

			// if is command add or remove, server request a film title
			if (s.equals("add") || s.equals("remove")) {
				
				// Waiting for a server message (film title requested)
				try {
					line = scanner.nextLine();
				} catch (NoSuchElementException e) {
					line = null;
				}
				System.out.println(line);

				// Read and send the film title to the server
				try {
					s = stdin.nextLine();
				} catch (NoSuchElementException e) {
					s = null;
				}
				out.println(s);
			}

			// Waiting for a status server message: success or fail processing action
			try {
				line = scanner.nextLine();
			} catch (NoSuchElementException e) {
				line = null;
			}
			System.out.println(line);

		} while (!(s.equals("exit")));

		// Waiting for a server message (updated list)
		try {
			line = scanner.nextLine();
		} catch (NoSuchElementException e) {
			line = null;
		}
		System.out.println(line);

		// Closing IO streams and socket
		System.err.println("Closing connection to " + host);
		out.close();
		scanner.close();
		socket.close();
		stdin.close();
		System.err.println("Ending client program...");
	}
}
