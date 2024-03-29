# Cmp React Native Integration

# Table of Contents
- [Key Concepts](#key-concepts)
- [Android Integration](#android-integration)
- [Ios Integration](#ios-integration)

# Key Concepts
React Native is an open source framework for building Android and iOS applications, for this reason, we provided different
ways to integrate successfully our SDK into your cross-platform app.

# Android Integration
From the Android point of view, we can accomplish it using the soft integration or the classic integration.

[The Soft Integration](ANDROID_SOFT_INTEGRATION.md) offer a solution which allows the client app to write the Cmp SDK configuration 
into extra Activity letting the ReactNative main Activity run in isolation.

WIP - [The Classic Integration](ANDROID_CLASSIC_INTEGRATION.md) is the traditional way for integration our SDK using the ReactNative main Activity.

The Classic Integration offers better performance than the Soft Integration.

# Ios Integration
From the Ios side, the [integration](IOS_INTEGRATION.md) is performed using a custom ViewController which makes the process 
easy and straightforward.

