package com.lyric.lyric.POJO.AI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 事件去重分析结果
 * <p>
 * 用于接收AI返回的事件匹配结果，包含匹配的事件ID和更新后的事件描述。
 * </p>
 *
 * @author Yichaoxuan
 * @since 2026-04-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDeduplicationResult {

    /**
     * 匹配到的事件ID
     * <p>
     * 如果匹配到已有事件，则返回该事件的ID；
     * 如果没有匹配到任何事件，则返回-1。
     * </p>
     */
    private Integer eventId;

    /**
     * 更新后的事件名称
     * <p>
     *  如果匹配到已有事件，AI会返回更新后的事件名称；
     *  如果没有匹配到事件（eventId为-1），则该字段为null。
     * </p>
     */
    private String updatedName;

    /**
     * 更新后的事件描述
     * <p>
     * 如果匹配到已有事件，AI会返回更新后的事件描述；
     * 如果没有匹配到事件（eventId为-1），则该字段为null。
     * </p>
     */
    private String updatedDescription;
}
