/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.joget.marketplace;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppPluginUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.util.WorkflowUtil;

/**
 *
 * @author Lenovo
 */
public class TelegramMessageProcessTool extends DefaultApplicationPlugin{
    
    private final static String MESSAGE_PATH = "message/form/TelegramMessageProcessTool";
    @Override
    public String getName() {
        return "Telegram Message Process Tool";
    }

    @Override
    public String getVersion() {
        return "8.0.0";
    }

    @Override
    public String getDescription() {
        return AppPluginUtil.getMessage("org.joget.marketplace.TelegramMessageProcessTool.pluginDesc" , getClassName() , MESSAGE_PATH);
    }

    @Override
    public String getLabel() {
        return AppPluginUtil.getMessage("org.joget.marketplace.TelegramMessageProcessTool.pluginLabel", getClassName(), MESSAGE_PATH);
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClassName(), "/properties/form/telegramMessageProcessTool.json", null, true, MESSAGE_PATH);
    }

        
    @Override
    public Object execute(Map props){
        
        //The telegram url
        String url = "https://api.telegram.org/bot";
        
       //Get declared properties value 
        String botToken = getPropertyString("botToken");
        String userName = getPropertyString("userName");
        String groupId = getPropertyString("groupId");
        String message = getPropertyString("message");
        String header = getPropertyString("header");
//        String image = getPropertyString("image");
        String link = getPropertyString("link");
        String caption = getPropertyString("caption");
        String formDefId = getPropertyString("formDefId");
        
        String recordId = getPropertyString("recordId");
        String imgId = getPropertyString("imgId");
        
        //Added HTML tag to inserted Link 
        if(link!= null){
            message += "<a href=" + "\"" +  link + "\">" + link + "</a>";
        }
        
        //Encode Image File
        String encodedFileName = imgId;
        try {
            encodedFileName = URLEncoder.encode(imgId, "UTF8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException ex) {
            // ignore
        }        
        
        AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        HttpServletRequest request = WorkflowUtil.getHttpServletRequest();
        
        //The url appended
        String theUrl = url + botToken + "/" + "sendMessage?chat_id=" + groupId + "&" + "parse_mode=HTML&text=<b>" + header + "</b>%0A" + message + "%0ABy%0A<b>" + userName + "</b>";
                
        //Make connection request to url    
        try{
            URL result = new URL(theUrl);
            URLConnection conn = result.openConnection();
            conn.connect();
            InputStream is = new BufferedInputStream(conn.getInputStream());
            
//           if(image!=null){
            String picUrl = url + botToken + "/" + "sendPhoto?chat_id=" + groupId + "&" + "photo=" + "https://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/web/client/app/" + appDef.getAppId() + "/" + appDef.getVersion().toString() + "/" + "form/download/" + formDefId + "/" + recordId + "/" + encodedFileName + "." + "&" + "caption=" + caption;
            URL picResult = new URL(picUrl);
            URLConnection picConn = picResult.openConnection();
            picConn.connect();
            InputStream picIs = new BufferedInputStream(picConn.getInputStream());
//         }
        }catch(Exception e){
            LogUtil.error(this.getClassName(), e, "Fail to send Message");
        }
        
        return null;
    }
    
}   
