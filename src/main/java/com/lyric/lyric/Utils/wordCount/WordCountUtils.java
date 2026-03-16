package com.lyric.lyric.Utils.wordCount;

import com.lyric.lyric.DTO.diary.Diary;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.regex.Pattern;

/**
 * 字数统计工具类
 * 提供对不同格式内容的字数统计功能，包括纯文本、富文本和Markdown格式
 *
 * @author Lyric
 * @since 2025-11-21
 */
public class WordCountUtils {

    // Markdown标记正则表达式
    /**
     * Markdown标题正则表达式，匹配以1-6个#号开头的行
     */
    private static final Pattern MARKDOWN_HEADER = Pattern.compile("^#{1,6}\\s+", Pattern.MULTILINE);
    
    /**
     * Markdown粗体和斜体正则表达式，匹配用*或_包围的内容
     */
    private static final Pattern MARKDOWN_BOLD_ITALIC = Pattern.compile("(\\*{1,3}|_{1,3})(.*?)\\1");
    
    /**
     * Markdown代码块正则表达式，匹配三个反引号包围的代码块
     */
    private static final Pattern MARKDOWN_CODE_BLOCK = Pattern.compile("```[\\s\\S]*?```", Pattern.MULTILINE);
    
    /**
     * Markdown行内代码正则表达式，匹配单个反引号包围的内容
     */
    private static final Pattern MARKDOWN_INLINE_CODE = Pattern.compile("`[^`]*`");
    
    /**
     * Markdown 链接正则表达式，匹配 [文本](链接) 格式
     */
    private static final Pattern MARKDOWN_LINK = Pattern.compile("\\[([^]]*)]\\([^)]*\\)");
        
    /**
     * Markdown 图片正则表达式，匹配![文本](链接) 格式
     */
    private static final Pattern MARKDOWN_IMAGE = Pattern.compile("!\\[([^]]*)]\\([^)]*\\)");
        
    /**
     * Markdown 列表正则表达式，匹配以 -、*或 + 开头的列表项
     */
    private static final Pattern MARKDOWN_LIST = Pattern.compile("^[\\s]*[-*+]\\s+", Pattern.MULTILINE);
    
    /**
     * Markdown引用正则表达式，匹配以>开头的引用行
     */
    private static final Pattern MARKDOWN_QUOTE = Pattern.compile("^>\\s+", Pattern.MULTILINE);
    
    /**
     * Markdown分隔线正则表达式，匹配由-、*或_组成的分隔线
     */
    private static final Pattern MARKDOWN_HR = Pattern.compile("^[-*_]{3,}\\s*$", Pattern.MULTILINE);

    /**
     * 计算指定格式内容的字数
     * 根据不同的内容格式选择相应的字数统计方法
     *
     * @param content 内容字符串
     * @param format 内容格式枚举，支持RICH_TEXT、MARKDOWN等格式
     * @return 字数统计结果
     */
    public static int calculateWordCount(String content, Diary.ContentFormat format) {

        // 判断内容是否为空
        if(content ==  null || content.trim().isEmpty()) return 0;

        // 根据编辑器类型计算字数
        return switch (format) {
            case RICH_TEXT -> calculateRichTextWordCount(content);
            case MARKDOWN -> calculateMarkdownWordCount(content);
        };
    }

    /**
     * 计算富文本(HTML)内容的字数
     * 使用Jsoup解析HTML并提取纯文本进行字数统计
     *
     * @param htmlContent HTML格式的内容
     * @return 字数统计结果
     */
    private static int calculateRichTextWordCount(String htmlContent) {
        try {
            // 使用Jsoup解析HTML并提取纯文本
            Document doc = Jsoup.parse(htmlContent);
            String plainText = doc.text();

            // 清理和统计
            return cleanAndCount(plainText);

        } catch (Exception e) {
            // 如果解析失败，回退到简单HTML标签移除
            String cleanText = htmlContent.replaceAll("<[^>]*>", "");
            return cleanAndCount(cleanText);
        }
    }

