package com.hrtxn.ringtone.project.threenets.threenet.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.config.systemConfig.RingtoneConfig;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import com.hrtxn.ringtone.project.threenets.threenet.service.ThreeNetsOrderService;
import com.hrtxn.ringtone.project.threenets.threenet.service.ThreeNetsRingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Author:lile
 * Date:2019/7/11 15:44
 * Description:铃音控制器
 */
@Slf4j
@Controller
public class ThreeNetsRingController {

    @Autowired
    private ThreeNetsRingService threeNetsRingService;
    @Autowired
    private ThreeNetsOrderService threeNetsOrderService;

    /**
     * 进入铃音列表
     *
     * @return
     */
    @GetMapping("/threenets/toMerchantsRingPage/{orderId}")
    public String toMerchantsChildPage(ModelMap map, @PathVariable Integer orderId){
        try {
            ThreenetsOrder order = threeNetsOrderService.getById(orderId);
            map.put("orderId", orderId);
            map.put("companyName", order.getCompanyName());
            // 根据父级ID获取铃音运营商
            AjaxResult  ajaxResult = threeNetsRingService.getRingOperate(orderId);
            if (StringUtils.isNotNull(ajaxResult.get("data"))){
                map.put("ringOperate",ajaxResult.get("data"));
            }
        } catch (Exception e) {
            log.error("进入铃音列表 方法：toMerchantsChildPage 错误信息",e);
        }
        return "threenets/threenet/merchants/ring_list";
    }

    /**
     * 获取铃音数据
     *
     * @param page
     * @param request
     * @return
     */
    @PostMapping("/threenets/getThreeNetsRingList")
    @ResponseBody
    public AjaxResult getThreeNetsRingList(Page page, BaseRequest request) {
        try {
            List<ThreenetsRing> list = threeNetsRingService.getChildOrderList(page, request);
            int totalCount = threeNetsRingService.getCount(request);
            return AjaxResult.success(list, "查询成功", totalCount);
        } catch (Exception e) {
            log.error("获取铃音列表数据 方法：getThreeNetsRingList 错误信息", e);
        }
        return null;
    }

    /**
     * 进入添加铃音页面
     * @return
     */
    @GetMapping("/threenets/toAddMerchantsRingPage")
    public String toAddMerchantsRingPage(ModelMap map,BaseRequest request){
        map.put("orderId", request.getOrderId());
        map.put("operate", request.getOperate());
        return "threenets/threenet/merchants/addring";
    }

    /**
     * 添加铃音
     *
     * @return
     */
    @PostMapping("/threenets/insterThreeNetsRing")
    @ResponseBody
    @Log(title = "添加铃音", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult insterThreeNetsRing(ThreenetsRing ring){
        try {
            threeNetsRingService.save(ring);
            return AjaxResult.success(ring,"保存成功");
        }catch (Exception e){
            log.error("添加铃音失败 方法：insterThreeNetsRing 错误信息", e);
            return AjaxResult.error("保存失败！");
        }
    }


    /**
     * 删除铃音
     *
     * @param id
     * @return
     */
    @ResponseBody
    @DeleteMapping("/threenets/deleteThreeNetsRing")
    @Log(title = "删除铃音", businessType = BusinessType.DELETE, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult deleteThreeNetsRing(Integer id) {
        return threeNetsRingService.delete(id);
    }

    /**
     * 复制铃音
     * @param id
     * @return
     */
    @PostMapping("/threenets/cloneRing/{id}")
    @ResponseBody
    public AjaxResult cloneRing(@PathVariable Integer id){
        try {
            threeNetsRingService.cloneRing(id);
            return AjaxResult.success(true,"克隆成功！");
        }catch (Exception e){
            log.error("下载铃音失败 方法：playRing 错误信息", e);
            return AjaxResult.error("克隆失败！");
        }
    }


    /**
     * 播放铃音
     * @param request
     * @param response
     * @param id
     * @throws IOException
     */
    @RequestMapping("/threenets/playRing/{id}")
    @ResponseBody
    public void playRing(HttpServletRequest request, HttpServletResponse response, @PathVariable Integer id) throws IOException {
        FileInputStream fileIs=null;
        try {
            ThreenetsRing ring = threeNetsRingService.getRing(id);
            fileIs = new FileInputStream(RingtoneConfig.getProfile()+ring.getRingWay());
            int i=fileIs.available(); //得到文件大小
            byte data[]=new byte[i];
            fileIs.read(data); //读数据
            //response.setContentType("image.png"); //设置返回的文件类型
            OutputStream outStream=response.getOutputStream(); //得到向客户端输出二进制数据的对象
            outStream.write(data); //输出数据
            outStream.flush();
            outStream.close();
            fileIs.close();
        } catch (Exception e) {
            log.error("播放铃音失败 方法：playRing 错误信息", e);
            return;
        }
    }

    /**
     * 下载铃音
     *
     * @param response
     * @param id
     */
    @RequestMapping("/threenets/downloadRing/{id}")
    public void downloadRing(HttpServletResponse response,@PathVariable Integer id){
        try {
            ThreenetsRing ring = threeNetsRingService.getRing(id);
            String fileName = ring.getRingWay().substring(ring.getRingWay().lastIndexOf("/")+1);
            fileName = ring.getRingWay().substring(ring.getRingWay().lastIndexOf("\\")+1);
            InputStream inStream = new FileInputStream(RingtoneConfig.getProfile()+ring.getRingWay());// 文件的存放路径
            // 设置输出的格式
            response.reset();
            response.setContentType("bin");
            response.addHeader("Content-Disposition", "attachment; filename=\"" + new String(fileName.getBytes("utf-8"),"ISO8859-1")+ "\"");
            // 循环取出流中的数据
            byte[] b = new byte[100];
            int len;
            while ((len = inStream.read(b)) > 0)
                 response.getOutputStream().write(b, 0, len);
             inStream.close();
        }catch (Exception e){
            log.error("下载铃音失败 方法：playRing 错误信息", e);
            return;
        }
    }
}
