# sensitive-data-protection

Sensitive data protection approach

In order to don't leave in logs sensitive data information, this could be a good approach avoiding human mistakes during
toString() overrides. 
-----------------------------------
## Known issues or pending tasks:
* [x] Sensitive fields annotations seems to be not detected
* [x] Custom sensitive object has a toString result with a different order
* [x] Check all the flow for the Custom sensitive object creation:
  * [x] inner class without @Sensitive annotation
  * [x] inner class with @Sensitive annotation
* [x] Code comments enhancement and code clean up
* [x] Clean up classes and packages
* Try to replace the .xml by a .properties
* Check layout functionality
* See how to add this to maven/gradle repositories
* See how to make this run within an app that uses it as dependency
* Improve performance (analyze KAPT to run code on compiling time)
* Enhance ProtectedField static methods support
* Enhance ProtectedField configuration with annotation metadata

## Current state:
LogEvent with no alterations:
-----------------------------------
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
-------------------------------------------------------------------------------------------------------------------------------------------------------------------

LogEvent with message replaced by custom object:
-----------------------------------
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
## log4j2.xml
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
## log4j2.properties
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
## Constraints
* classes with @Sensitive and some sensitive field annotation will be obfuscated
* by field only one sensitive annotation is allowed 
* @Sensitive should be through all the path of classes that want to be protected