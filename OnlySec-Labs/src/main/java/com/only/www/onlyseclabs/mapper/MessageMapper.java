package com.only.www.onlyseclabs.mapper;

import com.only.www.onlyseclabs.entity.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageMapper {
    
    // 添加新留言
    int insertMessage(Message message);
    // 删除留言
    int deleteMessage(int id);
    // 获取所有留言列表
    List<Message> getAllMessages();
    // 根据用户ID获取留言
    List<Message> getMessagesByUserId(Long userId);
    // 根据ID获取单条留言
    Message getMessageById(int id);
    // 更新留言内容
    int updateMessage(Message message);
    // 搜索留言
    List<Message> searchMessages(String keyword);
}
