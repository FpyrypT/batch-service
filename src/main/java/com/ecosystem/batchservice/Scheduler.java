package com.ecosystem.batchservice;

import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobParametersNotFoundException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Date;
import java.time.Instant;
import java.util.Optional;

@Configuration
@EnableScheduling
public class Scheduler {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job firstBatchJob;

    @Autowired
    private JobOperator operator;

    @Autowired
    private JobExplorer jobs;

    @Scheduled(cron = "0 0/1 * * * *")
    public void start() {
        JobInstance lastInstance = jobs.getLastJobInstance("firstBatchJob");

        try {
        if (Optional.ofNullable(lastInstance).isEmpty()) {
            jobLauncher.run(firstBatchJob, new JobParameters());
        } else {
            operator.startNextInstance("firstBatchJob");
        }

//            jobLauncher.run(firstBatchJob, new JobParametersBuilder()
//                    .addDate("launchDate", Date.from(Instant.now()))
//                    .toJobParameters());
        } catch (JobExecutionAlreadyRunningException e) {
            e.printStackTrace();
        } catch (JobRestartException e) {
            e.printStackTrace();
        } catch (JobInstanceAlreadyCompleteException e) {
            e.printStackTrace();
        } catch (JobParametersInvalidException e) {
            e.printStackTrace();
        } catch (JobParametersNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchJobException e) {
            e.printStackTrace();
        }
    }

}
