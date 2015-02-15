package org.arabidopsis.ahocorasick;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.arabidopsis.ahocorasick.wildcard.WildcardAhoCorasickTree;
import org.junit.Test;

import com.google.common.io.Files;


public class TestRight {
    @Test
    public void testsearchAll() throws Exception {
        Set<String> dict = getStopWord();

        WildcardAhoCorasickTree tree = new WildcardAhoCorasickTree(dict);
        List<String> tests = loadTest();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("E://testRight_ac.txt"))) {
            for (String t : tests) {
                TreeSet<String> rs = new TreeSet<>(tree.searchAll(t));
                if (!rs.isEmpty())
                    bw.write(t + "\n");
            }
            bw.close();
        }


    }

    private static List<String> loadTest() throws IOException {
        return Files.readLines(new File("E:\\commentForTest.txt"), Charset.forName("utf8"));
    }


    private static Set<String> getStopWord() throws IOException {
        Set<String> ret = new HashSet<String>(Files.readLines(new File("E:\\wsgs3.txt"), Charset.forName("utf8")));
        return ret;
    }

    private static void printMem() {
        System.gc();
        Runtime rt = Runtime.getRuntime();
        System.out.println("Total Memory= " + rt.totalMemory() +
                " Free Memory = " + rt.freeMemory() + " Used　Memory=" + (rt.totalMemory() - rt.freeMemory()));
    }

    public static void main(String[] args) throws Exception {
        TestRight t = new TestRight();
        t.testsearchAll();
    }

}
