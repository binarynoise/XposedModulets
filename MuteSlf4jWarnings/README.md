# MuteSlf4jWarnings

Mute those annoying SLF4J warnings:

```log
System.err W SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
System.err W SLF4J: Defaulting to no-operation (NOP) logger implementation
System.err W SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```

Enable for all the modules that produce this unnecessary warning.

Especially useful for people reading logcat that need to see System.err but get confused by SLF4J warnings from other apps.
