package com.hyjf.cs.message.controller.hgreportdata.caijing;

import com.hyjf.common.util.GetDate;
import com.hyjf.cs.message.service.hgreportdata.caijing.ZeroOneCaiJingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Calendar;

/**
 * 零壹财经数据推送
 * @Author: yinhui
 * @Date: 2019/6/3 10:29
 * @Version 1.0
 */
@ApiIgnore
@RestController
@RequestMapping("/cs-message/zeroOneCaiJingController")
public class ZeroOneCaiJingController {

    @Autowired
    private ZeroOneCaiJingService zeroOneCaiJingService;

    /**
     * 零壹财经记录报送
     */
    @RequestMapping("/investRecordSub")
    public void investRecordSub(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = GetDate.date_sdf.format(cal.getTime());
        //借款记录报送
        zeroOneCaiJingService.borrowRecordSub(yesterday,yesterday);
        // 出借记录报送
        zeroOneCaiJingService.investRecordSub(yesterday,yesterday);
        // 提前还款报送
        zeroOneCaiJingService.advancedRepay(yesterday,yesterday);
    }

    /**
     * 历史数据记录报送
     */
    @RequestMapping("/historySub")
    public void historySub() {
        Integer time[] = {4,5,6,7,8,9,10,11,12,1,2,3,4,5,6,7};
        StringBuilder startDate = null;
        StringBuilder endDate = null;
        for (int i =0;i<time.length;i++) {
            startDate = new StringBuilder();
            endDate = new StringBuilder();

            if(i > 8){
                startDate.append("2019-").append(time[i]).append("-01");
                endDate.append("2019-").append(time[i]).append("-32");
            }else{
                startDate.append("2018-").append(time[i]).append("-01");
                endDate.append("2018-").append(time[i]).append("-32");
            }

            //借款记录报送
            zeroOneCaiJingService.borrowRecordSub(startDate.toString(),endDate.toString());
            // 出借记录报送
            zeroOneCaiJingService.investRecordSub(startDate.toString(),endDate.toString());
            // 提前还款报送
            zeroOneCaiJingService.advancedRepay(startDate.toString(),endDate.toString());
        }
    }
}
