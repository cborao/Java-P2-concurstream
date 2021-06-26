package es.urjc.ist.concurstream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The ConcurrentPlaylist class implements a simple concurrent playlist.
 * The methods add and remove allow to add or remove content concurrently to this playlist
 * 
 * @author César Borao
 * 
 * @version	3.0
 *
 */
public class ConcurrentPlaylist {
	
	private List<String> playlist; 
	
	/**
	 * Default constructor 
	 */
	public ConcurrentPlaylist() {
		playlist = Collections.synchronizedList(new ArrayList<String>());
	}
	
	/**
	 * This method return the playlist 
	 * 
	 * @return the list with the content
	 */
	public List<String> getPlaylist() {
		return playlist;
	}

	/**
	 * This method set a new playlist 
	 * 
	 * @param list the list to set
	 */
	public void setPlaylist(List<String> list) {
		this.playlist = list;
	}
	
	/**
	 * This method is used to add content concurrently to the playlist.
	 * It is a synchronized method to ensure thread-safeness.
	 * 
	 * @param film The film to add to the playlist
	 */
	public void add(String film) {
		playlist.add(film);
	}
	
	/**
	 *  This method is used to remove content concurrently in the playlist.
	 *  It is a synchronized method to ensure thread-safeness.
	 *  
	 * @param film to remove
	 * 
	 * @return true if success or false if remove failed
	 */
	public boolean remove(String film) {
		if(playlist.remove(film)) return true;
		
		return false;
	}
	
	
	@Override
	public String toString() {
		return "Playlist: " + playlist.toString();
	}

	/**
	 * Calculate the hashCode for this object
	 * 
	 * @return integer with the hashCode
	 */
	@Override
	public int hashCode() {
		return Objects.hash(playlist);
	}

	/**
	 * Compares this object against the specified object
	 * 
	 * @param obj the other object to compare
	 * 
	 * @return boolean if are equals or not
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ConcurrentPlaylist))
			return false;
		ConcurrentPlaylist other = (ConcurrentPlaylist) obj;
		return Objects.equals(playlist, other.playlist);
	}
	
}
