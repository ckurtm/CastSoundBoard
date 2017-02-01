# Demo Chromecast SoundBoard

This is a demo application meant to demonstrate how to create a custom Chromecast receiver && matching sender application. The receiver application makes use of the web audio apis, to play multiple sound tracks.
The Android sender application merely sends requests to play/stop an audio clip on the soundboard.
This implementation is in no way production ready and only serves as a demonstration for creating a custom cast receiver application.

## Dependencies
* Get a Chromecast device and get it set up for development: https://developers.google.com/cast/docs/developers#Get_started
* Registered cast play store account. (you can register for one [here](https://cast.google.com/publish/) )


## Setup Instructions
* Just check out the code from GitHub, host all the content in **receiver_app** folder on your own server
* Register an application on the Developers Console (http://cast.google.com/publish). Select the Custom Receiver option and specify the URL to where you are hosting the index.html file. You will get an App ID when you finish registering your application.
* Insert your App ID in the strings.xml file of the android project (look for app_id in that file)
* Run the Android app and you should be able to see a similar app load on your cast device as show in video below.

## DEMO VIDEO
[![demo video running app on android device and connecting to Nvidia Shield android Tv](http://img.youtube.com/vi/T-RBbU8QHahs8/0.jpg)](https://youtu.be/RBbU8QHahs8)


## Bugs & Pull Requests
* If you find any issues, please open a bug here on GitHub


