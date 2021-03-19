package org.njcdc.confirmatory_laboratory.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.njcdc.confirmatory_laboratory.common.lang.Result;
import org.njcdc.confirmatory_laboratory.entity.DetectionRecord;
import org.njcdc.confirmatory_laboratory.service.DetectionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dingangui
 * @since 2021-03-18
 */
@RestController
@RequestMapping("/detectionRecord")
public class DetectionRecordController {

    @Autowired
    DetectionRecordService detectionRecordsService;
    @PostMapping("/save")
    public Result save(@RequestBody DetectionRecord detectionRecord){
        QueryWrapper<DetectionRecord> queryWrapper = new QueryWrapper<>();
        int testTime = detectionRecordsService.count(queryWrapper.eq("acceptanceNumber", detectionRecord.getAcceptanceNumber()));
        detectionRecord.setTestTime(testTime+1);
        detectionRecordsService.save(detectionRecord);
        return Result.success("保存成功",null);
    }

    /*获取所有的检测记录*/
    @GetMapping("/getDetectionRecords/{acceptanceNumber}")
    public Result getDetectionRecords(@PathVariable(name = "acceptanceNumber") String acceptanceNumber ){

        return Result.success(detectionRecordsService.list(new QueryWrapper<DetectionRecord>().eq("acceptanceNumber",acceptanceNumber)));
    }

    /*获取后面三次发生在确证实验室的检测记录*/
    @GetMapping("/getOtherThreeDetectionRecords/{acceptanceNumber}")
    public Result getOtherThreeDetectionRecords(@PathVariable(name = "acceptanceNumber") String acceptanceNumber ){
        return Result.success(detectionRecordsService.list(new QueryWrapper<DetectionRecord>().eq("acceptanceNumber",acceptanceNumber).ne("testTime",1)));
    }


    /*获取筛查实验室的筛查检测记录*/
    @GetMapping("/getFirstDetectionRecord/{acceptanceNumber}")
    public Result getFirstDetectionRecord(@PathVariable(name = "acceptanceNumber") String acceptanceNumber ){

        return Result.success(detectionRecordsService.getOne(new QueryWrapper<DetectionRecord>().eq("acceptanceNumber",acceptanceNumber).eq("testTime",1)));
    }

}
