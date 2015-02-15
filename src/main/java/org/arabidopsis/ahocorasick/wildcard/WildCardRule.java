package org.arabidopsis.ahocorasick.wildcard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Objects;


/**
 * 类 WildCardPattern.java 的实现描述 TODO
 * 
 * @author wuyue
 * 
 * @date Feb 6, 2015 5:09:34 PM
 */
public class WildCardRule {

    private static final Pattern P_WILDCARD = Pattern.compile("(\\?)+|(\\*)");
    // 不能连续*，通配符不能在开始或结束，?不能连续超过8个，*和？不能挨着
    private static final Pattern P_FORBID = Pattern.compile("^[\\?\\*]|[\\?\\*]$|(\\*){2,}|(\\?){8,}|\\*\\?|\\?\\*");
    private final String input;
    private final Set<String> fragments = new HashSet<String>();
    private final List<WildCardRuleNode> list = new ArrayList<WildCardRuleNode>();
    private boolean isWildCard = false;

    public String getInput() {
        return input;
    }

    public boolean isWildCard() {
        return isWildCard;
    }

    public Set<String> getFragments() {
        return fragments;
    }

    public List<WildCardRuleNode> getList() {
        return list;
    }

    public WildCardRule(String src) {
        if (StringUtils.isBlank(src))
            throw new IllegalArgumentException();
        src = src.trim();
        if (P_FORBID.matcher(src).matches())
            throw new IllegalArgumentException();
        this.input = src;
        Matcher matcher = P_WILDCARD.matcher(src);
        int end = 0;
        while (matcher.find()) {
            isWildCard = true;
            String before = src.substring(end, matcher.start());
            fragments.add(before);
            list.add(new WildCardRuleNode(before, false));
            list.add(new WildCardRuleNode(matcher.group().intern(), true));
            end = matcher.end();
        }
        String left = src.substring(end);
        fragments.add(left);
        list.add(new WildCardRuleNode(left, false));
    }

    @Override
    public int hashCode() {
        return input.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (o instanceof WildCardRule) {
            WildCardRule other = (WildCardRule) o;
            return Objects.equal(this.input, other.input);
        }
        return false;
    }

    @Override
    public String toString() {
        return input;
    }

    public static class WildCardRuleNode {

        private final String content;
        private final int skip;
        public static final int MAX_SKIP = 30;

        public WildCardRuleNode(String input, boolean isSkip) {
            if (!isSkip) {
                content = input;
                skip = 0;
            } else {
                content = null;
                if (input.equals("*"))
                    skip = MAX_SKIP;
                else {
                    skip = input.length();
                }
            }
        }

        public String getContent() {
            return content;
        }

        public int getSkip() {
            return skip;
        }
    }

}
