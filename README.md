# Nashorn Sandbox

[![Build Status](https://travis-ci.org/javadelight/delight-nashorn-sandbox.svg?branch=master)](https://travis-ci.org/javadelight/delight-nashorn-sandbox)

A secure sandbox for executing JavaScript in Java apps using the [Nashorn](https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/nashorn/) engine.

Also see [Rhino Sandbox](https://github.com/javadelight/delight-rhino-sandbox).

Part of the [Java Delight Suite](https://github.com/javadelight/delight-main#java-delight-suite).

## Usage

The sandbox by default **blocks access to all** Java classes.

Classes, which should be used in JavaScript, must be explicitly allowed.

```java
NashornSandbox sandbox = NashornSandboxes.create();
     
sandbox.allow(File.class);
     
sandbox.eval("var File = Java.type('java.io.File'); File;")
```

The sandbox also allows limiting the CPU time of scripts. This allows terminating scripts which contain infinite loops and other problematic code.

```java
NashornSandbox sandbox = NashornSandboxes.create();
     
sandbox.setMaxCPUTime(100);
sandbox.setExecutor(Executors.newSingleThreadExecutor());
     
sandbox.eval("while (true) { };");
```

This code will raise a ScriptCPUAbuseException.


## Maven
    
    <dependency>
        <groupId>org.javadelight</groupId>
        <artifactId>delight-nashorn-sandbox</artifactId>
        <version>0.0.2</version>
    </dependency>
    
Find out latest version [here](http://modules.appjangle.com/delight-nashorn-sandbox/latest/project-summary.html).

Use [Java Delight Repository](https://github.com/javadelight/delight-main#maven-repository).
    
## Further Documentation

- [JavaDocs](http://modules.appjangle.com/delight-nashorn-sandbox/latest/apidocs/index.html)
- [All Maven Reports](http://modules.appjangle.com/delight-nashorn-sandbox/latest/project-reports.html)