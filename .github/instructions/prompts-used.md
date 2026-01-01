<!-- This file has prompts used to generate instructions file.-->
# Sample prompts used to generate instructions.

Prompts are iterative. If we know what needs to be generated (the goal), we can keep modifying the prompts, to reach the goals.

Claude Sonnet is used to write the instructions.

First time, Claude asks,
Allo write to java21-upgrade-instructions.md?

Choose the option: Yes, and don't ask again.

## Assumptions
Assumptions in this instructions are:
1. The developers uses Macbook.
2. The default shell is zsh
3. curl is already installed
4. The organization uses Java jdk provided by Amazon. 

## Prompt to install and initialize sdkman tool.
Initial prompt to install sdkman software and the purpose of the instruction file, assumptions.

I want to write AI coding agent instructions to upgrade the application from java 17 to Java 21.

Help me to write instructions.

The first thing in the instructions to do is whether a tool "sdkman" is installed or not.

Whether the sdkman is installed or not can be identified by checking if there is a file ~/.sdkman/bin/sdkman-init.sh

If the file ~/.sdkman/bin/sdkman-init.sh is present, execute it.

If the file is not present, install sdkman tool.

Download the sdkman software from https://get.sdkman.io using curl.

If the file is present or newly downloaded, execute the following command. The following command adds a script function "sdk" to the PATH environment variable. The "sdk" command is the core, of installating Java version.

execute "source ~/.sdkman/bin/sdkmain-init.sh"

Search Internet, if you need clarifications.

Add instructions so far.




**Important Note**: The above instructions do not have a statement to say, use zsh. It is ambiguous to LLM. So it assumed to use bash. 

Then chat with Claude on whether zsh can be used.

### Iterative question
The generated instructions use bash, is it preferable to use zsh?

Do not make changes, I want to discuss the pros and cons.

Claude mentioned, SDKMAN is bash native. For installation of sdkman, we can use bash. Rather than getting compatibility issues.

The macbook uses zsh as the default shell, but let the sdkman be installed in bash as recommended.

### Iterative prompt - fix the problem that instructions assume that JDK 21 is not present in the machine
The instructions generated under "Step 2: Install Amazon Corretto Java 21" assumes that JDK must be installed.

The instructions does not take care of the condition, the SDKMAN already exists and JDK version also already exists.

Change the instructions such that check whether Amazon JDK version 21 already installed, if it is already installed skip the process of installing.

If the JDK version is not already present, modify instructions to find the latest version of amazon corretto jdk at the time time when the instruction file is executed and install the latest version.

### Iterative prompt - Use zsh instead of bash
Use zsh for any script written from step 2 onwards.
Change the scripts from bash to zsh.

### Iterative prompt - fix the issue found in the script claude created

Under section 2.1, the following script is present.

if sdk list java | grep -q "21\..*amzn.*>>>"; then
    echo "Amazon Corretto Java 21 is already installed and in use"
    JAVA21_ALREADY_INSTALLED=true
elif sdk list java | grep -q "21\..*amzn"; then
    echo "Amazon Corretto Java 21 is installed but not currently in use"
    JAVA21_ALREADY_INSTALLED=true

The script need not distinguish between whether a Java version is currently being used or not. We are interested only in whether it is installed locally or not.

The above if elif conditions can be merged into one.

Also, we are not interested in the available JDK versions. We want only the versions that are installed currently. To know the currently installed versions, use the following command:

if sdk list java | grep -v "local only" | grep "21\..*amzn"; then

### iterative prompt - Print clear feedback on actions taken

While generating the instructions, I want to provide general guidelines to Claude for 
Providing clear feedback about what action was taken in each step.

Add a few instructions to LLM to give clear feedback on actions being taken.

