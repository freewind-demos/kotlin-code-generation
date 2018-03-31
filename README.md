Kotlin Code Generation
=====================

This project is just copied from <https://github.com/JetBrains/kotlin-examples/tree/master/gradle/kotlin-code-generation> with some modifications.

Directories and files:

- `annotation-processor`: definition of our custom `TestAnnotation`
- `example`: Use and test the `TestAnnotation`. See `example/build.gradle` for configuration of annotations

Run:

```
./gradlew clean test
```

You will see logs from `annotation-processor/src/main/java/TestAnnotationProcessor.kt` from the generated log file: `./kotlin.log`