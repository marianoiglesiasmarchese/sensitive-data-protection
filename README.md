# sensitive-data-protection

Sensitive data protection approach

In order to don't leave in logs sensitive data information, this could be a good approach avoiding human mistakes during
toString() overrides. 
-----------------------------------
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

LogEvent with parameters swap alterations:
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

16:27:07.356 [main] INFO  - Starting InfoApplicationKt using Java 13.0.5.1 on mariano with PID 63843 (/home/mariano/Documents/my projects/sensitive-data-protection/target/classes started by mariano in /home/mariano/Documents/my projects/sensitive-data-protection)
16:27:07.609 [main] INFO  - No active profile set, falling back to default profiles: default
16:27:08.410 [main] INFO  - Started InfoApplicationKt in 1.451 seconds (JVM running for 2.395)
16:27:08.418 [main] INFO  - Sensitive data protection --annotatedFields: AnnotatedFields(stringDate=22/03/1990, date=2021-05-24, dateWithPattern=2021-05-24, email=mariano@test.com, text=some long text, textWithMoreVisibility=some long text, number=1023812094710923, numberWithMoreVisibility=1023812094710923)
16:27:08.461 [main] INFO  - An INFO Message --[param1] --[param2] --AnnotatedFields(stringDate=22/03/1990, date=2021-05-24, dateWithPattern=2021-05-24, email=mariano@test.com, text=some long text, textWithMoreVisibility=some long text, number=1023812094710923, numberWithMoreVisibility=1023812094710923)
16:27:08.462 [main] WARN  - A WARN Message
16:27:08.462 [main] ERROR - An ERROR Message

Process finished with exit code 0
```
-------------------------------------------------------------------------------------------------------------------------------------------------------------------

LogEvent with parameters swap alterations and parameters printed:
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

17:15:17.227 [main] INFO  - Starting InfoApplicationKt using Java 13.0.5.1 on mariano with PID 153983 (/home/mariano/Documents/my projects/sensitive-data-protection/target/classes started by mariano in /home/mariano/Documents/my projects/sensitive-data-protection)
17:15:17.472 [main] INFO  - No active profile set, falling back to default profiles: default
17:15:18.291 [main] INFO  - Started InfoApplicationKt in 1.462 seconds (JVM running for 2.338)
[AnnotatedFields([date=2021-05-24, , dateWithPattern=2021-05-24, , email=mariano@test.com, , number=1023812094710923, , numberWithMoreVisibility=1023812094710923, , stringDate=22/03/1990, ])]
17:15:18.300 [main] INFO  - Sensitive data protection --annotatedFields: [AnnotatedFields([date=2021-05-24, , dateWithPattern=2021-05-24, , email=mariano@test.com, , number=1023812094710923, , numberWithMoreVisibility=1023812094710923, , stringDate=22/03/1990, ])]
17:15:18.352 [main] INFO  - An INFO Message --[param1] --[param2] --AnnotatedFields(stringDate=22/03/1990, date=2021-05-24, dateWithPattern=2021-05-24, email=mariano@test.com, text=some long text, textWithMoreVisibility=some long text, number=1023812094710923, numberWithMoreVisibility=1023812094710923)
17:15:18.353 [main] WARN  - A WARN Message
17:15:18.353 [main] ERROR - An ERROR Message

Process finished with exit code 0
```

Regular parameter after toString:
```
AnnotatedFields(stringDate=22/03/1990, date=2021-05-24, dateWithPattern=2021-05-24, email=mariano@test.com, text=some long text, textWithMoreVisibility=some long text, number=1023812094710923, numberWithMoreVisibility=1023812094710923)
```
Custom parameter after toString:
```
[AnnotatedFields([date=2021-05-24, , dateWithPattern=2021-05-24, , email=mariano@test.com, , number=1023812094710923, , numberWithMoreVisibility=1023812094710923, , stringDate=22/03/1990, ])]
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