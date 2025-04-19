package com.only.www.onlyseclabs.service.impl;

import com.only.www.onlyseclabs.entity.Message;
import com.only.www.onlyseclabs.mapper.MessageMapper;
import com.only.www.onlyseclabs.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;

    // 添加新留言

    public int insertMessage(Message message) {
        return messageMapper.insertMessage(message);
    }

    // 删除留言

    public  int deleteMessage(int id) {
        return messageMapper.deleteMessage(id);
    }

    // 获取所有留言列表

   public List<Message> getAllMessages() {
        return messageMapper.getAllMessages();
    }

    // 根据用户ID获取留言
    public List<Message> getMessagesByUserId(Long userId) {
        return null;
    }

    // 根据ID获取单条留言
   public Message getMessageById(int id) {
        return messageMapper.getMessageById(id);
    }

    public List<Message> searchMessages(String keyword) {
        return messageMapper.searchMessages(keyword);
    }

    public int updateMessage(Message message) {
        return messageMapper.updateMessage(message);
    }

}
