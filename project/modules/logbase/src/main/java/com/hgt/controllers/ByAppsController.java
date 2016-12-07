package com.hgt.controllers;

import com.hgt.domain.DataResult;
import com.hgt.domain.AppsCodeCounts;
import com.hgt.mapper.StatsByAppMapper;
import com.hgt.utils.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * README: 关于按应用查询的接口
 * Created by Yao on 12/6/16.
 * =============================================================================
 * CHANGELOG:
 */
@RestController
public class ByAppsController {

    private final String BASE_URL = "logb/by/apps";

    @Autowired
    StatsByAppMapper statsByAppMapper;

    /**
     * 查询输入时间起点1小时内，按应用统计的日志数
     * @param strDate
     * @return
     */
    @RequestMapping(value = BASE_URL + "/pie/hour/{strDate}", method = RequestMethod.GET)
    public DataResult<List<AppsCodeCounts>> queryGeneralLogPieData(@PathVariable String strDate) {
        List<AppsCodeCounts> codeCountsList = new ArrayList<>();
        String dateInput = strDate;
        // System.out.println(dateInput);
        // TODO: 12/6/16 日期格式异常处理

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HashMap<String, Object> dateMap = new HashMap<String, Object>();
        String dateNow = simpleDateFormat.format(new Date());
        String startTime = "";
        if (dateInput == null || dateInput.trim().length() == 0) {
            dateMap.put("endTime", dateNow);
            startTime = DateHelper.operateDatetimeByHour(dateNow, -1);
        } else {
            dateMap.put("endTime", strDate);
            startTime = DateHelper.operateDatetimeByHour(strDate, -1);
        }
        dateMap.put("startTime", startTime);

        List<AppsCodeCounts> queryResults = statsByAppMapper.selectAllByTimePeriod(dateMap);

        //region 只显示前8个应用的日志百分比，其他应用作为一个整体显示
        int resultSize = queryResults.size();

        //设置显示的应用数
        int primaryApp = 8;

        if (resultSize > primaryApp) {

            for (int n = 0; n < primaryApp; n++) {
                codeCountsList.add(queryResults.get(n));
            }

            AppsCodeCounts other = new AppsCodeCounts();
            other.setAppCode("其他系统");
            int otherCounts = 0;
            for (int i = primaryApp; i < resultSize; i++) {
                otherCounts += Integer.parseInt(queryResults.get(i).getCounts());
            }
            other.setCounts(otherCounts + "");

            codeCountsList.add(other);
        } else {
            codeCountsList = queryResults;
        }
        //endregion

        return new DataResult<>(codeCountsList);

    }


    /**
     * 输入时间起点查询24小时内，按小时统计的日志总数
     * @param strDate
     * @return
     */
    @RequestMapping(value = BASE_URL + "/pie/hour/{strDate}", method = RequestMethod.GET)
    public DataResult<List<AppsCodeCounts>> queryGeneralLogTotal(@PathVariable String strDate){
        List<AppsCodeCounts> codeCountsList = new ArrayList<>();
        String dateInput = strDate;
        // System.out.println(dateInput);
        // TODO: 12/6/16 日期格式异常处理

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HashMap<String, Object> dateMap = new HashMap<String, Object>();
        String dateNow = simpleDateFormat.format(new Date());
        String startTime = "";
        if (dateInput == null || dateInput.trim().length() == 0) {
            dateMap.put("endTime", dateNow);
            startTime = DateHelper.operateDatetimeByHour(dateNow, -1);
        } else {
            dateMap.put("endTime", strDate);
            startTime = DateHelper.operateDatetimeByHour(strDate, -1);
        }
        dateMap.put("startTime", startTime);

        List<AppsCodeCounts> queryResults = statsByAppMapper.selectAllByTimePeriod(dateMap);

        return new DataResult<>(codeCountsList);

    }

}
