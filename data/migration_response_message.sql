-- 响应消息表数据初始化脚本
-- 将 YAML 配置文件中的响应消息迁移到数据库

-- 清空旧数据（可选，首次初始化时请注释掉）
-- DELETE FROM response_message;

-- ==================== 成功消息 (SUCCESS) ====================
INSERT OR REPLACE INTO response_message(message_key, message_type, code, message) VALUES
('save_success', 'SUCCESS', '200', '保存成功啦~这次做得还不错嘛'),
('delete_success', 'SUCCESS', '200', '删除完成！这种小事对我来说轻轻松松啦'),
('restore_from_trash_success', 'SUCCESS', '200', '已经从回收站恢复好了，下次要小心点哦'),
('message_config_success', 'SUCCESS', '200', '消息配置完成！虽然是你要求的，但确实做得不错呢'),
('setting_success', 'SUCCESS', '200', '设置成功！这下满意了吧？'),
('query_success', 'SUCCESS', '200', '查询完成！结果不是理所当然的嘛'),
('move_to_trash_success', 'SUCCESS', '200', '已经移入回收站啦，可别后悔哦'),
('modify_success', 'SUCCESS', '200', '修改成功！这次就勉强夸你一下好了'),

-- BaseTag（基本标签）相关
('base_tag_create_success', 'SUCCESS', '200', '基本标签创建成功！新的标签已经添加好了哦'),
('base_tag_query_success', 'SUCCESS', '200', '基本标签查询完成！结果就在这里呢'),
('base_tag_query_by_type_success', 'SUCCESS', '200', '按类型查询成功！分类整理可是我的强项呢'),
('base_tag_update_success', 'SUCCESS', '200', '基本标签更新完成！这次做得还不错嘛'),
('base_tag_delete_success', 'SUCCESS', '200', '基本标签删除成功！虽然有点可惜，但这是你的选择呢'),
('base_tag_usage_increment_success', 'SUCCESS', '200', '使用次数已增加！这个标签越来越重要了呢'),

-- PersonTag（人物标签）相关
('person_tag_create_success', 'SUCCESS', '200', '人物标签创建成功！新的人物已经记录好了哦'),
('person_tag_query_success', 'SUCCESS', '200', '人物标签查询完成！找到了你想要的人呢'),
('person_tag_query_by_name_success', 'SUCCESS', '200', '按名称查询成功！这个人我记得很清楚呢'),
('person_tag_query_by_gender_success', 'SUCCESS', '200', '按性别筛选完成！分类查询对我来说很简单哦'),
('person_tag_query_by_relation_success', 'SUCCESS', '200', '按关系查询成功！人际关系网已经整理好了呢'),
('person_tag_update_success', 'SUCCESS', '200', '人物标签更新完成！信息已经最新了哦'),
('person_tag_delete_success', 'SUCCESS', '200', '人物标签删除成功！希望你不会后悔这个决定呢'),
('person_tag_appearance_increment_success', 'SUCCESS', '200', '出现次数已增加！这个人物越来越重要了呢'),

-- LocationTag（地点标签）相关
('location_tag_create_success', 'SUCCESS', '200', '地点标签创建成功！新的地点已经记录在案了哦'),
('location_tag_query_success', 'SUCCESS', '200', '地点标签查询完成！地理位置信息都在这里呢'),
('location_tag_query_by_name_success', 'SUCCESS', '200', '按名称查询成功！这个地方我印象很深呢'),
('location_tag_query_by_alias_success', 'SUCCESS', '200', '按别名查询成功！即使是别称我也能找到哦'),
('location_tag_query_by_city_success', 'SUCCESS', '200', '按城市筛选完成！这座城市的所有地点都找到了呢'),
('location_tag_query_by_province_success', 'SUCCESS', '200', '按省份筛选完成！这个省的地点可真不少呢'),
('location_tag_query_by_country_success', 'SUCCESS', '200', '按国家筛选完成！跨国查询也不是问题哦'),
('location_tag_update_success', 'SUCCESS', '200', '地点标签更新完成！地理信息已经最新了哦'),
('location_tag_delete_success', 'SUCCESS', '200', '地点标签删除成功！这个地方已经从记录中移除了呢'),
('location_tag_appearance_increment_success', 'SUCCESS', '200', '出现次数已增加！这个地点越来越常去了呢'),

