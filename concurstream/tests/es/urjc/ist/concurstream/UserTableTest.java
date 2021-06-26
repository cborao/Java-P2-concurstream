package es.urjc.ist.concurstream;

import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Test;

class UserTableTest {

	@Test
	void testHashCode() {
		UserTable u1 = new UserTable();
		UserTable u2 = new UserTable();
		
		UserTable u3 = new UserTable();
		ConcurrentPlaylist play = new ConcurrentPlaylist();
		u3.getUsers().put("user", play);
		
		assertEquals(u1.hashCode(), u2.hashCode());
		assertNotEquals(u1.hashCode(), u3.hashCode());
	}

	@Test
	void testUserTable() {
		UserTable u1 = new UserTable();
		assertNotNull(u1);
	}

	@Test
	void testGetUsers() {
		UserTable u1 = new UserTable();
		ConcurrentPlaylist play = new ConcurrentPlaylist();
		u1.getUsers().put("user", play);
		
		assertEquals(u1.getUsers().get("user"),play);
	}

	@Test
	void testToString() {
		UserTable t1 = new UserTable();
		String expected = "UserTable [users={testing=Playlist: []}, defaultPlaylist=Playlist: []]";
	
		ConcurrentHashMap<String, ConcurrentPlaylist> users = new ConcurrentHashMap<String, ConcurrentPlaylist>();
		users.put("testing", new ConcurrentPlaylist());
		t1.setUsers(users);
		
		assertEquals(expected,t1.toString());
	}

	@Test
	void testEqualsObject() {
		UserTable t1 = new UserTable();
		UserTable t2 = new UserTable();
		
		assertEquals(t1, t2);
	}
}
