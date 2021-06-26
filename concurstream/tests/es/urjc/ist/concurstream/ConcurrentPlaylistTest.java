package es.urjc.ist.concurstream;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class ConcurrentPlaylistTest {

	@Test
	void testHashCode() {
		ConcurrentPlaylist c1 = new ConcurrentPlaylist();
		ConcurrentPlaylist c2 = new ConcurrentPlaylist();
		
		ConcurrentPlaylist c3 = new ConcurrentPlaylist();
		c3.add("testing");
		
		assertEquals(c1.hashCode(), c2.hashCode());
		assertNotEquals(c1.hashCode(), c3.hashCode());
	}

	@Test
	void testConcurrentPlaylist() {
		ConcurrentPlaylist c1 = new ConcurrentPlaylist();
		assertNotNull(c1);
	}

	@Test
	void testAdd() {
		ConcurrentPlaylist c1 = new ConcurrentPlaylist();
		c1.add("Film 1");
		
		assertEquals(c1.getPlaylist().get(0), "Film 1");
	}

	@Test
	void testRemove() {
		ConcurrentPlaylist c1 = new ConcurrentPlaylist();
		ConcurrentPlaylist c2 = new ConcurrentPlaylist();
		
		c1.add("Film");
		c2.add("Film");
		c1.remove("Film");
		
		assertNotEquals(c1.getPlaylist().size(), c2.getPlaylist().size());
	}
	
	@Test
	void testRemove2() {
		ConcurrentPlaylist c1 = new ConcurrentPlaylist();
		c1.add("Film 1");
		boolean failed = c1.remove("no exists");
		assertFalse(failed);
	}

	@Test
	void testToString() {
		ConcurrentPlaylist c1 = new ConcurrentPlaylist();
		String expected = "Playlist: []";
		assertEquals(expected,c1.toString());
	}

	@Test
	void testEqualsObject() {
		ConcurrentPlaylist c1 = new ConcurrentPlaylist();
		ConcurrentPlaylist c2 = new ConcurrentPlaylist();
		
		assertEquals(c1, c2);
	}
	
	@Test
	void testgetPlaylist() {
		ConcurrentPlaylist c1 = new ConcurrentPlaylist();
		assertNotEquals(c1.getPlaylist(),null);
	}
	
	@Test
	void testsetPlaylist() {
		ConcurrentPlaylist c1 = new ConcurrentPlaylist();
		List<String> newList = new ArrayList<String>();
		newList.add("film 1");
		
		c1.setPlaylist(newList);
		assertEquals(c1.getPlaylist(),newList);
	}
}

