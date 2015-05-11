# ocMoblieApp
Mobile App

## Brief description

This prototype demonstrates the use of WebRTC to develop 
online video conference for multiple devices, including 
browsers and Android devices. There are two parts to 
support this functionality, nodejs server(folder server_code)
and mobile application (folder OpenOC).


## Deployment

**nodejs server:** use following commands to get it running.
npm install
node server.js


**Mobile App:**
Since we have hardcode the server ip in the code, please change 
it to your nodejs server ip in following files, and get the 
mobile app compiled with Android Studio IDE:

**File:** OpenOC\app\src\main\java\android\com\openoc\ActivityMycourse.java

```javascript  
@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_mycourse);

  final String url = "ws://52.11.111.157:3000";  //Change the IP address and port to your own server IP and port  
```

