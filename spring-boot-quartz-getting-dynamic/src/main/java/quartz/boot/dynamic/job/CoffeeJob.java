package quartz.boot.dynamic.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
public class CoffeeJob implements Job{

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        System.out.println(context.getJobDetail().getKey().toString());

        JobDataMap map = context.getMergedJobDataMap();
        sendCoffee(map);
        //log.info("Job completed");
    }

    @SuppressWarnings("unchecked")
    private void sendCoffee(JobDataMap map) {

        System.out.println("테스트");
    }

}
