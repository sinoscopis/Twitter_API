package com.twitter.jdbc.main;
import java.util.Map;

import com.twitter.jdbc.cache.LRUCache;

// Test program for the LRUCache class.
public class TestLRUCache {

public static void main (String[] args) {
   LRUCache<String,String> cache = new LRUCache<String, String>(3);// <-- Cache size
   cache.put ("1", "one");                           // 1
   cache.put ("2", "two");                           // 2 1
   cache.put ("3", "three");                         // 3 2 1
   													 //  LRU        FIFO
   													 // -------------------
   cache.put ("4", "four");                          // 4 3 2   o   4 3 2
   if (cache.get("2") == null) throw new Error();    // 2 4 3   o   4 3 2
   cache.put ("4", "update1");						 // 4 2 3   o   4 3 2     
   cache.put ("5", "five");                          // 5 4 2   o   5 4 3
   cache.put ("4", "update2");                       // 4 5 2   o   5 4 3
   // Verify cache content.
   if (cache.usedEntries() != 3)              throw new Error();
   // List cache content.
   for (Map.Entry<String, String> e : cache.getAll())
      System.out.println (e.getKey() + " : " + e.getValue()); }

	/*public static void show_list (LRUCache cache){
		for (Map.Entry<String, String> e : cache.getAll())
		      System.out.println (e.getKey() + " : " + e.getValue());
	   System.out.println ("-----");
	}*/
}
