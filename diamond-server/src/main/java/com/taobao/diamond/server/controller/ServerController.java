package com.taobao.diamond.server.controller;


import com.taobao.diamond.domain.ConfigInfo;
import com.taobao.diamond.server.service.ConfigService;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/server.do"})
public class ServerController {
    private static final Log log = LogFactory.getLog(ServerController.class);
    @Autowired
    private ConfigService configService;

    public ServerController() {
    }

    @RequestMapping(
            params = {"method=updateConfig"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public void updateConfig(HttpServletRequest request, HttpServletResponse response, @RequestParam("dataId") String dataId, @RequestParam("group") String group, @RequestParam("content") String content, @RequestParam("historyMemo") String historyMemo) throws UnsupportedEncodingException {
        response.setCharacterEncoding("UTF-8");
        String ip = request.getRemoteAddr();
        String host = request.getRemoteHost();
        log.info(">>>dataId=" + dataId + ",ip=" + ip + ",host=" + host + ",content=" + content);
        ConfigInfo configInfo = new ConfigInfo(dataId, group, content);
        this.configService.updateConfigInfo(dataId, group, content, historyMemo);
        this.output(response, configInfo.getContent());
    }

    @RequestMapping(
            params = {"method=getConfigMd5"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public void getConfigMd5(HttpServletRequest request, HttpServletResponse response, @RequestParam("dataId") String dataId, @RequestParam("group") String group) throws UnsupportedEncodingException {
        response.setCharacterEncoding("UTF-8");
        String ip = request.getRemoteAddr();
        String host = request.getRemoteHost();
        log.info(">>>dataId=" + dataId + ",ip=" + ip + ",host=" + host);
        ConfigInfo configInfo = this.configService.findConfigInfo(dataId, group);
        this.output(response, configInfo != null ? configInfo.getMd5() : "");
    }

    private void output(HttpServletResponse response, String content) {
        response.setContentType("text/plain;charset=UTF-8");
        ServletOutputStream os = null;

        try {
            os = response.getOutputStream();
            os.write(content.getBytes("UTF-8"));
        } catch (Exception var13) {
            log.error("response exception", var13);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException var12) {
                    log.error("outputstream close exception", var12);
                }

                os = null;
            }

        }

    }

    public ConfigService getConfigService() {
        return this.configService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}
