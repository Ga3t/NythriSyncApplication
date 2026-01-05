package com.caliq.calorie_service.controller;


import com.caliq.calorie_service.models.response.ReportResponse;
import com.caliq.calorie_service.service.AnalyseService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/analyse")
public class AnalyseController {

   private AnalyseService analyseService;

    public AnalyseController(AnalyseService analyseService) {
       this.analyseService = analyseService;
    }

    @GetMapping("/reports")
    public ResponseEntity<ReportResponse> getReportInDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestHeader("X-User-ID") String userId) {

        return ResponseEntity.ok(analyseService.analyseforDateRange(startDate,endDate, Long.parseLong(userId)));
    }

    @GetMapping("/reportforweek")
    public ResponseEntity<ReportResponse> getReportForThisWeek(@RequestHeader("X-User-ID") String userId){

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().minusDays(6);

        return ResponseEntity.ok(analyseService.analyseforDateRange(startDate, endDate, Long.valueOf(userId)));
    }

}
