package org.njcdc.confirmatory_laboratory.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import org.njcdc.confirmatory_laboratory.common.lang.Result;
import org.njcdc.confirmatory_laboratory.entity.DetectionRecord;
import org.njcdc.confirmatory_laboratory.entity.SampleBasicInfo;
import org.njcdc.confirmatory_laboratory.service.DetectionRecordService;
import org.njcdc.confirmatory_laboratory.service.SampleBasicInfoService;
import org.njcdc.confirmatory_laboratory.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
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

    @Autowired
    SampleBasicInfoService sampleBasicInfoService;

    @GetMapping("/getTestTime/{acceptanceNumber}")
    public Result getTestTime(@PathVariable(name = "acceptanceNumber") String acceptanceNumber) {
        return Result.success(detectionRecordsService.count(new QueryWrapper<DetectionRecord>().eq("acceptanceNumber", acceptanceNumber)));
    }

    @PostMapping("/save")
    public Result save(@RequestBody DetectionRecord detectionRecord) {

        SampleBasicInfo sampleBasicInfo = sampleBasicInfoService.getOne(new QueryWrapper<SampleBasicInfo>().eq("acceptanceNumber", detectionRecord.getAcceptanceNumber()));

        QueryWrapper<DetectionRecord> queryWrapper = new QueryWrapper<>();
        int testTime = detectionRecordsService.count(queryWrapper.eq("acceptanceNumber", detectionRecord.getAcceptanceNumber()));

        detectionRecord.setSequence(testTime + 1);
        Assert.isTrue(detectionRecordsService.save(detectionRecord), "保存失败");

        if (testTime == 0) {
            return Result.success("首次录入信息");
        }
        /*
         * 检测记录只有一条
         * 说明：当前只是从筛查实验导入数据，确证实验室还未进行任何检测
         * 本次保存的是确证实验室的第一次复检结果
         * 保存检测结果 并 更新样品状态
         *
         * */
        if (testTime == 1) {

            sampleBasicInfo.setCurrentState("第一次复检结果已录入");
//            取消了审核过程，“审核第一次复检结果”这一状态取消
//            sampleBasicInfo.setOperation("审核第一次复检结果");
//            sampleBasicInfo.setFlag("waitingForReview");

            sampleBasicInfo.setOperation("输入第二次复检结果");
            sampleBasicInfo.setFlag("waitingForTest");

            Assert.isTrue(sampleBasicInfoService.saveOrUpdate(sampleBasicInfo), "保存失败");

            return Result.success("保存成功", "保存成功");
        }

        /*
         * 检测记录有两条
         * 说明：本次保存的是确证实验室的第二次复检结果
         * 保存检测结果 并 更新样品状态
         *
         * */
        if (testTime == 2) {

            sampleBasicInfo.setCurrentState("第二次复检结果已录入");

            //            取消了审核过程，“审核第二次复检结果”这一状态取消
//            sampleBasicInfo.setOperation("审核第二次复检结果");
//            sampleBasicInfo.setFlag("waitingForReview");


            /*

            下面对下一步操作进行判断：
                确证检测 or 导出报表

             */
            String firstConclusion = detectionRecordsService.getOne(new QueryWrapper<DetectionRecord>().eq("acceptanceNumber", detectionRecord.getAcceptanceNumber()).eq("sequence", 2)).getConclusion();
            String secondConclusion = detectionRecord.getConclusion();

            if (firstConclusion.equals("HIV抗体阴性") && secondConclusion.equals("HIV抗体阴性")) {
                sampleBasicInfo.setOperation("导出筛查阴性报表");
                sampleBasicInfo.setFlag("waitingForOutput");
            } else {
                sampleBasicInfo.setOperation("输入确证检测结果");
                sampleBasicInfo.setFlag("waitingForTest");
            }

            Assert.isTrue(sampleBasicInfoService.saveOrUpdate(sampleBasicInfo), "保存失败");

            return Result.success("保存成功", "保存成功");
        }


        /*
         * 检测记录有三条
         * 说明：本次保存的是确证实验室的确证检测结果
         * 保存检测结果 并 更新样品状态
         *
         * */
        if (testTime == 3) {
            sampleBasicInfo.setCurrentState("确证检测结果已录入");

//            取消了审核过程，“审核确证检测结果”这一状态取消
//            sampleBasicInfo.setOperation("审核确证检测结果");
//            sampleBasicInfo.setFlag("waitingForReview");

            sampleBasicInfo.setOperation("导出确证报表");
            sampleBasicInfo.setFlag("waitingForOutput");

            Assert.isTrue(sampleBasicInfoService.saveOrUpdate(sampleBasicInfo), "保存失败");

            return Result.success("保存成功", "保存成功");
        }

        return Result.fail("检测次数有误");

    }

    @PostMapping("/update")
    public Result update(@RequestBody DetectionRecord detectionRecord){
        QueryWrapper<DetectionRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("acceptanceNumber", detectionRecord.getAcceptanceNumber());
        Assert.isTrue(detectionRecordsService.update(detectionRecord,queryWrapper),"修改失败");

        return Result.success("修改成功",null);
    }

    /*获取所有的检测记录*/
    @GetMapping("/getDetectionRecords/{acceptanceNumber}")
    public Result getDetectionRecords(@PathVariable(name = "acceptanceNumber") String acceptanceNumber) {

        return Result.success(detectionRecordsService.list(new QueryWrapper<DetectionRecord>().eq("acceptanceNumber", acceptanceNumber)));
    }

    /*获取后面三次发生在确证实验室的检测记录*/
    @GetMapping("/getOtherThreeDetectionRecords/{acceptanceNumber}")
    public Result getOtherThreeDetectionRecords(@PathVariable(name = "acceptanceNumber") String acceptanceNumber) {
        return Result.success(detectionRecordsService.list(new QueryWrapper<DetectionRecord>().eq("acceptanceNumber", acceptanceNumber).ne("sequence", 1)));
    }

    /*获取筛查实验室的筛查检测记录*/
    @GetMapping("/getFirstDetectionRecord/{acceptanceNumber}")
    public Result getFirstDetectionRecord(@PathVariable(name = "acceptanceNumber") String acceptanceNumber) {

        return Result.success(detectionRecordsService.getOne(new QueryWrapper<DetectionRecord>().eq("acceptanceNumber", acceptanceNumber).eq("sequence", 1)));
    }

