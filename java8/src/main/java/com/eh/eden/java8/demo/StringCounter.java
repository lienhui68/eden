package com.eh.eden.java8.demo;

import lombok.Getter;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 一个迭代式字数统计方法
 *
 * @author David Li
 * @create 2020/08/12
 */
public class StringCounter {

    final static String SENTENCE = "Nel mezzo del cammin di nostra  vita mi ritrovai in una selva oscura ché la dritta via era smarrita";


    public static void main(String[] args) {
        Spliterator<Character> spliterator = new WordCounterSpliterator(SENTENCE);
        Stream<Character> stream = StreamSupport.stream(spliterator, true);
        System.out.println(new StringCounter().countWorlds(stream));
    }

    private int countWorlds(Stream<Character> stream) {
        WordCounter wordCounter = stream.reduce(new WordCounter(0, true), WordCounter::accumulate, WordCounter::combine);
        return wordCounter.getCount();
    }
}

class WordCounter {
    @Getter
    private final int count;
    private final boolean lastSpace;

    public WordCounter(int count, boolean lastSpace) {
        this.count = count;
        this.lastSpace = lastSpace;
    }

    // 和迭代算法一样， accumulate 方 法 一 个个遍历Character
    public WordCounter accumulate(Character c) {
        if (Character.isWhitespace(c)) {
            return lastSpace ? this : new WordCounter(count, true);
        } else {
            // 上一个字符是空格，而 当前遍历的字符不是 空格时，将单词计数器 加一
            return lastSpace ? new WordCounter(count + 1, false) : this;
        }
    }

    // 合并两个 Word- Counter，把其 计数器加起来
    public WordCounter combine(WordCounter wordCounter) {
        // 仅需要计数器 的总和，无需关 心lastSpace
        return new WordCounter(count + wordCounter.count, wordCounter.lastSpace);
    }
}

class WordCounterSpliterator implements Spliterator<Character> {

    private final String string;
    private int currentCharPos = 0;

    public WordCounterSpliterator(String string) {
        this.string = string;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Character> action) {
        // 处理当前字符
        action.accept(string.charAt(currentCharPos++));
        // 如果还有字符要处理，则返回true
        return currentCharPos < string.length();
    }

    @Override
    public Spliterator<Character> trySplit() {
        int currentSize = string.length() - currentCharPos;
        // 返回null表示要解析的string已经足够小，可以顺序处理
        if (currentSize < 10) {
            return null;
        }
        //将试探拆分位置设定为要解析的String的中间，然后让拆分位置前进直到下 一个空格
        for (int splitPos = currentCharPos + currentSize / 2; splitPos < string.length(); splitPos++) {
            if (Character.isWhitespace(string.charAt(splitPos))) {
                // 创建一个新的WordCounterSpliterator来解析string从开始位置到拆分位置的部分
                Spliterator<Character> spliterator = new WordCounterSpliterator(string.substring(currentCharPos, splitPos));
                // 将拆分位置赋值给这个WordCounterSpliterator的起始位置
                currentCharPos = splitPos;
                return spliterator;
            }
        }
        return null;
    }

    @Override
    public long estimateSize() {
        // 还剩多少个分配元素
        return string.length() - currentCharPos;
    }

    @Override
    public int characteristics() {
        return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
    }
}