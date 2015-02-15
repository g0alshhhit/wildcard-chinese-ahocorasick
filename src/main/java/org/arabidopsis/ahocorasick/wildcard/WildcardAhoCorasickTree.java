package org.arabidopsis.ahocorasick.wildcard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.arabidopsis.ahocorasick.AhoCorasick;
import org.arabidopsis.ahocorasick.SearchResult;
import org.arabidopsis.ahocorasick.wildcard.WildCardRule.WildCardRuleNode;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.SetMultimap;

/**
 * 类 WildcardAhoCorasick.java 的实现描述 TODO
 * 
 * @author wuyue
 * 
 * @date Feb 4, 2015 6:49:27 PM
 */
public class WildcardAhoCorasickTree {
    private final AhoCorasick _fragmentTree = new AhoCorasick();
    private final SetMultimap<String, WildCardRule> _frag2RuleMap = HashMultimap.create();
    private static final Logger logger = Logger.getLogger(WildcardAhoCorasickTree.class);

    public WildcardAhoCorasickTree(Set<String> inputs) {
        for (String input : inputs) {
            try {
                WildCardRule rule = new WildCardRule(input);
                for (String f : rule.getFragments()) {
                    _fragmentTree.add(f, f);
                    _frag2RuleMap.put(f, rule);
                }
            } catch (Exception e) {
                logger.error("wrong input:" + input, e);
            }
        }
        _fragmentTree.prepare();
        logger.info("prepare WildcardAhoCorasickTree finish!");
    }



    public Set<String> searchAll(String target) {

        Set<String> ret = new HashSet<String>();
        Iterator<SearchResult> searchIter = _fragmentTree.search(target.toCharArray());
        if (!searchIter.hasNext())
            return ret;

        // 获取所有备选规则集
        Set<WildCardRule> candiRules = new HashSet<>();
        ListMultimap<String, SearchResult> tmpSearchRsMap = ArrayListMultimap.create();

        while (searchIter.hasNext()) {
            SearchResult searchResult = searchIter.next();
            for (String tmpFrag : searchResult.getOutputs()) {
                candiRules.addAll(_frag2RuleMap.get(tmpFrag));
                tmpSearchRsMap.put(tmpFrag, searchResult);
            }
        }


        for (WildCardRule rule : candiRules) {
            if (!rule.isWildCard()) {
                ret.add(rule.getInput());
                continue;
            }
            if (!tmpSearchRsMap.keys().containsAll(rule.getFragments())) {
                continue;
            }
            if (checkRule(rule, tmpSearchRsMap)) {
                ret.add(rule.getInput());
                continue;
            }
        }

        return ret;

    }



    public String searchOne(String target) {

        Iterator<SearchResult> searchIter = _fragmentTree.search(target.toCharArray());
        if (!searchIter.hasNext())
            return null;

        // 获取所有备选规则集
        Set<WildCardRule> candiRules = new HashSet<>();
        ListMultimap<String, SearchResult> tmpSearchRsMap = null;

        while (searchIter.hasNext()) {
            SearchResult searchResult = searchIter.next();

            for (String tmpFrag : searchResult.getOutputs()) {
                for (WildCardRule rule : _frag2RuleMap.get(tmpFrag)) {
                    if (!rule.isWildCard())
                        return rule.getInput();
                    candiRules.add(rule);
                }
                if (tmpSearchRsMap == null)
                    tmpSearchRsMap = ArrayListMultimap.create();
                tmpSearchRsMap.put(tmpFrag, searchResult);
            }
        }


        for (WildCardRule rule : candiRules) {
            if (tmpSearchRsMap.keySet().size() < rule.getFragments().size()
                    || !tmpSearchRsMap.keySet().containsAll(rule.getFragments())) {
                continue;
            }

            if (checkRule(rule, tmpSearchRsMap)) {
                return rule.getInput();
            }
        }

        return null;



    }


    // public String searchOne2(String target) {
    //
    // Iterator<SearchResult> searchIter = _fragmentTree.search(target.toCharArray());
    // if (!searchIter.hasNext())
    // return null;
    //
    // // 获取所有备选规则集
    // Set<WildCardRule> candiRules = new HashSet<>();
    // ListMultimap<String, SearchResult> tmpSearchRsMap = ArrayListMultimap.create();
    //
    //
    // while (searchIter.hasNext()) {
    // SearchResult searchResult = searchIter.next();
    // for (String tmpFrag : searchResult.getOutputs()) {
    // for (WildCardRule rule : _frag2RuleMap.get(tmpFrag)) {
    // if (!rule.isWildCard())
    // return rule.getInput();
    // candiRules.add(rule);
    // }
    // tmpSearchRsMap.put(tmpFrag, searchResult);
    // }
    // }
    //
    //
    // for (WildCardRule rule : candiRules) {
    // if (!tmpSearchRsMap.keys().containsAll(rule.getFragments())) {
    // continue;
    // }
    // if (checkRule(rule, tmpSearchRsMap)) {
    // return rule.getInput();
    // }
    // }
    //
    // return null;
    //
    //
    //
    // }

    /**
     * @author wuyue
     * @return
     * 
     * @date Feb 9, 2015 2:29:21 PM
     */
    private static boolean checkRule(WildCardRule rule, ListMultimap<String, SearchResult> tmpSearchRsMap) {
        List<SearchResult> lastMatches = null;
        int skip = 0;
        for (WildCardRuleNode node : rule.getList()) {
            if (node.getSkip() == 0) {
                List<SearchResult> candiSr = tmpSearchRsMap.get(node.getContent());
                if (lastMatches == null) {
                    lastMatches = candiSr;
                } else {
                    candiSr = match(candiSr, lastMatches, skip, node.getContent().length());
                    if (candiSr.isEmpty())
                        return false;
                    else {
                        skip = 0;
                        lastMatches = candiSr;
                    }
                }
            } else {
                skip = node.getSkip();
            }
        }
        if (lastMatches != null && !lastMatches.isEmpty())
            return true;
        return false;
    }

    /**
     * @author wuyue
     * @param str
     * 
     * @date Feb 9, 2015 3:36:16 PM
     */
    private static List<SearchResult> match(List<SearchResult> test, List<SearchResult> lastMatches, int skip, int len) {
        List<SearchResult> ret = new ArrayList<>();
        for (SearchResult here : test) {
            for (SearchResult last : lastMatches) {
                int begin = here.getLastIndex() - len;
                if (last.getLastIndex() + skip >= begin && begin >= last.getLastIndex())
                    ret.add(here);
            }
        }
        return ret;
    }



    public static void main(String[] args) {
        WildcardAhoCorasickTree tree =
                new WildcardAhoCorasickTree(new HashSet<String>(Arrays.asList(new String[] {"人民*中?国", "人民*中国", "你好", "是?中国*大地",
                        "美*国"})));
        System.out.println(tree.searchOne("中华人民共和国万万么想到怎么办啊就是中国"));

        System.out.println(tree.searchAll("中华人民共和国万万么想到怎么办啊就是中国"));
    }
}
