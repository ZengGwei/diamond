package com.taobao.diamond.server.controller;

import com.taobao.diamond.server.model.MonitorDto;
import com.taobao.diamond.server.service.MysqlHAService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Description MysqlHAController
 * @Author fuyou
 * @Version 1.0
 * @Date 2025/2/4-2:14 下午-2025
 */
@Controller
@RequestMapping({"/mysqlha"})
public class MysqlHAController {
    @Autowired
    private MysqlHAService disasterRecoveryService;

    public MysqlHAController() {
    }

    @RequestMapping({"list.do"})
    public ModelAndView list() {
        ModelMap model = new ModelMap();
        List<MonitorDto> nodes = this.disasterRecoveryService.findMonitorList();
        model.addAttribute("list", nodes);
        return new ModelAndView("monitor/list", model);
    }

    @RequestMapping({"detail.do"})
    public ModelAndView detail(long configId) {
        ModelMap model = new ModelMap();
        MonitorDto dto = this.disasterRecoveryService.detailOfMonitor(configId);
        model.addAttribute("dto", dto);
        return new ModelAndView("monitor/detail", model);
    }
}
