## Automation Framework

## Prerequisites Installations:

## JAVA 1.8
- Install Java
- Set JAVA_HOME path on your machine 
- Add $JAVA_HOME/bin in PATH variable.

## Node & NPM
- Download & install node. you can refer `https://nodejs.org/en/download/`

## Install Gradle
>> brew install gradle
- Set the GRADLE_HOME on your machine. 
- Add $GRADLE_HOME/bin in PATH variable.

## Android Studio Setup
- Install Android Studio  (https://developer.android.com/studio/?gclid=EAIaIQobChMI7cjtqeuL6gIVlgsrCh1zIAI2EAAYASAAEgIQKPD_BwE&gclsrc=aw.ds)
- Set $ANDROID_HOME and update PATH variables as below on your machine.
export ANDROID_HOME=/Users/*****/Library/Android/sdk
export PATH=$ANDROID_HOME/platform-tools:$PATH
export PATH=$ANDROID_HOME/tools:$PATH
export PATH=$ANDROID_HOME/bin:$PATH
export PATH=$ANDROID_HOME/platform-tools/adb:$PATH
export PATH=$ANDROID_HOME/build-tools:$PATH

## Appium Setup
Install appium
>>sudo npm install -g appium@1.17.0

## Install appium doctor
Appium Doctor which is used to see if the appium setup is correctly done or not. Run it and fix the issues as per that

 >>sudo npm install -g appium-doctor
 >>appium-doctor

## How To Run Tests:
1. Start required Emulator or connect real device 
2. Start appium from terminal (In next phase will add code to start and stop appium server from framework itself)
type following command
>>appium

wait till appium starts

3. Get the code on your machine
4. Set the iOS device details in androidDevice.json file in resources directory as shown below
- Edit and Save device details 
For running on Emulator
	"name": "emulator-5554",
    "deviceName": "emulator-5554",
    "platformName": "Android",
    "platformVersion": "10",				## update with required version
    "automationName": "UIAutomator2",
    "packageName": "com.sourcepointccpa.app",
    "activity": "com.sourcepointccpa.app.ui.SplashScreenActivity",
    "reset": false,
    "app": "CCPA-MetaApp.apk”     ## update if want to run with other app

For running on real device
	"name": "emulator-5554",		## replace with name of the real device
    "deviceName": "emulator-5554", ## replace with name of the real device
    "platformName": "Android",
    "platformVersion": "10",		## update with required version
    "automationName": "UIAutomator2",
    "packageName": "com.sourcepointccpa.app",
    "activity": "com.sourcepointccpa.app.ui.SplashScreenActivity",
    "reset": false,
    "app": "CCPA-MetaApp.apk" 	 ## update if want to run with other app

6. If device details (name/deviceName) updated in above file need to update model parameter from testng.xml file 
7. Add .apk file under /src/main/resources folder

8. Build the JAR and run it 
From terminal go to project root directory "AutomationFramework"
>>gradle clean build
>>java -jar build/libs/AutomationFramework-1.0-SNAPSHOT.jar