//    取消审核检测结果这一流程
    /*@PostMapping("/inputReviewResult")
    public Result inputReviewResult(@RequestBody DetectionRecord detectionRecord) {

        SampleBasicInfo sampleBasicInfo = sampleBasicInfoService.getOne(new QueryWrapper<SampleBasicInfo>().eq("acceptanceNumber", detectionRecord.getAcceptanceNumber()));

        DetectionRecord detectionRecordNew = detectionRecordsService.getOne(new QueryWrapper<DetectionRecord>()
                .eq("acceptanceNumber", detectionRecord.getAcceptanceNumber())
                .eq("sequence", detectionRecord.getSequence()));

        detectionRecordNew.setConclusion(detectionRecord.getConclusion())
                .setReviewerName(detectionRecord.getReviewerName())
                .setReviewerAccountID(detectionRecord.getReviewerAccountID());

        System.out.println("=================================================================");
        Assert.isTrue(detectionRecordsService.saveOrUpdate(detectionRecordNew), "保存失败");

        *//*
     * 如果审核的是第一条检测记录
     *
     * *//*
        if (detectionRecord.getSequence() == 1)
            return Result.fail("错误");
        if (detectionRecord.getSequence() == 2) {
            sampleBasicInfo.setCurrentState("第一次复检结果已审核");
            sampleBasicInfo.setOperation("录入第二次复检结果");
            sampleBasicInfo.setFlag("waitingForTest");
            Assert.isTrue(sampleBasicInfoService.saveOrUpdate(sampleBasicInfo), "保存失败");

            return Result.success("");
        }
        if (detectionRecord.getSequence() == 3) {
            sampleBasicInfo.setCurrentState("第二次复检结果已审核");
            sampleBasicInfo.setOperation("录入确证检测结果");
            sampleBasicInfo.setFlag("waitingForTest");
            Assert.isTrue(sampleBasicInfoService.saveOrUpdate(sampleBasicInfo), "保存失败");

            return Result.success("");
        }
        if (detectionRecord.getSequence() == 4) {
            sampleBasicInfo.setCurrentState("确证检测结果已审核");
            sampleBasicInfo.setOperation("导出确证检测报告");
            sampleBasicInfo.setFlag("waitingForOutput");
            Assert.isTrue(sampleBasicInfoService.saveOrUpdate(sampleBasicInfo), "保存失败");

            return Result.success("");
        }
        return Result.fail("检测记录有误");

    }*/

    /*获取第sequence次的检测记录*/
    @GetMapping("/getDetectionRecord/{acceptanceNumber}/{sequence}")
    public Result getDetectionRecord(@PathVariable(name = "acceptanceNumber") String acceptanceNumber, @PathVariable(name = "sequence") int sequence) {

        System.out.println("/getDetectionRecord/{acceptanceNumber}/{sequence}");
        return Result.success(detectionRecordsService.getOne(new QueryWrapper<DetectionRecord>().eq("acceptanceNumber", acceptanceNumber).eq("sequence", sequence)));
    }

}
