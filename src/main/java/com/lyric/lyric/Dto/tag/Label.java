package com.lyric.lyric.Dto.tag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 标签DTO类
 *
 * @author Lyric
 */
@Getter
@Setter
@NoArgsConstructor
public class Label {

    /**
     * 标签名字
     */
    private String tagName;

    /**
     * 标签类型
     */
    private String tagType;

    /**
     * 定义列表（可选）
     */
    private List<String> definitions;

    /**
     * 有参构造方法（不包含definitions定义列表）
     *
     * @param tagName 标签名字
     * @param tagType 标签类型
     */
    public Label(String tagName, String tagType) {
        this.tagName = tagName;
        this.tagType = tagType;
    }

    /**
     * 有参构造方法（包含definitions定义列表）
     *
     * @param tagName      标签名字
     * @param tagType      标签类型
     * @param definitions  定义列表
     */
    public Label(String tagName, String tagType, List<String> definitions) {
        this.tagName = tagName;
        this.tagType = tagType;
        this.definitions = definitions;
    }
}
