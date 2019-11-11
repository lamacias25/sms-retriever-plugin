# Cordova plugin - OTP using SMS Retriever API
## Presentation

This plugin SMS Retriever API, you can perform SMS-based user verification in your Android app automatically, without requiring the user to manually type verification codes, and without requiring any extra app permissions.

## Features
- Generate hash key
- Start SMS Listener

## Installation
Add as a cordova dependency.

```bash
cordova plugin add https://github.com/Paulimjr/sms-retriever-plugin.git
```

## Using the plugin ##

### Namespace and API

All the functions described in this plugin reside in the `cordova.plugins.SmsRetrieverPlugin` namespace.

1 - First, you need to generate a hash key and put in your SMS Server; 

2 - Start the listener onDeviceReady method in your application.

```javascript
 
 <!-- Hash key string to determine which verification messages to send to your app
        After generating the hash key you need to create a message on your server that looks like the following:
        Your ExampleApp OTP code is '222222' (lenght code equals 6 digits)
        
        'FA+9qCX9VSu' -->

cordova.plugins.SmsRetrieverPlugin.generateHashKey(
    function(hashKey){console.log("hashKey: " + hashKey)},
    function(error){console.log("Error: " + error)}
)
```

```javascript

 <!-- Start the OTP listener to receive SMS with the OTP code extracted
        Example of result is: '222222' -->

document.addEventListener("deviceready", function(){ 
       ...
    cordova.plugins.SmsRetrieverPlugin.startSmsListener(
        function(code){console.log("Code: " + code)},
        function(error){console.log("Error: " + error)}
    });
)
```

Please note that **You need to generate a hash key and put in fill in your SMS server as specified at Android Documentation** https://developers.google.com/identity/sms-retriever/verify