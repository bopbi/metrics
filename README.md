## Initialize

1. disable the plugin declaration inside `build.gradle` on root and example/app
2. publish to local maven `./gradlew publishToMavenLocal`
3. re-enable the disabled plugin
4. sync gradle

Note:
1. gradlePlugin name dsl declaration need to have the same name as the name on Plugin class
