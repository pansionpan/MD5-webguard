/**
 *  Copyright(C) 2015 Suntec Software(Shanghai) Co., Ltd.
 *  All Right Reserved.
 */
package com.iauto.tools.webguard.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * Descriptions
 *
 * @version 2019年3月21日
 * @author SUNTEC
 * @since JDK1.7
 *
 */
public class Md5ResourceMonitorManagerTest {

	/**
	 * @throws java.lang.Exception
	 */

	Md5ResourceMonitorManager mm = new Md5ResourceMonitorManager();

	private String f1 = "src\\test\\resources\\file1.txt";
	private String f2 = "src\\test\\resources\\file2.txt";

	@Before
	public void setUp() throws Exception {
		new File(f1).createNewFile();
		new File(f2).createNewFile();
		FileWriter fw = new FileWriter(f1, false);
		fw.write("this is file1 by zyhy.\r\n");
		fw.close();
		mm.addResource(f1);
		mm.save(f2);
	}
	
	@AfterClass
	public static void del() {
		new File("WrongFile.conf").delete();
	}
	
	@Test
	public void test() {

	}

	@Test
	public void testAddResource() throws Exception {
		mm.getMap().clear();
		mm.addResource(f1);
		assertTrue(mm.getMap().containsKey(f1));
		String md5 = mm.getMap().get(f1);
		mm.getMap().clear();
		mm.addResource(f1);
		assertEquals(md5, mm.getMap().get(f1));
	}

	@Test
	public void testAddResourceWithEmpty() throws Exception {
		mm.getMap().clear();
		mm.addResource(null);
		assertTrue(mm.getMap().size() == 0);

	}

	@Test
	public void testSave() throws Exception {
		mm.getMap().clear();
		mm.addResource(f1);
		mm.save(f2);
	}

	@Test(expected = Exception.class)
	public void testSaveWithEmpty() throws Exception {
		try {
			mm.save(null);
			assertTrue("should not to here", false);
		}
		catch (Exception e) {
			assertEquals("save file is null or empty", e.getMessage());
			throw (e);
		}

		try {
			mm.save("");
			assertTrue("should not to here", false);
		}
		catch (Exception e) {
			assertEquals("save file is null or empty", e.getMessage());
			throw (e);
		}
	}

	@Test
	public void testSaveWithSamecontent() throws Exception {
		mm.getMap().clear();
		mm.addResource(f1);
		String md5 = mm.getMap().get(f1);
		String fm = md5 + " : " + f1;
		mm.save(f2);
		InputStream is;
		String line = "";
		is = new FileInputStream(f2);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		line = reader.readLine();
		reader.close();
		is.close();
		assertEquals(fm, line);
	}

	@Test
	public void testLoad() throws Exception {
		mm.getMap().clear();
		mm.load(f2);
	}

	@Test(expected = Exception.class)
	public void testLoadWithEmpty() throws Exception {

		try {
			mm.load(null);
		}
		catch (Exception e) {
			assertEquals("load file is null", e.getMessage());
			throw (e);
		}
	}

	@Test
	public void testLoadWithSameContent() throws Exception {
		mm.getMap().clear();
		mm.addResource(f1);
		mm.save(f2);

		String fm = mm.getMap().get(f1) + " : " + f1;

		mm.getMap().clear();
		mm.load(f2);
		assertTrue(mm.getMap().containsKey(f1));

		String lf = mm.getMap().get(f1) + " : " + f1;

		assertEquals(fm, lf);
	}

	@Test(expected = Exception.class)
	public void testLoadWithSameContentNoSpacer() throws Exception {
		mm.getMap().clear();
		mm.addResource(f1);
		FileWriter fw = new FileWriter(f2, false);
		fw.write(mm.getMap().get(f1) + f1);
		fw.close();
		mm.getMap().clear();
		mm.load(f2);

	}

