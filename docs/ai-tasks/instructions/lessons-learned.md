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


