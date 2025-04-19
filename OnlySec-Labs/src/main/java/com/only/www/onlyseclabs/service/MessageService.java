package com.only.www.onlyseclabs.service;

import com.only.www.onlyseclabs.entity.Message;

import java.util.List;

public interface MessageService {
    // 添加新留言
    public int insertMessage(Message message);

    // 删除留言
    public  int deleteMessage(int id);

    // 获取所有留言列表

    public List<Message> getAllMessages();

    // 根据用户ID获取留言
    public List<Message> getMessagesByUserId(Long userId);

    // 根据ID获取单条留言
    public Message getMessageById(int id);

    public List<Message> searchMessages(String keyword);

    public int updateMessage(Message message);

}
