package com.shbst.bst.text;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.greenrobot.event.EventBus;

/**
 * Created by hegang on 2017-05-19.
 */
public class ConfigurationParams {
    private static final String TAG = "ConfigurationParams";
    private Context mContext;
    DocumentBuilderFactory factory;
    public static ConfigurationParams configurationParams = null;
    private String CFG_ROOT = "root";   //root 根节点
    private String CFG_RESET = "reset";  // 一键恢复默认
    private String CFG_RESOURCE = "resource";  // 资源配置节点
    private String CFG_PARAMETER = "parameter";  //参数配置节点

    private final String CFG_RESOURCE_TITLE = "title";
    private final String CFG_RESOURCE_SCROLLTEXT = "scrollingtext";
    private final String CFG_RESOURCE_PICTURE = "picture";
    private final String CFG_RESOURCE_VIDEO = "video";

    private final String CFG_PARAMETER_VOLUME = "volume";
    private final String CFG_PARAMETER_BRIGHTNESS = "brightness";
    private final String CFG_PARAMETER_STANDBY = "standby";
    private final String CFG_PARAMETER_FULLSCREEN = "fullscreen";
    private final String CFG_PARAMETER_SCROLLAREA = "scrollingarea";
    private final String CFG_PARAMETER_TITLEAREA = "titlearea";
    private final String CFG_PARAMETER_TIMERAREA = "timearea";
    private EventBus parseEventBus = EventBus.getDefault();
    private boolean resetMediaScreen = false;   //是否一键恢复

    public static ConfigurationParams getConfigurationParams(Activity context) {
        if (configurationParams == null) {
            configurationParams = new ConfigurationParams(context);
        }
        return configurationParams;
    }



    public ConfigurationParams(Context context) {
        this.mContext = context;
    }

    public void parseXML(InputStream is) {
        Message msg = new Message();
        msg.obj = is;
        parseXMLHandler.sendMessage(msg);
    }

