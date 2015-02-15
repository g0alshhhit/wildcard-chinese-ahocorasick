package org.arabidopsis.ahocorasick;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.arabidopsis.ahocorasick.wildcard.WildcardAhoCorasickTree;
import org.junit.Test;

import com.google.common.io.Files;


public class TestUnicode {
    @Test
    public void testUnicode() {
        AhoCorasick tree = new AhoCorasick();

        tree.add("hsro", "hsro");
        tree.add("sra", "sra");
        tree.add("马超", "马超");

        tree.prepare();

        Iterator<SearchResult> it = tree.search("马刺今年必须亚军！=1".toCharArray());
        while (it.hasNext()) {
            SearchResult sr = it.next();
            System.out.println(sr.getLastIndex());
            System.out.println(new String(sr.chars).substring(sr.getLastIndex()));

            Set<String> outputs = sr.getOutputs();

            // for(Object s : outputs)
            // System.out.println(new String((char[]) s));
        }
    }

    @Test
    public void testUnicode2() throws IOException {

        Set<String> dict = getStopWord();
        long date1 = System.currentTimeMillis();
        printMem();
        AhoCorasick tree = new AhoCorasick();
        for (String e : dict) {
            tree.add(e, e);
        }
        tree.prepare();
        printMem();

        List<String> tests = loadTest();
        long date2 = System.currentTimeMillis();

        int i = 0;
        for (int j = 0; j < 10; j++) {

            for (String t : tests) {
                Iterator<SearchResult> iter = tree.search(t.toCharArray());
                if (iter.hasNext()) {
                    i++;
                    // String tmp = iter.next().getOutputs().iterator().next();
                    // System.out.println(tmp + ":" + t);
                }
            }
        }

        long date3 = System.currentTimeMillis();
        System.out.println("ac建模用时:" + (date2 - date1));
        System.out.println("ac测试用时:" + (date3 - date2));
        System.out.println(i);
    }

    @Test
    public void testsearchOne() throws Exception {
        Set<String> dict = getStopWord();
        long date1 = System.currentTimeMillis();
        printMem();
        WildcardAhoCorasickTree tree = new WildcardAhoCorasickTree(dict);
        printMem();
        List<String> tests = loadTest();
        System.out.println(tests.size());
        long date2 = System.currentTimeMillis();

        // Thread.sleep(10000);
        int i = 0;
        for (int j = 0; j < 10; j++) {
            for (String t : tests) {
                String rs = tree.searchOne(t);
                if (rs != null)
                    i++;
                // System.out.println(t);
            }
        }

        long date3 = System.currentTimeMillis();
        System.out.println("wcac建模用时:" + (date2 - date1));

        System.out.println("wcac测试用时:" + (date3 - date2));


        System.out.println(i);

    }

    private static List<String> loadTest() throws IOException {
        return Files.readLines(new File("E:\\commentForTest.txt"), Charset.forName("utf8"));
    }


    private static Set<String> getStopWord() throws IOException {
        Set<String> ret = new HashSet<String>(Files.readLines(new File("E:\\wsgs6.txt"), Charset.forName("utf8")));
        return ret;
    }

    private static void printMem() {
        System.gc();
        Runtime rt = Runtime.getRuntime();
        System.out.println("Total Memory= " + rt.totalMemory() +
                " Free Memory = " + rt.freeMemory() + " Used　Memory=" + (rt.totalMemory() - rt.freeMemory()));
    }

    public static void main(String[] args) throws Exception {
        TestUnicode t = new TestUnicode();
        t.testsearchOne();
    }

}
