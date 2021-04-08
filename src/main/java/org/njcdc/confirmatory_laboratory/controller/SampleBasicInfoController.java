package org.njcdc.confirmatory_laboratory.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.njcdc.confirmatory_laboratory.common.lang.Result;
import org.njcdc.confirmatory_laboratory.entity.DetectionRecord;
import org.njcdc.confirmatory_laboratory.entity.SampleBasicInfo;
import org.njcdc.confirmatory_laboratory.service.DetectionRecordService;
import org.njcdc.confirmatory_laboratory.service.SampleBasicInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author dingangui
 * @since 2021-03-01
 */
@RestController
@RequestMapping("/sampleBasicInfo")
public class SampleBasicInfoController {

    @Autowired
    SampleBasicInfoService sampleBasicInfoService;
    @Autowired
    DetectionRecordService detectionRecordsService;

    @PostMapping("/save")
    public Result save(@Validated @RequestBody SampleBasicInfo sampleBasicInfo) {

        sampleBasicInfo.setCurrentState("筛查实验室结果已导入");
        sampleBasicInfo.setOperation("录入第一次复检结果");
        sampleBasicInfo.setFlag("waitingForTest");

        Assert.isTrue(sampleBasicInfoService.save(sampleBasicInfo), "保存失败");

        return Result.success("保存成功");

    }

    @GetMapping("/getAcceptanceNumber")
    public Result getAcceptanceNumber() {
//
//        Subject currentUser = SecurityUtils.getSubject();
//
//        currentUser.isAuthenticated();
//
//        currentUser.getPrincipal();
        Calendar cal = Calendar.getInstance();
        int num = sampleBasicInfoService.count();
        String acceptanceNumber = "A" + cal.get(Calendar.YEAR) + " - " + (num + 1);
        return Result.success(acceptanceNumber);
    }

    @GetMapping("/getSampleInfo/{acceptanceNumber}")
    public Result getSampleInfoByAcceptanceNumber(@PathVariable(name = "acceptanceNumber") String acceptanceNumber) {

        return Result.success(sampleBasicInfoService.getOne(new QueryWrapper<SampleBasicInfo>().eq("acceptanceNumber", acceptanceNumber)));
    }

    @PostMapping("/update")
    public Result update(@RequestBody SampleBasicInfo sampleBasicInfo) {
        QueryWrapper<SampleBasicInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("acceptanceNumber", sampleBasicInfo.getAcceptanceNumber());
        Assert.isTrue(sampleBasicInfoService.update(sampleBasicInfo, queryWrapper), "修改失败");

        return Result.success("修改成功", null);
    }

    @GetMapping("/getAllOperableSampleList")
    public Result getAllOperableSampleList() {
        return Result.success(sampleBasicInfoService.list(new QueryWrapper<SampleBasicInfo>().ne("currentState", "表格可导出")));
    }

    @GetMapping("/getAllReviewableSampleList")
    public Result getAllReviewableSampleList() {
        return Result.success(sampleBasicInfoService.list(new QueryWrapper<SampleBasicInfo>().eq("flag", "waitingForReview")));
    }

    @GetMapping("/getAllDetectableSampleList")
    public Result getAllDetectableSampleList() {
        return Result.success(sampleBasicInfoService.list(new QueryWrapper<SampleBasicInfo>().in("flag", "waitingForTest", "waitingForReview")));
    }

    @GetMapping("/getFlag/{acceptanceNumber}")
    public Result getFlag(@PathVariable String acceptanceNumber) {
        return Result.success(sampleBasicInfoService.getOne(new QueryWrapper<SampleBasicInfo>().eq("acceptanceNumber", acceptanceNumber)).getFlag());

    }

    @GetMapping("/delete/{acceptanceNumber}")
    public Result delete(@PathVariable String acceptanceNumber) {
        Assert.isTrue(sampleBasicInfoService.remove(new QueryWrapper<SampleBasicInfo>().eq("acceptanceNumber", acceptanceNumber)),"删除失败");
        Assert.isTrue(detectionRecordsService.remove(new QueryWrapper<DetectionRecord>().eq("acceptanceNumber", acceptanceNumber)),"删除失败");
        return Result.success("删除成功",null);
    }
}