	@Test
	public void testLoadWithSameContentNullMd5() throws Exception {
		mm.getMap().clear();
		mm.addResource(f1);
		String s = null;
		FileWriter fw = new FileWriter(f2, false);
		fw.write(s + " : " + f1);
		fw.close();
		mm.load(f2);
		assertEquals(mm.getMap().get(f1), "null");

	}

	@Test
	public void testLoadWithSameContentNullFile() throws Exception {
		mm.getMap().clear();
		mm.addResource(f1);
		String s = null;
		String m = mm.getMap().get(f1);
		FileWriter fw = new FileWriter(f2, false);
		fw.write(mm.getMap().get(f1) + " : " + s);
		fw.close();
		mm.getMap().clear();
		mm.load(f2);
		assertEquals(mm.getMap().get("null"), m);
	}

	@Test
	public void testLoadWithSameContentMoreSpacerFront() throws Exception {
		mm.getMap().clear();
		mm.addResource(f1);
		FileWriter fw = new FileWriter(f2, false);
		fw.write(" : " + mm.getMap().get(f1) + " : " + f1);
		fw.close();
		mm.getMap().clear();
		mm.load(f2);
		assertFalse(mm.getMap().containsKey(f1));
	}

	@Test
	public void testLoadWithSameContentMoreSpacerBehind() throws Exception {
		mm.getMap().clear();
		mm.addResource(f1);
		FileWriter fw = new FileWriter(f2, false);
		fw.write(mm.getMap().get(f1) + " : " + f1 + " : ");
		fw.close();
		mm.getMap().clear();
		mm.load(f2);
		assertFalse(mm.getMap().containsKey(f1));

	}

	@Test
	public void testCheckResource() throws Exception {
		mm.getMap().clear();
		mm.load(f2);
		assertTrue(mm.checkResource(f1));
	}

	@Test
	public void testCheckResourceWithEmptyResource() throws Exception {
		assertTrue(mm.checkResource(""));
	}

	@Test
	public void testCheckResourceWithMapNocontainsKey() throws Exception {
		assertFalse(mm.checkResource("abcde.abcde"));
	}

	@Test
	public void testCheckResourceWithResourceisNull() throws Exception {
		assertTrue(mm.checkResource(null));
	}

	@Test
	public void testCheckResourceWithMD5NotEquals() throws Exception {
		mm.getMap().clear();
		mm.load(f2);
		FileWriter fw = new FileWriter(f1, false);
		fw.write("this is file1 by zyhy.\r\n");
		fw.write("this is file2 by zyhy.\r\n");
		fw.write("this is file3 by zyhy.\r\n");
		fw.close();
		assertFalse(mm.checkResource(f1));
	}

	@Test
	public void testCheckResources() throws Exception {
		mm.getMap().clear();
		mm.load(f2);
		List<String> testpath = new ArrayList<String>();
		testpath.add(f1);
		assertTrue(mm.checkResources(testpath));
	}

	@Test
	public void testCheckResourcesWithListsizeLessthanMapsize() throws Exception {
		mm.getMap().clear();
		mm.load(f2);
		List<String> testpath = new ArrayList<String>();
		assertFalse(mm.checkResources(testpath));
	}

	@Test
	public void testCheckResourcesWithListsizeequalsMapsize() throws Exception {
		mm.getMap().clear();
		mm.load(f2);
		List<String> testpath = new ArrayList<String>();
		testpath.add(f1);
		assertTrue(mm.checkResources(testpath));
	}

	@Test
	public void testCheckResourcesWithListsizeMorethanMapsize() throws Exception {
		mm.getMap().clear();
		mm.load(f2);
		List<String> testpath = new ArrayList<String>();
		testpath.add(f1);
		testpath.add(f2);
		assertFalse(mm.checkResources(testpath));
	}

	@Test
	public void testCheckResourcesWithCheckResourceisfalse() throws Exception {
		mm.getMap().clear();
		mm.load(f2);
		List<String> testpath = new ArrayList<String>();
		testpath.add("abcde.abcde");
		assertFalse(mm.checkResources(testpath));
	}
}
