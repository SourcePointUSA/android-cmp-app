## Automation Framework


## Prerequisites and Setup

JAVA 1.8
- Install Java 

        https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html
And set environmental variables

On MAC:
- Set JAVA_HOME and $JAVA_HOME/bin in PATH variable on MAC OS

    export JAVA_HOME=/Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk/Contents/$

    export PATH=$PATH:$JAVA_HOME/bin

On Windows:

    https://docs.oracle.com/en/database/oracle/r-enterprise/1.5.1/oread/creating-and-modifying-environment-variables-on-windows.html#GUID-DD6F9982-60D5-48F6-8270-A27EC53807D0

Install Gradle

On MAC:
Install Homebrew

    https://docs.brew.sh/Installation

    $ ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"

Install Node & NPM

    https://www.npmjs.com/get-npm

Install Gradle

    $ brew install gradle

- Set the GRADLE_HOME and $GRADLE_HOME/bin in PATH variable on MAC OS

	export GRADLE=/usr/local/Cellar/gradle/6.5
	export PATH=$PATH:$GRADLE_HOME/bin

On Windows:

    https://gradle.org/install/
    
Install Android Studio

    https://developer.android.com/studio
On MAC:

- Set $ANDROID_HOME and update PATH variables as below on your machine.

    export ANDROID_HOME=/Users/*****/Library/Android/sdk
    export PATH=$ANDROID_HOME/platform-tools:$PATH
    export PATH=$ANDROID_HOME/tools:$PATH
    export PATH=$ANDROID_HOME/bin:$PATH
    export PATH=$ANDROID_HOME/platform-tools/adb:$PATH
    export PATH=$ANDROID_HOME/build-tools:$PATH
    
On Windows:

1. Right-click on ‘My Computer’ and select Properties. Go to Advanced system settings and select ‘Environmental Variables’ option.
2. Under the User Variable table, click New to open New User Variable dialog.
3. Put ANDROID_HOME as Variable name and provide the path of the SDK folder next to Variable value.
4. Click OK to close the dialog 
5. Go to the folder where SDK has been installed.
6. Inside the SDK folder look for ‘tools’ and ‘platform-tools’ folder.
7. Copy the path for both tools and platform-tools.
8. Open ‘Environmental Variables’ dialog box.
9. Go to System Variables table and locate the Path variable.
10. Edit the path variable from ‘Edit system Variables’ dialog box.
11. Add the ‘tools’ and platform-tools’ folder’s full path

Install Appium 

    $ sudo npm install -g appium@1.17.0

Install appium doctor

Appium Doctor which is used to see if the appium setup is correctly done or not. Run it and fix the issues as per that

    $ sudo npm install -g appium-doctor
    $ appium-doctor

How To Run Tests:


1. Launch required emulator or connect real device on which you want tests should run
2. Start appium server from terminal or command prompt (In next phase will add code to start and stop appium server from framework itself)

        $ appium

3. Get the code on your machine
4. Open androidDevice.json file (path: AutomationFramework/src/main/resources)
5. Edit and Save device details 
For running on emulator update only platformVersion as per the emulator which is launched

          "name": "emulator-5554",
          "deviceName": "emulator-5554",
          "platformName": "Android",
          "platformVersion": "10",				
          "automationName": "UIAutomator2",
          "packageName": "com.sourcepointccpa.app",
          "activity": "com.sourcepointccpa.app.ui.SplashScreenActivity",
          "app": "CCPA-MetaApp.apk”    			 

For running on real device update values for name, deviceName, platformVersion as per the connected device

        "name": "emulator-5554",				
        "deviceName": "emulator-5554",			
        "platformName": "Android",
        "platformVersion": "10",				
        "automationName": "UIAutomator2",
        "packageName": "com.sourcepointccpa.app",
        "activity": "com.sourcepointccpa.app.ui.SplashScreenActivity",
        "app": "CCPA-MetaApp.apk" 			

6.  Edit testing.xml, if device details like nam or deviceName updated in above file
7.  Add application under test .apk file under /src/main/resources folder
8. Build the JAR and run it from terminal 

Go to project root directory "AutomationFramework"

        $ gradle clean build
        $ java -jar build/libs/AutomationFramework-1.0-SNAPSHOT.jar

Allure Report

Install Allure Report library on your machine. Please follow below link to install it on MAC.
Similarly install allure-report installer on your respective machine.  

    https://docs.qameta.io/allure/#_installing_a_commandline
Once test execution is complete, allure-results directory gets generated. I assume you have already installed allure on your machine. If not, install it. If yes, run below command to see the report.

        $ allure serve <allure-results path>




