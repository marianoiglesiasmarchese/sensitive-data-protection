# sensitive-data-protection
-----------------------------------
This project targets to those who had troubles making good code once 
they faced the management of sensitive data information 
within the Java/Kotlin application logs.

-----------------------------------
## How to use it?
1. Depending on your project configuration, add one of the following dependencies:
* Maven
```
<dependency>
  <groupId>com.sensitive</groupId>
  <artifactId>info</artifactId>
  <version>0.1.0</version>
</dependency>
```
* Grable
```
gradle dependency
```
2. Exclude spring-boot-starter-logging configuration dependency:
* Maven
```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter</artifactId>
  <exclusions>
    <exclusion>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-logging</artifactId>
    </exclusion>
  </exclusions>
</dependency>
```
* Gradle
```
gradle example
```
This is necessary because it will override the custom logging configuration defined as part of this project. 

3. Add the `@Sensitive` annotation to the class that you desire to add attributes obfuscation.


4. Use the desired annotations to obfuscate your sensitive attributes. The current set of available annotations is:

* `@HideDate`
  * Can be used on **LocalDateTime, Date, and String types**. It also allows you to configure the pattern used to print it. By default, it's configured by default with **MM/dd/yyyy** pattern.
  * ***Due to the use of regex and date patterns at the same time, this is the most time-consuming annotation. Please use it carefully.***   
* `@HideText`
  * Can be used on **Strings**. It allows us to configure the length of the word that won't be obfuscated. By default, it's configured as 2.
* `@HideEmail`
  * Can be used on **Strings** and will seek for the email shape, leaving visible par of it but not the full text.
* `@HideNumber`
  * Can be used on **Longs**. It allows us to configure the length of the number that won't be obfuscated. By default, it's configured up as 2.

5. Pass the object as argument to the logger to enable its capture. This is required due to the implementation of the library. You can find an example within in the project mentioned further. It should be as following:
```
 logger.info("Sensitive data protection --$logMessage: [{}]", testClass)
```
***That's it!*** here you will find a [test repository](https://github.com/marianoiglesiasmarchese/sensitive-data-protection-usage) with an example of use and a performance measurement.

-----------------------------------
## Constraints
* Only classes with `@Sensitive` and some sensitive field annotation will be obfuscated, if not the to string will be the default one.
* Only one sensitive annotation is allowed by field.
* `@Sensitive` should be through all the path of classes that want to be protected. You can find an example inspecting `Runner.kt` file.
* Send your object as an argument to the logger to make its capture possible for the library. You can find an example inspecting `Runner.kt` file.
-----------------------------------
## Known issues or pending tasks:
* [x] Sensitive fields annotations seems to be not detected.
* [x] Custom sensitive object has a toString result with a different order.
* [x] Check all the flow for the Custom sensitive object creation:
  * [x] inner class without @Sensitive annotation.
  * [x] inner class with @Sensitive annotation.
* [x] Code comments enhancement and code clean up.
* [x] Clean up classes and packages.
* [ ] See how to add this to maven/gradle repositories.
* [ ] See how to make this run within an app that uses it as dependency:
  * [x] Validate visibility of annotations.
  * [ ] Validate layout configuration functionality.
* [ ] Improve readme, describe:
  * [x] Define the scope of the project.
  * [x] How to use it.
  * [x] Performance constraints.
  * [ ] Licence details.
  * [ ] publish it in our networks (Github, Linkedin, Blog).
  * [ ] Add pay me a coffee.
  * [ ] Suggestion and reporting are always welcome!.
-----------------------------------
## Future developments and improvements:
* [ ] Improve performance (analyze KAPT to run code on compiling time)
  * [x] Project profiling to detect bottlenecks 
  * [ ] Custom appender should handle concurrent events
* [ ] Add support to more data types
  * [ ] Maps
  * [ ] Lists
* [ ] Add support to more standard formats
  * [ ] Urls
* [ ] Try to replace the .xml by a .properties
* [ ] Enhance ProtectedField static methods support
* [ ] Enhance ProtectedField configuration with annotation metadata
-----------------------------------
## Current state:
LogEvent with no alterations:
-----------------------------------
```
.   ____          _            __ _ _
/\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
\\/  ___)| |_)| | | | | || (_| |  ) ) ) )
'  |____| .__|_| |_|_| |_\__, | / / / /
=========|_|==============|___/=/_/_/_/
:: Spring Boot ::                (v2.4.5)

16:18:54.864 [main] INFO  - Starting InfoApplicationKt using Java 13.0.5.1 on mariano with PID 48979 (/home/mariano/Documents/my projects/sensitive-data-protection/target/classes started by mariano in /home/mariano/Documents/my projects/sensitive-data-protection)
16:18:55.087 [main] INFO  - No active profile set, falling back to default profiles: default
16:18:55.867 [main] INFO  - Started InfoApplicationKt in 1.398 seconds (JVM running for 2.328)
16:18:55.876 [main] INFO  - Sensitive data protection --annotatedFields: AnnotatedFields(stringDate=22/03/1990, date=2021-05-24, dateWithPattern=2021-05-24, email=mariano@test.com, text=some long text, textWithMoreVisibility=some long text, number=1023812094710923, numberWithMoreVisibility=1023812094710923)
16:18:55.918 [main] INFO  - An INFO Message --[param1] --[param2] --AnnotatedFields(stringDate=22/03/1990, date=2021-05-24, dateWithPattern=2021-05-24, email=mariano@test.com, text=some long text, textWithMoreVisibility=some long text, number=1023812094710923, numberWithMoreVisibility=1023812094710923)
16:18:55.919 [main] WARN  - A WARN Message
16:18:55.919 [main] ERROR - An ERROR Message

Process finished with exit code 0
```

