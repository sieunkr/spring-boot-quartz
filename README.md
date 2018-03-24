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
쿼츠 스케쥴러 인터페이스에 대한 내용 정리한다. 기본적으로 아래와 같이 선언한 후 사용가능하다. 

```java
@RequiredArgsConstructor
public class Class{
	private final Scheduler scheduler;

}
```
아래와 같이 Field Inject 도 가능하지만 추천하지는 않는다.
```java
@Autowired
private Scheduler scheduler;
```

인터페이스에서 제공하는 메소드는 org.quartz 라이브러리에서 직접 확인 가능하다. 자주 사용하는 메소드만 간략하게 정리하였다.  사용은 scheduler.getJobGroupNames() 와 같이 사용하면 된다. 


**JobDetail getJobDetail**  
Job 을 조회한다. 

**List\<String\> getJobGroupNames()**   
Job 의 그룹 리스트를 조회

**List\<? extends Trigger\> getTriggersOfJob**  
Job에 등록된 트리거 리스트를 조회한다. 

**void scheduleJob**  
Job 생성한다. 만약 기존에 존재한다면 덮어쓸지에 대한 값을 파라미터(replace)로 받는다. 파라미터가 true 라면, 기존 Job 이 업데이트 된다. 

**void pauseJob**  
Job을 중지한다. pauseJob 메서드에서 해당 Job 에 연결되어있는 모든 트리거를 중지한다. 트리거에 대한 상태라기 보다는, 잡에 대한 상태로 이해했었는데, 실제로 필드값은 TriggerState 로 되어있고 각 트리거에 상태값이 저장되어있다. 아래는 라이브러리의 소스이다. 참고만 하자.
```java
public void pauseJob(final JobKey jobKey) throws JobPersistenceException {
        executeInLock(
            LOCK\_TRIGGER\_ACCESS,
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

**void resumeJob**  
중지 되었던, Job을 재시작한다. 


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

[참고-http://www.baeldung.com/spring-quartz-schedule](http://www.baeldung.com/spring-quartz-schedule)

## 5. Spring Boot 2.0 에서의 Quartz

위에 설명하였지만, 스프링 부트 2.0 에서부터 Quartz 스타터를 디펜던시 추가하여 간결하게 쿼츠를 연동할 수 있다.  org.springframework.boot.autoconfigure.quartz 패키지에서 QuartzProperties 클래스를 보자!! 상단에 @ConfigurationProperties("spring.quartz") 선언 된 것을 확인할 수 있다. 아래와 같이 Properties 설정 값은 해당 QuartzProperties 클래스에 주입이 될 것이다. 

```java
spring.quartz.job-store-type=jdbc
spring.quartz.jdbc.initialize-schema=always
```
그렇다면 QuartzProperties 클래스에서 제공하지 않는 다른 Properties 값은 어떻게 설정할 수 있을까? 예를 들어서 쿼츠 잡의 쓰레드풀 설정을 하고 싶다면? 예전  스프링 버전에서는 아래와 같이 컨피그 설정을 따로 했었다. 

```java
//예전 방법
@Configuration
public class QuartzConfig {
 
    @Value("${org.quartz.scheduler.instanceName}")
    private String instanceName;
    @Value("${org.quartz.scheduler.instanceId}")
    private String instanceId;
    @Value("${org.quartz.threadPool.threadCount}")
    private String threadCount;
    @Value("${job.startDelay}")
    private Long startDelay;
    @Value("${job.repeatInterval}")
    private Long repeatInterval;
 
    생략...
```
스프링 부트 2.0에서는 QuartzProperties 클래스에 properties 라는 필드가 있다. 커스텀하게 작성한 application.properties 파일에 쿼츠 설정 정보를 추가하면, 해당 설정 값이 QuartzProperties 클래스에 properties 주입되고,  QuartzAutoConfiguration 클래스 에서 SchedulerFactoryBean 을 생성하는 과정에서 아래와 같이 해당 속성값을 설정해 준다. 

```java
@Bean  
@ConditionalOnMissingBean  
public SchedulerFactoryBean quartzScheduler() {  
   SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();  
schedulerFactoryBean.setJobFactory(new AutowireCapableBeanJobFactory(  
      this.applicationContext.getAutowireCapableBeanFactory()));  
if (!this.properties.getProperties().isEmpty()) {  
   schedulerFactoryBean  
         .setQuartzProperties(asProperties(this.properties.getProperties()));  
}
      
생략..
```

*.setQuartzProperties(asProperties(this.properties.getProperties())); *

자, 그러면 가장 중요한 Properties 설정은 어떻게 하는가? prefix가 spring.quartz 이므로 아래 소스와 같이 spring.quartz 뒤에 붙여서 속성
값을 설정한다! 
```java
spring.quartz.properties.org.quartz.threadPool.threadCount=20
```
이렇게 추가하면 기존에 10개의 쓰레드로 동작되면 쿼츠 스케쥴러가 20개의 쓰레드에서 동작하게 된다. 해당 설정 말고 다른 설정값들도 동일하게 설정이 가능할 것이지만, 실제로 테스트는 못해봤다. 해당 방법이 옳은 방법인지에 대해서 명확하게 가이드가 없다. 

> 혹시라도  자세히 아시는 분이 있다면 피드백 부탁드립니다...

스프링 부트 2.0 에서의 Quartz 지원에 대해서는 레퍼런스 문서를 참고하자. 
[https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-quartz](https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-quartz)



----------




# spring-boot-quartz 샘플 소스

### spring-boot-quartz-getting-started
스프링 부트 쿼츠 연동 아주 심플한 예제

### spring-boot-quartz-getting-dynamic	
스프링 부트 쿼츠 연동 시, Job 을 동적으로 생성,제거 하는 로직 구현

### spring-boot-quartz-getting-dynamic-admin
스프링 부트 쿼츠 연동 시, Job 을 동적으로 생성,제거 하는 로직 구현 - 관리자화면
