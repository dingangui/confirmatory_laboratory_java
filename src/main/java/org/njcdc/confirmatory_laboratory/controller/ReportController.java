package org.njcdc.confirmatory_laboratory.controller;

import org.njcdc.confirmatory_laboratory.common.lang.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
public class ReportController {


    @GetMapping("/confReportOutput")
    public Result confReportOutput(){
        return Result.success(null);
    }
}