    Handler parseXMLHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            InputStream data = (InputStream) msg.obj;
            // 创建DOM工厂对象
            try {
                Log.i(TAG, "parseXML 创建DOM工厂对象");
                factory = DocumentBuilderFactory.newInstance();
                // DocumentBuilder对象
                DocumentBuilder builder = null;
                Log.i(TAG, "parseXML DocumentBuilder对象");
                builder = factory.newDocumentBuilder();

                // 获取文档对象
                Document document = builder.parse(data);
                Log.i(TAG, "parseXML 获取文档对象");
                // 获取文档对象的root
                Element root = document.getDocumentElement();
                Log.i(TAG, "parseXML 获取文档对象的root");
                // 获取root根节点中所有的节点对象
                NodeList personNodes = root.getChildNodes();

                Log.i(TAG, "parseXML 获取SubUI根节点中所有的节点对象");
                for (int i = 0; i < personNodes.getLength(); i++) {
                    // 根据item(index)获取该索引对应的节点对象
//                Log.i(TAG, "parseXML: " + personNodes.item(i).getNodeName());
                    if (personNodes.item(i).getNodeName().equals(CFG_RESET)) {
                        String content = personNodes.item(i).getTextContent();
                        if (content.equals("true")) {
                            resetMediaScreen = true;
                        } else {
                            resetMediaScreen = false;
                        }
                        Log.i(TAG, "parseXML:CFG_RESET " + resetMediaScreen);
                        parseEventBus.post(new Params(CFG_RESET,String.valueOf(resetMediaScreen)));
                    }
                    if (!resetMediaScreen) {
                        if (personNodes.item(i).getNodeName().equals(CFG_RESOURCE)) {
                            Node n = personNodes.item(i);
                            NodeList nodelist = n.getChildNodes();
                            for (int j = 0; j < nodelist.getLength(); j++) {
                                if (nodelist.item(j) instanceof Element) {
//                                Log.i(TAG, "parseXML:CFG_RESOURCE " + nodelist.item(j).getNodeName());
                                    switch (nodelist.item(j).getNodeName()) {
                                        case CFG_RESOURCE_TITLE:
                                            String title = nodelist.item(j).getTextContent();
                                            Log.i(TAG, "parseXML:CFG_RESOURCE_TITLE: "+title);
                                            parseEventBus.post(new Params(CFG_RESOURCE_TITLE,title));
                                            break;
                                        case CFG_RESOURCE_SCROLLTEXT:
                                            String scrolltext = nodelist.item(j).getTextContent();
                                            Log.i(TAG, "parseXML:CFG_RESOURCE_SCROLLTEXT: "+scrolltext);
                                            parseEventBus.post(new Params(CFG_RESOURCE_SCROLLTEXT,scrolltext));
                                            break;
                                        case CFG_RESOURCE_PICTURE:
                                            NodeList mutimedia = nodelist.item(j).getChildNodes();
                                            for (int k = 0; k < mutimedia.getLength(); k++) {
                                                if (mutimedia.item(k) instanceof Element) {
                                                    Log.i(TAG, "parseXML:CFG_RESOURCE_MUTIMEDIA: "+mutimedia.item(k).getTextContent());
                                                    parseEventBus.post(new Params(CFG_RESOURCE_PICTURE,mutimedia.item(k).getTextContent()));
                                                }
                                            }

                                            break;
                                        case CFG_RESOURCE_VIDEO:
                                            String video = nodelist.item(j).getTextContent();
                                            Log.i(TAG, "parseXML:CFG_RESOURCE_VIDEO: "+video);
                                            parseEventBus.post(new Params(CFG_RESOURCE_VIDEO,video));
                                            break;

                                    }
                                }

                            }
                        }
                        if (personNodes.item(i).getNodeName().equals(CFG_PARAMETER)) {
                            Node n = personNodes.item(i);
                            NodeList nodelist = n.getChildNodes();
                            for (int j = 0; j < nodelist.getLength(); j++) {
                                if (nodelist.item(j) instanceof Element) {
                                    switch (nodelist.item(j).getNodeName()) {
                                        case CFG_PARAMETER_VOLUME:
                                            String volume = nodelist.item(j).getTextContent();
                                            Log.i(TAG, "parseXML:volume: "+volume);
                                            parseEventBus.post(new Params(CFG_PARAMETER_VOLUME,volume));
                                            break;
                                        case CFG_PARAMETER_BRIGHTNESS:
                                            String brightness = nodelist.item(j).getTextContent();
                                            Log.i(TAG, "parseXML:brightness: "+brightness);
                                            parseEventBus.post(new Params(CFG_PARAMETER_BRIGHTNESS,brightness));
                                            break;
                                        case CFG_PARAMETER_FULLSCREEN:
                                            String fullscreen = nodelist.item(j).getTextContent();
                                            parseEventBus.post(new Params(CFG_PARAMETER_FULLSCREEN,fullscreen));
                                            Log.i(TAG, "parseXML:fullscreen: "+fullscreen);
                                            break;
                                        case CFG_PARAMETER_SCROLLAREA:
                                            String scrollingarea = nodelist.item(j).getTextContent();
                                            parseEventBus.post(new Params(CFG_PARAMETER_SCROLLAREA,scrollingarea));
                                            Log.i(TAG, "parseXML:scrollingarea: "+scrollingarea);
                                            break;
                                        case CFG_PARAMETER_TITLEAREA:
                                            String titlearea = nodelist.item(j).getTextContent();
                                            parseEventBus.post(new Params(CFG_PARAMETER_TITLEAREA,titlearea));
                                            Log.i(TAG, "parseXML:titlearea: "+titlearea);
                                            break;
                                        case CFG_PARAMETER_TIMERAREA:

                                            break;

                                    }
                                }
                            }
                        }
                    }
                }

            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
    };

    private void MyLOG(String data) {
        Log.i(TAG, "MyLOG: " + data);
    }

    /**
     * 参数信息
     */
    public class Params{
        Gson gson = new Gson();
        public String type;   //配置类型
        public String info;   //配置信息

        public Params(String type, String info) {
            this.type = type;
            this.info = info;
        }

        @Override
        public String toString() {
            return "Params{" +
                    "type='" + type + '\'' +
                    ", info='" + info + '\'' +
                    '}';
        }
    }
}
