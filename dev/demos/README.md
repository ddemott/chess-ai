# Developer demos & debug utilities

This folder contains ad-hoc demos, debug harnesses, and example test drivers used by contributors for manual debugging and exploration.

Important:
- These files are for local development only. They are not part of the main product and are not executed in CI by default.
- Demo code may contain `System.out.println` statements and is exempt from the pre-commit debug-print rule.

How to run demos locally
1. Build the project:
```bash
mvn -q -DskipTests package
```
2. Run a demo by invoking the class from the Java command line using the `target/classes` directory on the classpath. Example:
```bash
# Run the Debug Promotion demo
java -cp target/classes:dev/demos com.ddemott.chessai.dev.demos.DebugPromotion
```

Notes:
- Demo files may not be packaged or compiled automatically by Maven because they are not under `src/main/java`. If you want them on the classpath easily, you can move or reference them via `--class-path` or compile/run them separately.
- To move a demo into the main package for packaging or to make it a proper sample, consider creating a small `examples/` project/module that depends on `chessai`.
