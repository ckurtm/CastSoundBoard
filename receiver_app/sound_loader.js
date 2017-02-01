/**
 * Created by Kurt on 2017/01/31.
 *
 * This is a demo application meant to demonstrate how to create a custom chromecast receiver application making use of the web audio apis,
 * it is in no way production ready, but merely serves as a demo
 */
var context;
var soundLoader;
var bufferRain,bufferBirds,bufferThunder,bufferWolves; //buffers for the different audio clips loaded in memory
var loaded = false; //flag for determining if we can actually play an audio clip
var buffers; //the audio buffer list
var playingRain,playingBirds,playingThunder,playingWolves; //flags for determining if we're already playing an audio clip

/**this processes the incoming chromecast requests from the sender application. we dont send back and messages to the caller
 * you probably should change this to acknowledge processing of the request to the sender application
 * @param message
 */
function processMessage(message){
   if(message.action === 'play'){
       playSound(message.sound);
   }else if(message.action == 'stop'){
       stopSound(message.sound);
   }else{
       console.log("failed to decode message");
   }
}

/**
 * plays the requested audio clip
 * @param sound - the name of the sound clip to play
 */
function playSound(sound){
    console.log("playSound() [sound:"+sound+"]");
    if(loaded){
        show(sound,true);
        if(sound == 'rain' && !playingRain){
            playingRain = true;
            bufferRain = playBuffer(bufferRain,1);
        }else if(sound == 'birds'){
            playingBirds = true;
            bufferBirds = playBuffer(bufferBirds,0);
        }else if(sound == 'thunder'){
            playingThunder = true;
            bufferThunder = playBuffer(bufferThunder,2);
        }else if(sound == 'wolves'){
            playingWolves = true;
            bufferWolves = playBuffer(bufferWolves,3);
        }
    }
}

/**
 * starts playback of the requested buffer
 * @param buffer
 * @param index
 */
function playBuffer(buffer,index) {
    buffer = context.createBufferSource();
    buffer.connect(context.destination);
    buffer.buffer = buffers[index];
    buffer.loop = true;
    buffer.start();
    return buffer;
}

/**
 * handles indication on screen of which audio clips are playing
 */
function show(id,enable){
    var elem = document.getElementById(id);
    if(enable) {
        elem.style.color = "#000000";
    }else{
        elem.style.color = "#ddd";
    }
}

/**
 * this uses the Web Audio api to load multiple sounds into memory
 */
function stopSound(sound){
    console.log("stopSound() [sound:"+sound+"]");
    if(loaded){
        show(sound,false);
        if(sound == 'rain'){
            playingRain = false;
            bufferRain.stop();
        }else if(sound == 'birds'){
            playingBirds = false;
            bufferBirds.stop();
        }else if(sound == 'thunder'){
            playingThunder = false;
            bufferThunder.stop();
        }else if(sound == 'wolves'){
            playingWolves = false;
            bufferWolves.stop();
        }
    }
}

/**
 * load the 4 example sounds into memory
 */
function loadSounds() {
    console.log("finished loading...");
    window.AudioContext = window.AudioContext || window.webkitAudioContext;
    context = new AudioContext();

    soundLoader = new SoundLoader(
        context,
        [
            './audio/birds.mp3',
            './audio/rain.mp3',
            './audio/thunder.mp3',
            './audio/wolves.mp3',
        ],
        finishedLoading
    );

    soundLoader.load();
}

/**
 * creates the audio clip buffers after loading is completed.
 * @param bufferedSounds
 */
function finishedLoading(bufferedSounds) {
    console.log("finished loading...");
    buffers = bufferedSounds;
    loaded = true;

    bufferBirds = context.createBufferSource();
    bufferRain = context.createBufferSource();
    bufferThunder = context.createBufferSource();
    bufferWolves = context.createBufferSource();

    bufferBirds.loop = true;
    bufferRain.loop = true;
    bufferThunder.loop = true;
    bufferWolves.loop = true;


    bufferBirds.buffer = buffers[0];
    bufferRain.buffer = buffers[1];
    bufferThunder.buffer = buffers[2];
    bufferWolves.buffer = buffers[3];

    bufferBirds.connect(context.destination);
    bufferRain.connect(context.destination);
    bufferThunder.connect(context.destination);
    bufferWolves.connect(context.destination);

}


/**
 * utility class to help with loading the audio clips.
 * @param context
 * @param urlList
 * @param callback
 * @constructor
 */
function SoundLoader(context, urlList, callback) {
    this.context = context;
    this.urlList = urlList;
    this.onload = callback;
    this.bufferList = new Array();
    this.loadCount = 0;
}

SoundLoader.prototype.loadBuffer = function(url, index) {
    // Load buffer asynchronously
    var request = new XMLHttpRequest();
    request.open("GET", url, true);
    request.responseType = "arraybuffer";

    var loader = this;

    request.onload = function() {
        // Asynchronously decode the audio file data in request.response
        loader.context.decodeAudioData(
            request.response,
            function(buffer) {
                if (!buffer) {
                    alert('error decoding file data: ' + url);
                    return;
                }
                loader.bufferList[index] = buffer;
                if (++loader.loadCount == loader.urlList.length)
                    loader.onload(loader.bufferList);
            },
            function(error) {
                console.error('decodeAudioData error', error);
            }
        );
    }

    request.onerror = function() {
        alert('SoundLoader: XHR error');
    }

    request.send();
}

SoundLoader.prototype.load = function() {
    for (var i = 0; i < this.urlList.length; ++i)
        this.loadBuffer(this.urlList[i], i);
}