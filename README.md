[![](https://img.shields.io/badge/java-packagecloud.io-844fec.svg)](https://packagecloud.io/)

# testessentials

Some useful stuff around testing for TestNG and JUnit. 

## How to bind the packagecloud repository

```xml
    <repositories>
        <repository>
            <id>packagecloud-testessentials</id>
            <url>https://packagecloud.io/mlieshoff/testessentials/maven2</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
```

## Add dependency

to Gradle:
```groovy
    implementation group: 'testessentials', name: 'testessentials', version: '1.0.0'
```

to Maven:
```xml
    <dependency>
        <groupId>testessentials</groupId>
        <artifactId>testessentials</artifactId>
        <version>1.0.0</version>
    </dependency>
```

## Continuous Integration

https://github.com/mlieshoff/testessentials/actions

## Repository

https://packagecloud.io/mlieshoff/testessentials

## Logging

We are using SLF4j.
