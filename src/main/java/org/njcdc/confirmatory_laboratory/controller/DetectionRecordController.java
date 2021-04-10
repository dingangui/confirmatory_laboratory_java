package org.njcdc.confirmatory_laboratory.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.njcdc.confirmatory_laboratory.common.lang.Result;
import org.njcdc.confirmatory_laboratory.entity.DetectionRecord;
import org.njcdc.confirmatory_laboratory.entity.SampleBasicInfo;
import org.njcdc.confirmatory_laboratory.service.DetectionRecordService;
import org.njcdc.confirmatory_laboratory.service.SampleBasicInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;

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

        SampleBasicInfo sampleBasicInfo = sampleBasicInfoService.getOne(
                new QueryWrapper<SampleBasicInfo>()
                        .eq("acceptanceNumber", detectionRecord.getAcceptanceNumber()));

        QueryWrapper<DetectionRecord> queryWrapper = new QueryWrapper<>();

        // 测试次数，表示样品已经进行了多少次的测试了
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
                确证检测 or 导出报告

             */
            String firstConclusion = detectionRecordsService.getOne(new QueryWrapper<DetectionRecord>().eq("acceptanceNumber", detectionRecord.getAcceptanceNumber()).eq("sequence", 2)).getConclusion();
            String secondConclusion = detectionRecord.getConclusion();

            // 两次都是阴性，则导出筛查阴性报告
            if (firstConclusion.equals("HIV抗体阴性") && secondConclusion.equals("HIV抗体阴性")) {
                sampleBasicInfo.setOperation("导出筛查阴性报告");
                sampleBasicInfo.setFlag("waitingForOutput");

                // 拼接筛查阴性报告编号
                // 计算本年度确证结果阴性的报告数量
                /*
                    方法：本年度样品基本基本信息中的operation为“导出筛查阴性报告”的数量
                    无论该样品信息是否删除都进行统计（避免重复）
                 */
                int year = Calendar.getInstance().get(Calendar.YEAR);

                int number = sampleBasicInfoService.count(new QueryWrapper<SampleBasicInfo>()
                        .eq("operation", "导出筛查阴性报告")
                        .like("acceptanceNumber", year));

                String reportNumber = "艾" + year + "-南京-阴-" + (number+1);
                sampleBasicInfo.setReportNumber(reportNumber);
            }
            // 存在一次阳性，继续进行确证检测
            else {
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
            sampleBasicInfo.setOperation("导出确证报告");
            sampleBasicInfo.setFlag("waitingForOutput");

            /*
            *   拼接报告编号
                筛查阴性报告编号示例：如：2019-1，即：年度-流水号，流水号5位，
                确证阴性报告编号示例：艾2019-南京-阴-1，2019是年度，1是流水
                确证阳性报告编号示例：艾2019-南京-1，2019是年度，1是流水
                结果不确定报告编号示例：艾2019-南京-疑-1，2019是年度，1是流水
             */
            int year = Calendar.getInstance().get(Calendar.YEAR);
            String confDetectionConclusion = detectionRecord.getConclusion();

            if (confDetectionConclusion.equals("HIV-1抗体不确定")) {

                // 计算本年度确证结果不确定的报告数量
                /*
                    方法：本年度样品的最后一次检测记录中结果为“HIV-1抗体不确定”
                 */
                int number = detectionRecordsService.count(
                        new QueryWrapper<DetectionRecord>()
                                .eq("sequence", 4)
                                .eq("conclusion", "HIV-1抗体不确定")
                                .like("acceptanceNumber", year));
                String reportNumber = "艾" + year + "-南京-疑-" + number;
                sampleBasicInfo.setReportNumber(reportNumber);

            }
            if (confDetectionConclusion.equals("HIV-1抗体阳性")) {

                // 计算本年度确证结果阳性的报告数量
                int number = detectionRecordsService.count(new QueryWrapper<DetectionRecord>()
                        .eq("sequence", 4)
                        .eq("conclusion", "HIV-1抗体阳性")
                        .like("acceptanceNumber", year));
                String reportNumber = "艾" + year + "-南京-" + number;
                sampleBasicInfo.setReportNumber(reportNumber);
            }
            if (confDetectionConclusion.equals("HIV抗体阴性")) {

                // 计算本年度确证结果阴性的报告数量
                int number = detectionRecordsService.count(new QueryWrapper<DetectionRecord>()
                        .eq("sequence", 4)
                        .eq("conclusion", "HIV抗体阴性")
                        .like("acceptanceNumber", year));

                String reportNumber = "艾" + year + "-南京-阴-" + number;
                sampleBasicInfo.setReportNumber(reportNumber);
            }

            Assert.isTrue(sampleBasicInfoService.saveOrUpdate(sampleBasicInfo), "保存失败");
            return Result.success("保存成功", "保存成功");
        }

        return Result.fail("检测次数有误");

    }

    @PostMapping("/update")
    public Result update(@RequestBody DetectionRecord detectionRecord) {
        QueryWrapper<DetectionRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("acceptanceNumber", detectionRecord.getAcceptanceNumber()).eq("sequence", detectionRecord.getSequence());
        Assert.isTrue(detectionRecordsService.update(detectionRecord, queryWrapper), "修改失败");

        return Result.success("修改成功", null);
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
