package com.nntk.m2s.utils;

import cn.hutool.core.util.ReUtil;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;

public class MarkdownUtils {

    /**
     * 将Markdown文本转换为HTML
     *
     * @param markdownText Markdown文本
     * @return 转换后的HTML文本
     */
    public static String removeMarkdownTags(String markdownText) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownText);
        TextContentRenderer renderer = TextContentRenderer.builder().build();
        return renderer.render(document);
    }

    public static String getJson(String markdownText) {
        if (!markdownText.contains("```json")) {
            return markdownText;
        }
        String sourceRegex = "```json(.*?)```";
        String source = ReUtil.get(sourceRegex, markdownText, 1);
        return source;
    }

}