-- EventTag（事件标签）相关
('tog_event_create_success', 'SUCCESS', '200', '父事件创建成功！整个事件的时间线已经开始记录了哦'),
('tog_event_query_success', 'SUCCESS', '200', '父事件查询完成！事件的来龙去脉都很清楚呢'),
('tog_event_query_by_diary_success', 'SUCCESS', '200', '按日记查询父事件成功！这篇日记的故事背景找到了呢'),
('tog_event_update_success', 'SUCCESS', '200', '父事件更新完成！事件信息已经同步了哦'),
('tog_event_delete_success', 'SUCCESS', '200', '父事件删除成功！整个事件及其子事件都已清除干净了哦'),
('sub_event_create_success', 'SUCCESS', '200', '子事件创建成功！事件的细节已经补充完整了哦'),
('sub_event_query_success', 'SUCCESS', '200', '子事件查询完成！这些细节我都记得很清楚呢'),
('sub_event_query_by_tog_event_success', 'SUCCESS', '200', '按父事件查询子事件成功！所有分支事件都整理好了哦'),
('sub_event_update_success', 'SUCCESS', '200', '子事件更新完成！事件的细节已经修正了哦'),
('sub_event_delete_success', 'SUCCESS', '200', '子事件删除成功！这个事件的片段已经移除了呢');

-- ==================== 系统错误 (system-error) ====================
INSERT OR REPLACE INTO response_message(message_key, message_type, code, message) VALUES
('system_error', 'system-error', '500', '系统内部错误啦...请稍后再试嘛'),
('network_error', 'system-error', '500', '网络连接异常啦，快检查一下网络嘛'),
('database_error', 'system-error', '500', '数据库操作失败了...请稍后再试哦');

-- ==================== 业务错误 (business-error) ====================
INSERT OR REPLACE INTO response_message(message_key, message_type, code, message) VALUES
('diary_content_empty', 'business-error', '400', '喂！日记内容怎么能是空的啊！至少写点什么嘛...'),
('ai_processing_error', 'business-error', '500', '真是的，AI 处理出错了...这种时候就该好好反省一下呢'),
('media_type_not_supported', 'business-error', '400', '这种媒体文件类型不支持哦，你是在故意为难我吗？'),
('file_not_found', 'business-error', '404', '找不到文件啦！是不是已经被删除了？'),
('diary_not_found', 'business-error', '404', '找不到日记啦！你是不是记错了？'),
('user_preference_invalid', 'business-error', '400', '你的偏好设置有问题啦，好好检查一下嘛'),
('diary_title_empty', 'business-error', '400', '标题怎么能是空的！这样一点都不认真哦'),
('file_empty', 'business-error', '400', '文件是空的！你是不是想上传空气啊？'),
('ai_model_not_available', 'business-error', '503', '哼~现在 AI 模型不在服务状态呢，真是的...明明人家都准备好了的说'),
('media_upload_failed', 'business-error', '500', '呜...媒体文件上传失败了啦，都怪服务器不争气'),
('ai_request_timeout', 'business-error', '408', '等太久了啦！AI 请求超时了，真是让人着急'),
('file_type_not_supported', 'business-error', '415', '这种文件类型不支持哦，你是不是搞错了什么？'),
('file_size_exceeded', 'business-error', '413', '文件太大了啦！超过限制了呢，压缩一下再试试嘛'),
('file_upload_failed', 'business-error', '500', '呜...文件上传失败了，都怪服务器不争气'),
('diary_not_in_trash', 'business-error', '400', '日记根本不在回收站里，别乱说嘛'),
('months_out_in_range', 'business-error', '404', '你家一个年有几个月？'),
('user_setting_update_failed', 'business-error', '500', '用户设置更新失败了...真是的，系统又在闹脾气'),
('response_message_command_not_input', 'business-error', '400', '响应消息命令都没输入，让人家怎么工作嘛'),

-- BaseTag（基本标签）相关
('base_tag_not_found', 'business-error', '404', '基本标签不存在啦！你是不是记错了？'),
('base_tag_create_failed', 'business-error', '500', '呜...基本标签创建失败了，都怪系统不争气呢'),
('base_tag_update_failed', 'business-error', '500', '基本标签更新失败了呢...请稍后再试哦'),
('base_tag_delete_failed', 'business-error', '500', '基本标签删除失败了...真是的，系统在闹脾气吗？'),
('base_tag_type_invalid', 'business-error', '400', '标签类型无效哦！好好检查一下输入吧'),
('base_tag_name_empty', 'business-error', '400', '标签名称怎么能是空的！这样一点都不认真哦'),
('base_tag_already_exists', 'business-error', '409', '这个标签已经存在了啦！别重复添加嘛'),