    /**
     * 计算Markdown格式内容的字数
     * 通过正则表达式移除各种Markdown标记后进行字数统计
     *
     * @param markdownContent Markdown格式的内容
     * @return 字数统计结果
     */
    private static int calculateMarkdownWordCount(String markdownContent) {
        String cleanText = markdownContent;

        // 逐步移除各种Markdown标记
        cleanText = MARKDOWN_HEADER.matcher(cleanText).replaceAll("");
        cleanText = MARKDOWN_BOLD_ITALIC.matcher(cleanText).replaceAll("$2");
        cleanText = MARKDOWN_CODE_BLOCK.matcher(cleanText).replaceAll("");
        cleanText = MARKDOWN_INLINE_CODE.matcher(cleanText).replaceAll("");
        cleanText = MARKDOWN_LINK.matcher(cleanText).replaceAll("$1");
        cleanText = MARKDOWN_IMAGE.matcher(cleanText).replaceAll("$1");
        cleanText = MARKDOWN_LIST.matcher(cleanText).replaceAll("");
        cleanText = MARKDOWN_QUOTE.matcher(cleanText).replaceAll("");
        cleanText = MARKDOWN_HR.matcher(cleanText).replaceAll("");

        return cleanAndCount(cleanText);
    }

    /**
     * 计算纯文本内容的字数
     *
     * @param text 纯文本内容
     * @return 字数统计结果
     */
    private static int calculatePlainTextWordCount(String text) {
        return cleanAndCount(text);
    }

    /**
     * 清理文本并进行字数统计
     * 对文本进行预处理，包括去除空白字符、合并连续空白等操作，然后分别统计中文字数、英文单词数和其他字符数
     *
     * @param text 待处理的文本
     * @return 综合字数统计结果
     */
    private static int cleanAndCount(String text) {
        if (text == null) return 0;

        // 1. 去除首尾空白
        String cleaned = text.trim();

        // 2. 合并连续的空白字符
        cleaned = cleaned.replaceAll("\\s+", " ");

        // 3. 统计中文字数（每个汉字算一个字）
        int chineseCharCount = countChineseCharacters(cleaned);

        // 4. 统计英文单词数（按空格分割）
        int englishWordCount = countEnglishWords(cleaned);

        // 5. 统计其他字符（数字、标点等）
        int otherCharCount = countOtherCharacters(cleaned);

        // 综合计算（可根据业务需求调整权重）
        return chineseCharCount + englishWordCount + (otherCharCount / 3);
    }

    /**
     * 统计中文字符数量
     * 使用Unicode范围\u4e00-\u9fa5匹配中文字符
     *
     * @param text 待统计的文本
     * @return 中文字符数量
     */
    private static int countChineseCharacters(String text) {
        if (text == null) return 0;
        // 匹配中文字符
        String chineseChars = text.replaceAll("[^\\u4e00-\\u9fa5]", "");
        return chineseChars.length();
    }

    /**
     * 统计英文单词数量
     *
     * @param text 待统计的文本
     * @return 英文单词数量
     */
    private static int countEnglishWords(String text) {
        if (text == null) return 0;

        // 移除中文字符，保留英文和数字
        String englishText = text.replaceAll("[\\u4e00-\\u9fa5]", " ");

        // 分割单词（考虑连字符等）
        String[] words = englishText.split("[^a-zA-Z0-9'-]+");

        int count = 0;
        for (String word : words) {
            if (word.matches("[a-zA-Z0-9'-]+") && !word.trim().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 统计其他字符数量（数字、标点符号等非中英文字符）
     *
     * @param text 待统计的文本
     * @return 其他字符数量
     */
    private static int countOtherCharacters(String text) {
        if (text == null) return 0;

        // 移除中文字符和英文字母数字
        String otherChars = text.replaceAll("[\\u4e00-\\u9fa5a-zA-Z0-9\\s]", "");
        return otherChars.length();
    }
}