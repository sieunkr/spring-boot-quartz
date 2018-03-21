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

```java
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
```

## 4. Trigger
Quartz(쿼츠)에서 사용하는 트리거의 종류는 아래와 같다. 

- Crontrigger
- SimpleTrigger

Crontrigger 에 대해서 정리한다. 


#### Cron 표현식(Expression)
Cron 표현식은 7개의 표현식으로 구성된 문자열이다. 각 단위는 공백으로 구분된다. 

1. 초
2. 분
3. 시
4. 일
5. 월
6. 요일
7. 연도

"  0 0 8 ? * SUN * "와 같이 표현되며, 이것은 매주 일요일 8시를 의미한다. 

> " 초 분 시 일 월 요일 연도 " 의 순서로 표현된다. 

요일은 SUN, MON, TUE, WED, THU, FRI, 그리고, SAT 등으로 표현 가능하지만, 숫자로도 가능하다. 
SUN 1 이고 SAT 이 7 이다.

#### Always
항상, 매번을 표현할 때는 와일드카드 (*) 로 표현한다.  "  0 0 8 ? * SUN * "  에서

5번째의 와일드카드(*) 표현은 매월을 의미한다.   

마찬가지로 제일 마지막의 와일드카드(*)는 매해 를 의미한다.

#### On
특정 숫자를 입력하면 그 숫자에 맞는 값이 설정된다. 예를 들어서, "  0 0 8 ? * SUN * " 에서 제일 앞에 0 은 0초를 의미한다. 만약에, 0초와 30초에 Cron 이 실행되도록 설정할려면, "  0,30 0 8 ? * SUN * " 이렇게 콤마(,) 를 통해서 표현할 수 있다. 

#### Between
값의 범위를 나타낼 때는 하이픈 (-) 으로 표현할 수 있다. 예를 들어서, 월요일 부터 수요일은 "MON-WED" 로 표현하면 된다.  8시부터 11시는 "8-11"의 형태로 표현한다. 

#### QuestionMark
물음표(?) 문자는 설정 값 없음 을 의미한다.  일, 요일 필드에서만 허용이 된다.  "  0 0 8 ? * SUN * " 에서는 매주 일요일이라는 요일 필드 값을 설정하였다. 매주 일요일이라는 가정을 정하였기 때문에, 몇일 이라는 표현은 필요 없기 때문에 ? 로 표현해야 한다. 매주 일요일이라는 표현에서 매월 1일 로 바꾼다면  " 0 0 8 1 * ? * "  로 표현가능하며 SUN 이라고 표현되었던 필드는 물음표(?) 로 변경이 된다. 

#### /
슬래쉬(/) 문자는 값의 증가 표현을 의미한다. 분 필드에 0/5 를 사용한다면 0분 부터 시작하여 매5분마다 를 의미한다. 이것은 콤마(,)로 표현하면 0,5,10,15,20,25,30,35,40,45,50,55 와 같다. 

#### L
L 문자는 일, 요일 필드에서만 허용이 된다.  일 필드에서는 매달 마지막 날을 의미하고, 요일 필드에서는 "7" 또는 "SAT" 를 의미한다. 하지만, L 이 특정 값의 뒤에 올경우에는 이달의 마지막 무슨 요일이 된다. 예를 들어서 7L 이면 이달의 마지막 토요일 을 표현한다. 

[참고 - http://www.quartz-scheduler.org/documentation/quartz-2.2.x/tutorials/tutorial-lesson-06.html](http://www.quartz-scheduler.org/documentation/quartz-2.2.x/tutorials/tutorial-lesson-06.html)









# spring-boot-quartz 샘플 소스

### spring-boot-quartz-getting-started
스프링 부트 쿼츠 연동 아주 심플한 예제

### spring-boot-quartz-getting-dynamic	
스프링 부트 쿼츠 연동 시, Job 을 동적으로 생성,제거 하는 로직 구현

### spring-boot-quartz-getting-dynamic-admin
스프링 부트 쿼츠 연동 시, Job 을 동적으로 생성,제거 하는 로직 구현 - 관리자화면
