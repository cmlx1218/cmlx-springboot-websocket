package com.cmlx.springboot.websocket.controller;

import com.cmlx.springboot.websocket.server.WebSocketServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-12-11 0011 17:24
 */
@Controller
@RequestMapping("/checkcenter")
public class CheckCenterController {

    //页面请求
    @GetMapping("/socket/{cid}")
    public ModelAndView socket(@PathVariable String cid) {
        ModelAndView mav = new ModelAndView("/socket");
        mav.addObject("cid", cid);
        return mav;
    }

    //推送数据接口
    @ResponseBody
    @RequestMapping("/socket/push/{cid}")
    public ModelAndView pushToWeb(@PathVariable String cid, String message) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            WebSocketServer.sendInfo(message, cid);
        } catch (IOException e) {
            e.printStackTrace();
            return modelAndView.addObject(cid + "###" + e.getMessage());
        }
        return modelAndView.addObject("cid", cid);
    }
}
