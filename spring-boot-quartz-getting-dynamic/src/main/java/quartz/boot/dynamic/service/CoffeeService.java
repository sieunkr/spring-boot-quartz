package quartz.boot.dynamic.service;

import static org.quartz.JobKey.jobKey;

import java.util.*;

import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import quartz.boot.dynamic.model.JobDescriptor;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CoffeeService {

    private final Scheduler scheduler;


    public JobDescriptor createJob(String group, JobDescriptor descriptor) {
        descriptor.setGroup(group);
        JobDetail jobDetail = descriptor.buildJobDetail();
        Set<Trigger> triggersForJob = descriptor.buildTriggers();
        try {
            scheduler.scheduleJob(jobDetail, triggersForJob, false);
        } catch (SchedulerException e) {
            throw new IllegalArgumentException(e.getLocalizedMessage());
        }
        return descriptor;
    }

    @Transactional(readOnly = true)
    public Collection<JobDescriptor> listJob(String group) {
        try{

            Collection<JobDescriptor> jobDescriptors = new ArrayList<>();

            //TODO:Stream 활용
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    /*
                    String jobName = jobKey.getName();
                    String jobGroup = jobKey.getGroup();

                    //get job's trigger
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    Date nextFireTime = triggers.get(0).getNextFireTime();

                    System.out.println("[jobName] : " + jobName + " [groupName] : "
                            + jobGroup + " - " + nextFireTime);

                    */

                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    if(Objects.nonNull(jobDetail)){
                        //optionals.add(Optional.of(JobDescriptor.buildDescriptor(jobDetail, scheduler.getTriggersOfJob(jobKey), scheduler.getTriggerState(TriggerKey.triggerKey(jobKey.getName(),group)))));
                        jobDescriptors.add((JobDescriptor.buildDescriptor(jobDetail, scheduler.getTriggersOfJob(jobKey), scheduler.getTriggerState(TriggerKey.triggerKey(jobKey.getName(),group)))));
                    }
                }
            }
            System.out.println("데이터 조회");
            return jobDescriptors;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getLocalizedMessage());
        }
    }


    @Transactional(readOnly = true)
    public Optional<JobDescriptor> findJob(String group, String name) {
        // @formatter:off
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey(name, group));
            if(Objects.nonNull(jobDetail))
                return Optional.of(
                        JobDescriptor.buildDescriptor(jobDetail,
                                scheduler.getTriggersOfJob(jobKey(name, group)), scheduler.getTriggerState(TriggerKey.triggerKey(name,group))));
        } catch (SchedulerException e) {
            log.error("Could not find job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
        }
        // @formatter:on
        log.warn("Could not find job with key - {}.{}", group, name);
        return Optional.empty();
    }

    public void updateJob(String group, String name, JobDescriptor descriptor) {
        try {
            JobDetail oldJobDetail = scheduler.getJobDetail(jobKey(name, group));
            if(Objects.nonNull(oldJobDetail)) {
                JobDataMap jobDataMap = oldJobDetail.getJobDataMap();
                jobDataMap.put("subject", descriptor.getSubject());
                jobDataMap.put("messageBody", descriptor.getMessageBody());
                jobDataMap.put("to", descriptor.getTo());
                jobDataMap.put("cc", descriptor.getCc());
                jobDataMap.put("bcc", descriptor.getBcc());
                JobBuilder jb = oldJobDetail.getJobBuilder();
                JobDetail newJobDetail = jb.usingJobData(jobDataMap).storeDurably().build();
                scheduler.addJob(newJobDetail, true);

                //TODO:Triggers, Trigger 트리거의 복수값으로 넘어오는 경우에 대한 확인 필요. 일단 배열의 0번째 값으로 셋팅하였으나 추후 개선 필요
                Set<Trigger> triggersForJob = descriptor.buildTriggers();
                scheduler.rescheduleJob(TriggerKey.triggerKey(name,group), (Trigger)descriptor.buildTriggers().toArray()[0]);

                log.info("Updated job with key - {}", newJobDetail.getKey());
                return;
            }
            log.warn("Could not find job with key - {}.{} to update", group, name);
        } catch (SchedulerException e) {
            log.error("Could not find job with key - {}.{} to update due to error - {}", group, name, e.getLocalizedMessage());
        }
    }

    public void deleteJob(String group, String name) {
        try {
            scheduler.deleteJob(jobKey(name, group));
            log.info("Deleted job with key - {}.{}", group, name);
        } catch (SchedulerException e) {
            log.error("Could not delete job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
        }
    }

    public void pauseJob(String group, String name) {
        try {
            scheduler.pauseJob(jobKey(name, group));
            log.info("Paused job with key - {}.{}", group, name);
        } catch (SchedulerException e) {
            log.error("Could not pause job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
        }
    }

    public void resumeJob(String group, String name) {
        try {
            scheduler.resumeJob(jobKey(name, group));
            log.info("Resumed job with key - {}.{}", group, name);
        } catch (SchedulerException e) {
            log.error("Could not resume job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
        }
    }

}
