# Nashorn Sandbox

A secure sandbox for executing JavaScript in Java apps using the [Nashorn](https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/nashorn/) engine.

## Usage

The sandbox by default blocks access to **all** Java classes.

Classes, which should be used in JavaScript, must be explicitly allowed.

     NashornSandbox sandbox = NashornSandboxes.create();
     sandbox.allow(File.class);
     

