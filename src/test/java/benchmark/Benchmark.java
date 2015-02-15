package benchmark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.arabidopsis.ahocorasick.AhoCorasick;

public class Benchmark {
    public static void main(String[] args) throws IOException, InterruptedException {
        String[] words = {
                "Christmas", "Cains", "Marley", "spectre", "Ebenezer", "double-ironed", "supernatural", "SPIRITS",
                "Ding", "Ali Baba"
        };

        long t0 = System.currentTimeMillis();

        BufferedReader fr = new BufferedReader(
                new InputStreamReader(Benchmark.class.getResourceAsStream("christmas.txt")));
        String text = "";
        String line = fr.readLine();
        while (line != null) {
            text += line + "\n";
            line = fr.readLine();
        }

        System.out.println("Starting benchmark");
        long t1 = System.currentTimeMillis();

        AhoCorasick finder = new AhoCorasick();
        for (String word : words)
            finder.add(word, word);
        finder.prepare();

        Iterator it = finder.search(text.toCharArray());
        while (it.hasNext())
            it.next();

        long t2 = System.currentTimeMillis();

        String pattern = "";
        for (String word : words) {
            if (!pattern.equals(""))
                pattern += "|";
            pattern += word;
        }
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        while (m.find())
            continue;

        long t3 = System.currentTimeMillis();

        System.out.println("File reading: " + Long.toString(t1 - t0) + "ms");
        System.out.println("Aho-Corasick: " + Long.toString(t2 - t1) + "ms");
        System.out.println("Java-regexp: " + Long.toString(t3 - t2) + "ms");
    }
}
