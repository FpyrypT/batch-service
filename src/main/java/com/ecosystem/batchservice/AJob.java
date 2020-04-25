package com.ecosystem.batchservice;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
public class AJob implements Job {

    @Autowired
    private AService aService;

    @Override
    public void execute(JobExecutionContext context) {
        aService.printTime();
    }
}
