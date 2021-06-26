package es.urjc.ist.concurstream;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * PlayListServer implements a server that allow clients to access a playlist.
 * This server will run all the client's connections concurrently over a thread pool.
 * 
 * @author César Borao
 * 
 * @version 2.0
 *
 */
public class PlayListServer {
	
	private static final int NCLIENTES = 5;
	
	private int port; // Port to bind
	private ServerSocket serverSocket; // Server Socket
	private ExecutorService pool; // Thread pool to run concurrent threads
	private List<Future<String>> resultList; // List to save the threads's execution results
	
	/**
	 * PlaylistServer is a constructor to run a Playlist Server. 
	 * 
	 * @param port Port where the server is listening
	 * @param nThreads Thread pool size, the maximum number of threads the pool can run.
	 * 
	 */
	public PlayListServer(int port, int nThreads) {
		this.port = port;
		serverSocket = null;
        pool = Executors.newFixedThreadPool(nThreads);
	}
	
	/**
	 * Getter method to get the listening port
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/** Setter method to set the listening port
	 * 
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/** Getter method to access the threads result list
	 * 
	 * @return the resultList
	 */
	public List<Future<String>> getResultList() {
		return resultList;
	}

	/**
	 * Bind the server socket to the given port
	 * 
	 * @throws IOException
	 */
	public void bindServer() throws IOException {
		this.serverSocket = new ServerSocket(port);
	}
	
	/**
	 * This method serve clients concurrently.
	 * The clients are handled by ClientHandler tasks, executing concurrently in the threadpool
	 * 
	 * When all the clients have been processed, this method close the thread pool and
	 * wait for all the tasks until finish.
	 * 
	 * @param nCli maximum number of clients that the server can process before finalize.
	 */
	public void handlingClients(int nCli, UserTable userTable) {
		
		// Initialize the result list
		resultList = new ArrayList<Future<String>>(nCli);
		
        for (int count = 0; count < nCli; count++) {
        	System.err.println("Waiting new client...");
            
        	Socket clientSocket = null;
        	try {
        		clientSocket = serverSocket.accept();
        	} catch (IOException ex) {
            	ex.printStackTrace();
            }
            System.err.println("Accepted new client connection, processing...");
            
            // We create a new ClientHandler task to process the client
            ClientHandler handler = new ClientHandler(clientSocket, userTable);
            
            // We run a pool thread with the task and add the result to the result list
            resultList.add(this.pool.submit(handler)); 
        }
	}
	
	/**
	 * Method to close the server
	 * 
	 * @throws IOException
	 */
	public void closeServer() throws IOException {
		serverSocket.close();
	}
	
	/**
	 * Method to close the ExecutorService in two stages: first avoiding running new
	 * tasks in the pool and after, requesting the tasks running to finish.
	 * 
	 * @param firstTimeout Timeout to the first waiting stage.
	 * @param secondTimeout Timeout to the second waiting stage.
	 */
	public void shutdownAndAwaitTermination(int firstTimeout, int secondTimeout) {
		pool.shutdown(); 
		try {
		
			if (!pool.awaitTermination(firstTimeout, TimeUnit.SECONDS)) {
				System.err.println("No han terminado la tareas. Forzando cierre...");
				pool.shutdownNow(); 
				if (!pool.awaitTermination(secondTimeout, TimeUnit.SECONDS))
					System.err.println("El pool no terminó.");
			}
		} catch (InterruptedException ie) {
			pool.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	/*
	 *  Main method
	 */
	public static void main(String[] args) {
		
		// We create the server object
		PlayListServer server = new PlayListServer(19000, NCLIENTES);

        // We bind the server to localhost:port
        try {
        	server.bindServer();
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        System.err.println("Running server in port " + server.getPort());
        
        // We create the default userTable
        UserTable userTable = new UserTable();
        
        ConcurrentPlaylist playlist0 = new ConcurrentPlaylist();
        ConcurrentPlaylist playlist1 = new ConcurrentPlaylist();
        
        ConcurrentHashMap<String, ConcurrentPlaylist> users = new ConcurrentHashMap<String, ConcurrentPlaylist>();
        users.put("Bruce Wayne", playlist0);
		users.put("Clark Kent", playlist0);
		users.put("Diana Prince", playlist0);
		users.put("Peter Parker", playlist1);
		users.put("Steve Rogers", playlist1);
		
		userTable.setUsers(users);
		userTable.setDefaultPlaylist(playlist1);
		
		
        server.handlingClients(NCLIENTES, userTable);
        
        // We have to ensure that all the threads are finished before ending the program.
        
        System.err.println("Waiting for the finish of all the client threads...");
        
        server.shutdownAndAwaitTermination(60, 60);
        
        // Closing server
        System.err.println("We have processed " + NCLIENTES + "client, ending...");
        System.err.println("closing server socket...");
        try {
        	server.closeServer();
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        
        for (Future<String> task : server.resultList) {
        	if(task.isDone()) {
        		try {
        			System.out.println("Result: " + task.get());
        		} catch (InterruptedException | ExecutionException ex) {
        			System.out.print("Thread interrupted or" +
        		                     " execution failed");
        			ex.printStackTrace();
        		}
        	} else {
        		System.out.println("unfinished task");
        	}
        }  
        System.err.println("closing server");
	}
}
