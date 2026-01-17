# Lessons Learned


## The generated instructions have to be reviewed thoroughly

### Open Rewrite activeRecipes in build.gradle - Not a best practice 

The instructions added activeRecipe list to the build.gradle.
In the documentation of Open Rewrite came across the info that adding activeRecipes to the build.gradle is not a good practice.
Because the Java upgrade is one time job, there is no need to commit that change.
Also, usually developers may want to execute a particular recipe such as static code analysis more frequently.

When discussed that with AI agent, it gave solution. It is important to question and chat the approach or instructions given.

### The build fix loop instruction errors

The initial instructions generated in build/fix loop have quite some mistakes.

If there are errors that cann't be resolved by AI agent, the build fix loop could go into infinite loop.

There were no instructions on how LLM can identify the reason for compilation error and how it can resolve it.


### Writing instructions or script to identify current Java version is complex 
It may sound simple to find the version of Java from a Gradle configuration.
However, there are dozens of ways in which Java version can be set.

#### It is very complex to write code to write script to identify the current Java version and skip the upgrade if the version is Java 17

There are Dozens of ways how a Java applications can set Java version.
Some of them are documented in test-cases.md

Open Rewrite recipe upgradeJavato21

### Mix of zsh and bash scripts gave problem
The instruction file used both bash and zsh scripts.
The SDKMAN scripts are in bash and the others in zsh.

When the first script in bash has to be executed, got the following error:

"The terminal seems to be in an unusual state. Let me try to check the java version directly."

The execution stuck there.

Changed all script to be in bash.

### The Java installation asked a question in Terminal

Do you want java 21.0.9-amzn to be set as default? (Y/n)

Change the instruction so that it does not ask for confirmation.

### Gradlew version is 8.5 - It still went ahead to upgrade
Change the instruction.