package de.mhus.lib.test;

import java.io.File;

import de.mhus.lib.core.MPassword;
import de.mhus.lib.core.util.MUri;
import junit.framework.TestCase;

public class MUriTest extends TestCase {

	public void testParser() {
		{ // full
			MUri uri = MUri.toUri("http://user:pass@domain.com/path1/path2;param1;param2?q1=abc&q2=def#fragment");
			assertEquals("http", uri.getScheme());
			assertEquals("user", uri.getUsername());
			assertEquals("pass", MPassword.decode(uri.getPassword()));
			assertEquals("domain.com", uri.getLocation());
			assertEquals(2, uri.getPathParts().length);
			assertEquals("path1", uri.getPathParts()[0]);
			assertEquals("path2", uri.getPathParts()[1]);
			assertEquals("param1", uri.getParams()[0]);
			assertEquals("param2", uri.getParams()[1]);
			assertEquals(2,uri.getQuery().size());
			assertEquals("abc", uri.getQuery().get("q1"));
			assertEquals("def", uri.getQuery().get("q2"));
			assertEquals("fragment", uri.getFragment());
		}

		{ 
			MUri uri = MUri.toUri("http://user:pass@domain.com/path1/path2;param1;param2?q1=abc&q2=def");
			assertEquals("http", uri.getScheme());
			assertEquals("user", uri.getUsername());
			assertEquals("pass", MPassword.decode(uri.getPassword()));
			assertEquals("domain.com", uri.getLocation());
			assertEquals(2, uri.getPathParts().length);
			assertEquals("path1", uri.getPathParts()[0]);
			assertEquals("path2", uri.getPathParts()[1]);
			assertEquals("param1", uri.getParams()[0]);
			assertEquals("param2", uri.getParams()[1]);
			assertEquals(2,uri.getQuery().size());
			assertEquals("abc", uri.getQuery().get("q1"));
			assertEquals("def", uri.getQuery().get("q2"));
			assertNull( uri.getFragment());
		}

		{ 
			MUri uri = MUri.toUri("http://user:pass@domain.com/path1/path2;param1;param2#fragment");
			assertEquals("http", uri.getScheme());
			assertEquals("user", uri.getUsername());
			assertEquals("pass", MPassword.decode(uri.getPassword()));
			assertEquals("domain.com", uri.getLocation());
			assertEquals(2, uri.getPathParts().length);
			assertEquals("path1", uri.getPathParts()[0]);
			assertEquals("path2", uri.getPathParts()[1]);
			assertEquals("param1", uri.getParams()[0]);
			assertEquals("param2", uri.getParams()[1]);
			assertNull(uri.getQuery());
			assertEquals("fragment", uri.getFragment());
		}
		{ 
			MUri uri = MUri.toUri("http://user:pass@domain.com/path1/path2?q1=abc&q2=def#fragment");
			assertEquals("http", uri.getScheme());
			assertEquals("user", uri.getUsername());
			assertEquals("pass", MPassword.decode(uri.getPassword()));
			assertEquals("domain.com", uri.getLocation());
			assertEquals(2, uri.getPathParts().length);
			assertEquals("path1", uri.getPathParts()[0]);
			assertEquals("path2", uri.getPathParts()[1]);
			assertNull(uri.getParams());
			assertEquals(2,uri.getQuery().size());
			assertEquals("abc", uri.getQuery().get("q1"));
			assertEquals("def", uri.getQuery().get("q2"));
			assertEquals("fragment", uri.getFragment());
		}
		{ // full
			MUri uri = MUri.toUri("http://user@domain.com/path1/path2;param1;param2?q1=abc&q2=def#fragment");
			assertEquals("http", uri.getScheme());
			assertEquals("user", uri.getUsername());
			assertNull(uri.getPassword());
			assertEquals("domain.com", uri.getLocation());
			assertEquals(2, uri.getPathParts().length);
			assertEquals("path1", uri.getPathParts()[0]);
			assertEquals("path2", uri.getPathParts()[1]);
			assertEquals("param1", uri.getParams()[0]);
			assertEquals("param2", uri.getParams()[1]);
			assertEquals(2,uri.getQuery().size());
			assertEquals("abc", uri.getQuery().get("q1"));
			assertEquals("def", uri.getQuery().get("q2"));
			assertEquals("fragment", uri.getFragment());
		}
		{ // full
			MUri uri = MUri.toUri("http://domain.com/path1/path2;param1;param2?q1=abc&q2=def#fragment");
			assertEquals("http", uri.getScheme());
			assertNull(uri.getUsername());
			assertNull(uri.getPassword());
			assertEquals("domain.com", uri.getLocation());
			assertEquals(2, uri.getPathParts().length);
			assertEquals("path1", uri.getPathParts()[0]);
			assertEquals("path2", uri.getPathParts()[1]);
			assertEquals("param1", uri.getParams()[0]);
			assertEquals("param2", uri.getParams()[1]);
			assertEquals(2,uri.getQuery().size());
			assertEquals("abc", uri.getQuery().get("q1"));
			assertEquals("def", uri.getQuery().get("q2"));
			assertEquals("fragment", uri.getFragment());
		}
		{ // full
			MUri uri = MUri.toUri("//user:pass@domain.com/path1/path2;param1;param2?q1=abc&q2=def#fragment");
			assertNull(uri.getScheme());
			assertEquals("user", uri.getUsername());
			assertEquals("pass", MPassword.decode(uri.getPassword()));
			assertEquals("domain.com", uri.getLocation());
			assertEquals(2, uri.getPathParts().length);
			assertEquals("path1", uri.getPathParts()[0]);
			assertEquals("path2", uri.getPathParts()[1]);
			assertEquals("param1", uri.getParams()[0]);
			assertEquals("param2", uri.getParams()[1]);
			assertEquals(2,uri.getQuery().size());
			assertEquals("abc", uri.getQuery().get("q1"));
			assertEquals("def", uri.getQuery().get("q2"));
			assertEquals("fragment", uri.getFragment());
		}

		{ // full
			MUri uri = MUri.toUri("http://domain.com/path1/path2?q1=abc&q2=def");
			assertEquals("http", uri.getScheme());
			assertNull(uri.getUsername());
			assertNull(uri.getPassword());
			assertEquals("domain.com", uri.getLocation());
			assertEquals(2, uri.getPathParts().length);
			assertEquals("path1", uri.getPathParts()[0]);
			assertEquals("path2", uri.getPathParts()[1]);
			assertNull(uri.getParams());
			assertEquals(2,uri.getQuery().size());
			assertEquals("abc", uri.getQuery().get("q1"));
			assertEquals("def", uri.getQuery().get("q2"));
			assertNull(uri.getFragment());
		}

	}
	
	public void testFile() {
		{
			File f = new File("/demo/demo.txt");
			MUri uri = MUri.toUri(f);
			assertEquals("file", uri.getScheme());
			assertEquals(f.getAbsolutePath(), uri.getPath());
		}
		{
			MUri uri = MUri.toUri("file:C:/demo/demo.txt");
			assertEquals("file", uri.getScheme());
			assertEquals("C:/demo/demo.txt", uri.getPath());
		}
		{
			MUri uri = MUri.toUri("file:C:\\demo\\demo.txt");
			assertEquals("file", uri.getScheme());
			assertEquals("C:/demo/demo.txt", uri.getPath());
		}
	}
	
	public void testSpecial() {
		{
			MUri uri = MUri.toUri("xdb:adb/test/local");
			assertEquals("xdb", uri.getScheme());
			assertEquals("adb", uri.getPathParts()[0]);
			assertEquals("test", uri.getPathParts()[1]);
			assertEquals("local", uri.getPathParts()[2]);
		}
	}
	
	
	
	
}