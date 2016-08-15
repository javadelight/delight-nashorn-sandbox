# Nashorn Sandbox

A secure sandbox for executing JavaScript in Java apps using the [Nashorn](https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/nashorn/) engine.

Also see [Rhino Sandbox](https://github.com/javadelight/delight-rhino-sandbox).

Part of the [Java Delight Suite](https://github.com/javadelight/delight-main#java-delight-suite).

[![Build Status](https://travis-ci.org/javadelight/delight-nashorn-sandbox.svg?branch=master)](https://travis-ci.org/javadelight/delight-nashorn-sandbox)

## Usage

The sandbox by default **blocks access to all** Java classes.

Classes, which should be used in JavaScript, must be explicitly allowed.

```java
NashornSandbox sandbox = NashornSandboxes.create();
     
sandbox.allow(File.class);
     
sandbox.eval("var File = Java.type('java.io.File'); File;")
```

Or you can inject your java object as a JS global variable

```java
NashornSandboxes sandbox = NashornSandboxes.create();

sandbox.inject("fromJava", new Object());

sandbox.eval("fromJava.getClass();");
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
        <version>0.0.6</version>
    </dependency>
    
Find out latest version [here](http://modules.appjangle.com/delight-nashorn-sandbox/latest/project-summary.html).

Use [Java Delight Repository](https://github.com/javadelight/delight-main#maven-repository).

If you are looking for a JAR with all dependencies, you can download it from [here](https://github.com/javadelight/delight-nashorn-sandbox/releases).

## Contributors

[Eduardo Velasques](https://github.com/eduveks): API extensions to block/allow Rhino system functions; Capability to block/allow variables after Sandbox has been created. 
    
## Further Documentation

- [JavaDocs](http://modules.appjangle.com/delight-nashorn-sandbox/latest/apidocs/index.html)
- [All Maven Reports](http://modules.appjangle.com/delight-nashorn-sandbox/latest/project-reports.html)