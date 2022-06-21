# CmpReactNativeIntegration

## Setting up the development environment

### Installing dependencies
```shell
  brew install node
  brew install watchman
```

### Running your React Native application
#### Step 1:
```shell
  npx react-native start
```

#### Step 2:
```shell
  npx react-native run-ios # on ios
  npx react-native run-android # on android
```

## Run instructions for Android:
Have an Android emulator running (quickest way to get started), or a device connected.
```shell
cd CmpReactNativeIntegration && npx react-native run-android
```


## Run instructions for iOS:
#### Method 1
```shell
cd CmpReactNativeIntegration && npx react-native run-ios
```
  
or

#### Method 2
- Open `CmpReactNativeIntegration/ios/CmpReactNativeIntegration.xcworkspace` in Xcode or run ``xed -b ios``
- Hit the `Run` button

## Run instructions for macOS:
- See https://aka.ms/ReactNativeGuideMacOS for the latest up-to-date instructions.

## Common issues

#### Issue 1
``Could not find node. Make sure it is in bash PATH or set the NODE_BINARY environment variable.
   ``

Solution:

``ln -s $(which node) /usr/local/bin/node
``

#### Issue 2
``module map file '/Users/sourcepoint/Library/Developer/Xcode/DerivedData/IOSReact-hghqytqdwmcwyecyooaaajwpjrmq/Build/Products/Debug-iphonesimulator/YogaKit/YogaKit.modulemap' not found
``

Solution:

``open xxx.xcworkspace instead of xxx. xcodeproj``

#### Issue 3
Check all Java version installed on your Mac:

``/usr/libexec/java_home -V``

#### Issue 4
Setting up the ``JAVA_HOME`` env variable. There are 2 methods:
1. Set the variable in the ``.bash_profile``

```shell
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk<version>.jdk/Contents/Home
export PATH=$PATH:$JAVA_HOME/bin
```

and then reload from terminal

```shell
> source ~/.bash_profile
```

2. Set the JDK from the `gradle.properties` of the project:
```groovy
org.gradle.java.home=/Library/Java/JavaVirtualMachines/jdk<version>.jdk/Contents/Home
```





  