-- PersonTag（人物标签）相关
('person_tag_not_found', 'business-error', '404', '人物标签找不到啦！可能你记错名字了哦'),
('person_tag_create_failed', 'business-error', '500', '呜...人物标签创建失败了，人家也很无奈呢'),
('person_tag_update_failed', 'business-error', '500', '人物标签更新失败了呢...系统又在闹脾气了'),
('person_tag_delete_failed', 'business-error', '500', '人物标签删除失败了...请稍后再试一次吧'),
('person_tag_name_empty', 'business-error', '400', '人物名称不能为空啦！至少告诉我是谁嘛'),
('person_tag_name_already_exists', 'business-error', '409', '这个名字的人物已经存在了哦！不要重复添加同一个人呢'),
('person_tag_gender_invalid', 'business-error', '400', '性别信息无效哦！只能选择男或女呢'),
('person_tag_relation_invalid', 'business-error', '400', '关系描述有问题啦！好好想想是什么关系嘛'),

-- LocationTag（地点标签）相关
('location_tag_not_found', 'business-error', '404', '地点标签找不到啦！可能这个地方不存在呢'),
('location_tag_create_failed', 'business-error', '500', '呜...地点标签创建失败了，地理信息系统出问题了啦'),
('location_tag_update_failed', 'business-error', '500', '地点标签更新失败了呢...坐标数据无法保存哦'),
('location_tag_delete_failed', 'business-error', '500', '地点标签删除失败了...这个地方好像被使用了呢'),
('location_tag_name_empty', 'business-error', '400', '地点名称不能为空啦！总得有个名字吧？'),
('location_tag_already_exists', 'business-error', '409', '这个地点已经存在了！同一个地方不需要重复添加哦'),
('location_tag_coordinates_invalid', 'business-error', '400', '经纬度坐标无效哦！地理位置可不是随便写的呢'),
('location_tag_province_invalid', 'business-error', '400', '省份信息不对哦！请确认是正确的行政区划呢'),
('location_tag_city_invalid', 'business-error', '400', '城市信息有误啦！这个城市真的存在吗？'),
('location_tag_country_invalid', 'business-error', '400', '国家信息无效哦！请不要乱写国家名称呢'),

-- EventTag（事件标签）相关
('tog_event_not_found', 'business-error', '404', '父事件找不到啦！这个事件可能已经被删除了哦'),
('tog_event_create_failed', 'business-error', '500', '呜...父事件创建失败了，时间线记录出现问题了呢'),
('tog_event_update_failed', 'business-error', '500', '父事件更新失败了呢...事件信息无法同步哦'),
('tog_event_delete_failed', 'business-error', '500', '父事件删除失败了...还有子事件关联着呢，先处理一下嘛'),
('tog_event_date_invalid', 'business-error', '400', '事件日期无效哦！开始和结束时间要合理才行呢'),
('tog_event_name_empty', 'business-error', '400', '父事件名称不能为空啦！总得有个事件名称吧？'),
('sub_event_not_found', 'business-error', '404', '子事件找不到啦！这个事件片段可能不存在哦'),
('sub_event_create_failed', 'business-error', '500', '呜...子事件创建失败了，事件细节无法补充了呢'),
('sub_event_update_failed', 'business-error', '500', '子事件更新失败了呢...细节信息无法修改哦'),
('sub_event_delete_failed', 'business-error', '500', '子事件删除失败了...可能还有其他关联数据呢'),
('sub_event_date_invalid', 'business-error', '400', '子事件日期有问题哦！要在整个事件的时间范围内呢'),
('sub_event_name_empty', 'business-error', '400', '子事件名称不能为空啦！事件的每个细节都很重要哦'),
('sub_event_tog_event_not_found', 'business-error', '404', '所属的父事件不存在哦！子事件不能单独存在呢'),
('cascade_delete_failed', 'business-error', '500', '级联删除失败了...关联数据太多了，清理起来很麻烦呢');
