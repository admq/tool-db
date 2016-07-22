package com.cootoo.tool.move;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by larry on 16/7/22.
 */
public class XmlUtil {
    private static Logger logger = LoggerFactory.getLogger(XmlUtil.class);

    public Node readXml(String xmlPath) throws Exception {
        File file = checkFilePath(xmlPath);
        if (file == null) return null;
        String content = FileUtils.readFileToString(file);
        Pattern pattern = Pattern.compile("<[/\\w =\"\\.?]+>");
        Matcher matcher = pattern.matcher(content);
        Node root = new Node("Root");
        Node currentNode = root;
        Stack<Node> stack = new Stack<>();
        stack.push(currentNode);
        boolean flag = true; // 标记检查是否成功
        while (matcher.find() && flag) {
            String s = matcher.group();
            if (s.startsWith("<?")) continue;
            if (s.startsWith("</")) {
                String temp = s.substring(2,s.length()-1);
                currentNode = stack.pop();
                if (currentNode != null && currentNode.getNodeName().equals(temp)) {
                    currentNode = stack.peek();
                    continue;
                } else {
                    flag = false;
                    System.out.println("错误!缺少标签");
                }
            } else {
                String titleContent = s.substring(1,s.length()-1);
                Node node = generateNode(titleContent);

                //判断如果叶子节点,设置叶子节点的Text
                int endIdx = matcher.end();
                int idx = content.indexOf("<", endIdx);
                String string = content.substring(endIdx, idx);
                if (string != null && !"".equals(string.trim())) {
                    node.setText(string);
                }
                currentNode.addNode(node);
                currentNode = node;
                stack.push(currentNode);
            }
        }
        if (flag && stack.size() != 1) {
            flag = false;
            System.out.println("错误!缺少标签");
        }
        return root;
    }

    /**
     * 生成节点
     * @param str
     * @return
     * @throws Exception
     */
    private Node generateNode(String str) throws Exception {
        if(str == null) return null;
        Pattern pattern = Pattern.compile("[ ]+");
        String[] sl = pattern.split(str.trim());
        Node node = new Node();
        for (int i=0; i<sl.length; i++) {
            String tstr = sl[i];
            if (i == 0) {
                node.setNodeName(tstr);
                continue;
            }
            String[] split = tstr.split("=");
            if (split.length != 2) {
                throw new Exception("属性配置错误:"+str);
            }
            node.setProperty(split[0].trim(), split[1].trim());
        }
        return node;
    }

    private File checkFilePath(String xmlPath) {
        if (!xmlPath.endsWith("xml")) {
            logger.error("文件错误,请选择xml格式的文件");
            return null;
        }
        File file = new File(xmlPath);
        if (!file.isFile()) {
            logger.error("未找到该文件:" + xmlPath);
            return null;
        }
        return file;
    }
}

/**
 * xml节点类
 */
class Node {
    private String nodeName;
    private Map<String, String> properties;
    private List<Node> childNodes;
    private String text;

    public Node() {
        properties = new HashMap<>();
        childNodes = new ArrayList<>();
    }

    Node(String nodeName) {
        this.nodeName = nodeName;
        properties = new HashMap<>();
        childNodes = new ArrayList<>();
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setProperty(String name, String value) {
        properties.put(name, value);
    }

    public String getProperty(String name) {
        return properties.get(name);
    }

    public void addNode(Node node) {
        childNodes.add(node);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