LogEvent with message replaced by custom object:
-----------------------------------

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

22:06:01.324 [main] INFO  - Starting InfoApplicationKt using Java 13.0.5.1 on mariano with PID 392127 (/home/mariano/Documents/my projects/sensitive-data-protection/target/classes started by mariano in /home/mariano/Documents/my projects/sensitive-data-protection)
22:06:01.575 [main] INFO  - No active profile set, falling back to default profiles: default
22:06:02.300 [main] INFO  - Started InfoApplicationKt in 1.305 seconds (JVM running for 2.256)
22:06:02.307 [main] INFO  - Sensitive data protection --annotatedFields: [AnnotatedFields(stringDate=**/**/****, date=***LOCAL_DATE_OBFUSCATION_ERROR***, dateWithPattern=***LOCAL_DATE_OBFUSCATION_ERROR***, email=mari***@test.com, text=so** **** **xt, textWithMoreVisibility=so** **** **xt, number=102**********923, numberWithMoreVisibility=102**********923, annotatedInnerClass=AnnotatedInnerClass(someText=so** **** ****** ***** ***ss, map={}, list=[]), notAnnotatedInnerClass=NotAnnotatedInnerClass(text=not annotates inner class text, date=2021-05-30, double=30.0))]
22:06:02.329 [main] INFO  - An INFO Message --[param1] --[param2] --AnnotatedFields(stringDate=22/03/1990, date=2021-05-30, dateWithPattern=2021-05-30, email=mariano@test.com, text=some long text, textWithMoreVisibility=some long text, number=1023812094710923, numberWithMoreVisibility=1023812094710923, annotatedInnerClass=AnnotatedInnerClass(someText=some text within inner class, map={}, list=[]), notAnnotatedInnerClass=NotAnnotatedInnerClass(text=not annotates inner class text, date=2021-05-30, double=30.0))
22:06:02.337 [main] WARN  - A WARN Message
22:06:02.337 [main] ERROR - An ERROR Message

Process finished with exit code 0

```

---------------
## Possible log4j2 file approaches:
log4j2.xml
---------------
```
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="TRACE" xmlns:xi="http://www.w3.org/2001/XInclude" packages="com.sensitive.info.appender">
    <Appenders>
        <CustomAppender name="CustomAppender" />
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="CustomAppender"/>
        </Root>
    </Loggers>
</Configuration>
```
log4j2.properties
-----------------------
```
name=PropertiesConfig
appenders=customAppender
packages=com.sensitive.info.appender

appender.customAppender.type=CustomAppender
appender.customAppender.name=CUSTOMAPPENDER
appender.customAppender.filter.1.type=ThresholdFilter
appender.customAppender.filter.1.onMatch=ACCEPT
appender.customAppender.filter.1.level=INFO

rootLogger.level=INFO

loggers=appLogger

logger.appLogger.name=sensitive.data.protection
logger.appLogger.level=INFO
logger.appLogger.appenderRefs=customAppender
logger.appLogger.appenderRef.customAppender.ref=CUSTOMAPPENDER
``` 

-----------------------------------
## Performance
All the tests were performed with 100K coroutines running at once.
### original 
```
21:05:11.247 [main] INFO  - Sensitive data protection --nonAnnotatedFields --elapsedTime: [1.0628922399999998 seconds]
21:05:11.251 [main] INFO  - Sensitive data protection --annotatedFields --elapsedTime: [5.8533793020000005 seconds]
```
### improved without concurrent event logging
```
18:45:11.039 [main] INFO  - Sensitive data protection --warmingTime --elapsedTime: [1.342950412 seconds]
18:45:11.042 [main] INFO  - Sensitive data protection --nonAnnotatedFields --elapsedTime: [1.158640054 seconds]
18:45:11.042 [main] INFO  - Sensitive data protection --overriddenFields --elapsedTime: [2.008003098 seconds]
18:45:11.042 [main] INFO  - Sensitive data protection --annotatedFields --elapsedTime: [2.165369831 seconds]
```
### improved with concurrent event logging
```
```