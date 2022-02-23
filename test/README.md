# test

Contains tests for verifying rest-api works as expected.

By default maven run tests with rest-api Docker container.

If needed to have more rapid development feedback loop then

1. use Caller instead of CallerWithDockerizedRestApi in [Tests.kt](src/test/kotlin/Tests.kt) 

        companion object {
            @JvmField
            @RegisterExtension
            var caller = CallerWithDockerizedRestApi()
            //var caller = Caller() // Uncomment if you already have rest-api running
        } 
1. Modify/start module rest-api with IDE which support dotnet apps.
1. Run [Tests.kt](src/test/kotlin/Tests.kt) with IDE which support Kotlin and JUnit5.