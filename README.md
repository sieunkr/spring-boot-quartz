# Quartz Scheduler With Spring Boot
Quartz(쿼츠) 와 스프링 부트의 연동 방법 대해서 정리한다.


## 1. 기본 개념 정리

**Job**
실행해야 할 작업, Job 인터페이스는 execute 메서드를 정의한다. execute 메서드의 파라미터인 JobExecutionContext 에는 트리거 핸들링, 스케쥴에 의한 핸들링 등을 포함하여 런타임 환경에 대한 정보를 제공한다.

```java
@Override  
public class CoffeeJob implements Job{

	public void execute(JobExecutionContext context) throws JobExecutionException {  
	  //잡 로직 수행
	}
```

**JobDetail**
Job을 실행하기 위한 상세 정보, JobBuilder 에 의해서 생성된다.

**JobBuilder**
JobBuilder는 빌더패턴으로 JobDetail 생성한다.
```java
public JobDetail buildJobDetail() {  
	JobDataMap jobDataMap = new JobDataMap(getData());  
	jobDataMap.put("subject", subject);  
	return newJob(CoffeeJob.class)  
            .withIdentity(getName(), getGroup())  
            .usingJobData(jobDataMap)  
            .build();  
}
```

**Trigger**
Job을 실행하기 위한 조건(작업 실행 주기, 횟수 등)
다수의 Trigger 는 동일한 Job을 공유하여 지정할 수 있지만, 하나의 Trigger는 반드시 하나의 Job을 지정해야 한다.

**TriggerBuilder**
TriggerBuilder 는 빌더패턴으로 트리거 객체를 생성한다.
```java
@Bean
public Trigger trigger(JobDetail job) {
    return TriggerBuilder.newTrigger().forJob(job)
      .withIdentity("Qrtz_Trigger")
      .withDescription("Sample trigger")      .withSchedule(simpleSchedule().repeatForever().withIntervalInHours(1))
      .build();
}
```

**Scheduler** 
SchedulerFactory에 의해 생성이 되는 서비스 핵심 객체, JobDetail 과 Trigger를 관리한다.

## 2. 설정 정보 

### 스프링 부트 디펜던시

**Gradle** 설정
```java
dependencies {  
   ...
   compile('org.springframework.boot:spring-boot-starter-quartz')  
   ...
}
```

**Maven** 설정
```java
생략...
```

### Job 정보 저장  
디폴트로 인메모리 기반으로 동작한다. 애플리케이션이 재시작하면 휘발성으로 데이터는 사라진다. DB 에 저장하기 위해서는 아래와 같이 Property 설정을 하고, Quartz 연동 데이터베이스에 테이블을 생성하면 된다. 

```java
//propery 설정
spring.quartz.job-store-type=jdbc
```
```java
//build.gradle 설정
compile("mysql:mysql-connector-java:5.1.34")  
compile('org.springframework.boot:spring-boot-starter-data-jpa')
```
[링크-테이블 생성 쿼리](https://github.com/quartz-scheduler/quartz/tree/master/quartz-core/src/main/resources/org/quartz/impl/jdbcjobstore) 

참고로, 테이블 생성은 반드시 버전에 맞게 생성해야 한다.  버전마다 스키마가 다를 수 있다.

> 자동으로 테이블 스키마가 생성되는 방법이 있을 것으로 추측(?)이 되나 정확한 방법은 아직 찾지 못하였다.

## 3. Scheduler
작성 중...




pauseJob 메서드에서
해당 Job 에 연결되어있는 모든 트리거를 중지함

트리거에 대한 상태라기 보다는, 
잡에 대한 상태라는 개념이 맞을 듯한데, 

실제로 필드값은 TriggerState 로 되어있어서 정확히 확인 필요

``java
public void pauseJob(final JobKey jobKey) throws JobPersistenceException {
        executeInLock(
            LOCK_TRIGGER_ACCESS,
            new VoidTransactionCallback() {
                public void executeVoid(Connection conn) throws JobPersistenceException {
                    List<OperableTrigger> triggers = getTriggersForJob(conn, jobKey);
                    for (OperableTrigger trigger: triggers) {
                        pauseTrigger(conn, trigger.getKey());
                    }
                }
            });
    }
``






# spring-boot-quartz 샘플 소스

### spring-boot-quartz-getting-started
스프링 부트 쿼츠 연동 아주 심플한 예제

### spring-boot-quartz-getting-dynamic	
스프링 부트 쿼츠 연동 시, Job 을 동적으로 생성,제거 하는 로직 구현

### spring-boot-quartz-getting-dynamic-admin
스프링 부트 쿼츠 연동 시, Job 을 동적으로 생성,제거 하는 로직 구현 - 관리자화면
