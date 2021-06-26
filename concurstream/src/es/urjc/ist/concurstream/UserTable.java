package es.urjc.ist.concurstream;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The UserTable class develop a default table implemented with a Concurrent Hash Map to ensure concurrent compatibility.
 * In this user table we storage a playlist related to each user.
 * This table have an initial configuration that we can change adding users in client connections.
 * 
 * @author César Borao
 * 
 * @version	2.0
 *
 */
public class UserTable {
	
	// We create the initial parameters. The practice specifications ensure us that we have only two concurrent playlists.
	private ConcurrentHashMap<String,ConcurrentPlaylist> users;
	private ConcurrentPlaylist defaultPlaylist;
	
	/**
	 * Default constructor with an initial configuration
	 * 
	 */
	public UserTable() {
		users = new ConcurrentHashMap<String,ConcurrentPlaylist>();
		defaultPlaylist = new ConcurrentPlaylist();
	}

	/** 
	 * Getter method to get the user HashMap
	 * 
	 * @return the users
	 */
	public ConcurrentHashMap<String, ConcurrentPlaylist> getUsers() {
		return users;
	}
	
	/**
	 * Setter method to set the user list
	 * 
	 * @param users the users to set
	 */
	public void setUsers(ConcurrentHashMap<String, ConcurrentPlaylist> users) {
		this.users = users;
	}

	/**
	 * Getter method to get the default list
	 * 
	 * @return the defaultPlaylist
	 */
	public ConcurrentPlaylist getDefaultPlaylist() {
		return defaultPlaylist;
	}

	/**
	 * Setter method to set the default list
	 * 
	 * @param defaultPlaylist the defaultPlaylist to set
	 */
	public void setDefaultPlaylist(ConcurrentPlaylist defaultPlaylist) {
		this.defaultPlaylist = defaultPlaylist;
	}

	/**
	 * A String representation of the user table
	 * 
	 * @return String with the user table contents
	 */
	@Override
	public String toString() {
		return "UserTable [users=" + users + ", defaultPlaylist=" + defaultPlaylist + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(defaultPlaylist, users);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof UserTable))
			return false;
		UserTable other = (UserTable) obj;
		return Objects.equals(defaultPlaylist, other.defaultPlaylist) && Objects.equals(users, other.users);
	}
}
