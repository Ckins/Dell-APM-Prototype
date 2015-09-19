package com.dell.prototype.apm.base;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import com.dell.prototype.apm.model.base.MaxCapacityList;

public class MaxCapacityListTest {

	@Test
	public void cannotAddElementToZeroCapacityList() {
		MaxCapacityList<String> list = new MaxCapacityList<String>(0);
		assertFalse(list.add("element"));
		assertEquals(0, list.size());
		
		list = new MaxCapacityList<String>(0);
		assertFalse(list.addAll(Arrays.asList("element")));
		assertEquals(0, list.size());
	}
	
	
	@Test
	public void addElementSuccessWhenSizeIsSmallThanCapacity() {
		MaxCapacityList<String> list = new MaxCapacityList<String>(10);
		assertTrue(list.add("element"));
		assertEquals(1, list.size());
		assertEquals("element", list.get(0));
		
		list = new MaxCapacityList<String>(10);
		assertTrue(list.addAll(Arrays.asList("element")));
		assertEquals(1, list.size());
	}
	
	@Test
	public void removeFirstElementWhenSizeReachesCapacity() {
		MaxCapacityList<String> list = new MaxCapacityList<String>(2);
		list.addAll(Arrays.asList("element1","element2"));
		assertTrue(list.add("element3"));
		assertEquals(2, list.size());
		assertEquals("element2", list.get(0));
		assertEquals("element3", list.get(1));
		
		list = new MaxCapacityList<String>(2);
		list.addAll(Arrays.asList("element1","element2"));
		assertTrue(list.addAll(Arrays.asList("element3","element4")));
		assertEquals(2, list.size());
		assertEquals("element3", list.get(0));
		assertEquals("element4", list.get(1));
	}
}
