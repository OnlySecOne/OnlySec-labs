package com.only.www.onlyseclabs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import java.io.StringReader;

@Controller
@RequestMapping("/xxe")
public class XXEController {

    /**
     * 返回XXE测试页面
     */
    @GetMapping("/web")
    public String showXXEPage() {
        return "web_vul_basic/xxe";  // 返回xxe.html模板
    }

    /**
     * 处理XXE漏洞测试请求
     */
    @PostMapping("/parse")
    @ResponseBody
    public String processXXE(@RequestBody String xmlContent) {
        try {
            // 创建一个允许XXE的XML解析器
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 启用XXE
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", true);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            // 解析XML
            Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));
            // 获取根节点的文本内容
            return doc.getDocumentElement().getTextContent();
        } catch (Exception e) {
            return "解析错误: " + e.getMessage();
        }
    }
}