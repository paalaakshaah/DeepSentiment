/*
	although ammap has methos like getAreaCenterLatitude and getAreaCenterLongitude,
	they are not suitable in quite a lot of cases as the center of some countries
	is even outside the country itself (like US, because of Alaska and Hawaii)
	That's why wehave the coordinates stored here
*/

var places = [];

var latlong = {};
/*latlong[0] = {"latitude":47, "longitude":-123};
latlong[1] = {"latitude":27, "longitude":-81};*/

var count=0;
var drawfreq = 0;
var socket = new WebSocket('ws://localhost:8080/events/');
var map;
var minBulletSize = 3;
var maxBulletSize = 70;
var min = Infinity;
var max = -Infinity;
var myPoints = [];

socket.onmessage = function (event) {
  console.log(event.data);
}

for(var i=0;i<places.length;i++) {
	//var ran = Math.floor(Math.Random()*4);
	myPoints.push({"ID" : places[i] , "sentiment" : "0.66"});
}

// get min and max values

var colorData = ["#ff0000","#ff8800","#ffff00","#88ff88","#00ff00"];
//var colorData = ["#eea638","#d8854f","#de4c4f","#de4c4f","#a7a737"];

function getColor(myArr) {
      	return colorData[myArr % 5];
    }

 // build map
 
 function mymapfunc() {
  	AmCharts.theme = AmCharts.themes.dark;
	map = new AmCharts.AmMap();
  	map.pathToImages = "http://www.amcharts.com/lib/3/images/";

	map.addTitle("Twitter Sentiment Map of the World", 14);
	//map.addTitle("source: Gapminder", 11);
	map.areasSettings = {
		unlistedAreasColor: "#000000",
		unlistedAreasAlpha: 0.1
	};
	map.imagesSettings.balloonText = "<span style='font-size:14px;'><b>[[title]]</b>: [[value]]</span>";

	var dataProvider = {
		mapVar: AmCharts.maps.worldLow,
		images: []
	}

    // create circle for each country
    for (var i = 0; i < myPoints.length; i++) {
        var dataItem = myPoints[i];
        var size = 7;//Math.sqrt(square / (Math.PI * 2));
        var id = dataItem.code;

        dataProvider.images.push({
            type: "circle",
            width: size,
            height: size,
            color: getColor(dataItem.sentiment),//dataItem.color,
            longitude: latlong[dataItem.ID].longitude,
            latitude: latlong[dataItem.ID].latitude
        });
    }
	map.dataProvider = dataProvider;

	map.write("chartdiv");
}
 
 function getlatitude()
 {

 	if(count%100<17){ //US
 		return 27 + Math.random()*(20);
 	} else if(count%100<29) { //Canada
 		return 47 + Math.random()*(20);
 	} else if(count%100<38) { //SAm1
 		return -15 + Math.random()*(15);
 	} else if(count%100<44) { //SAm2
 		return -45 + Math.random()*(20);
 	} else if(count%100<55) { //NAf
 		return 8 + Math.random()*(22);
 	} else if(count%100<61) { //SAf
 		return -30 + Math.random()*(35);
 	} else if(count%100<64) { //Russia
 		return 45 + Math.random()*(20);
 	} else if(count%100<68) { //SAs
 		return 23 + Math.random()*(20);
 	} else if(count%100<81) { //Europe
 		return 45 + Math.random()*(15);
 	} else if(count%100<90) { //Alaska
    return 60 + Math.random()*(10);
  } else { //Aus
 		return -32 + Math.random()*(12);
 	}
 }

 function getlongitude()
 {
 	if(count%100<17){ //US
 		return -123 + Math.random()*(40);
 	} else if(count%100<29) { //Canada
 		return -120 + Math.random()*(60);
 	} else if(count%100<38) { //SAm1
 		return -75 + Math.random()*(38);
 	} else if(count%100<44) { //SAm2
 		return -67 + Math.random()*(10);
 	} else if(count%100<55) { //NAf
 		return -15 + Math.random()*(60);
 	} else if(count%100<61) { //SAf
 		return 15 + Math.random()*(20);
 	} else if(count%100<64) { //Russia
 		return 30 + Math.random()*(105);
 	} else if(count%100<68) { //SAs
 		return 45 + Math.random()*(80);
 	} else if(count%100<81) { //Europe
 		return 0 + Math.random()*(45);
 	} else if(count%100<90) { //Alaska
    return -164 + Math.random()*(45);
  } else { //Aus
 		return 120 + Math.random()*(30);
 	}

 }


 function register() {
      function createCanvas(divName) {
      
      var div = document.getElementById(divName);
      var canvas = document.createElement('canvas');
      div.appendChild(canvas);
      if (typeof G_vmlCanvasManager != 'undefined') {
        canvas = G_vmlCanvasManager.initElement(canvas);
      } 
      var ctx = canvas.getContext("2d");
      return ctx;
    }
    
   // var ctx = createCanvas("graphDiv1");
    var c = document.getElementById("graphDiv1");
    var ctx = c.getContext("2d");
    
    var graph = new BarGraph(ctx);
    graph.maxValue = 30;
    graph.margin = 10;
    graph.width = 450;
    graph.height = 150;
    graph.colors = ["#49a0d8", "#d353a0", "#ffc527", "#df4c27", "#df4c27"];
    graph.xAxisLabelArr = ["Extreme-Ve", "-Ve", "Neutral", "+Ve", "Extreme+Ve"];
    graph.yAxisLabelArr = ["twitter"];

   // var ctx2 = createCanvas("graphDiv2");
    var c2 = document.getElementById("graphDiv2");
    var ctx2 = c2.getContext("2d");
    
    var graph2 = new BarGraph(ctx2);
    graph2.maxValue = 30;
    graph2.margin = 10;
    graph.width = 450;
    graph.height = 150;
    graph2.colors = ["#49a0d8", "#d353a0", "#ffc527", "#df4c27", "#df4c27"];
    graph2.xAxisLabelArr = ["Extreme-Ve", "-Ve", "Neutral", "+Ve", "Extreme+Ve"];
    graph2.yAxisLabelArr = ["Facebook"];
    //setInterval(function () {
    //  graph.update([Math.random() * 30, Math.random() * 30, Math.random() * 30, Math.random() * 30, Math.random() * 30]);
    //}, 1000);


  var senderId = document.getElementById("age_group").value;
  //chrome.gcm.register([senderId], registerCallback);

  // Prevent register button from being click again before the registration
  // finishes.
  //document.getElementById("register").disabled = true;


    var send_data =  senderId ;

    if(typeof socket === 'undefined') {

    var socket = new WebSocket('ws://localhost:8080/events/');
    setTimeout(function(){
      socket.send(send_data);
    }, 1000);

    }

    if(socket.readyState == socket.CLOSED) {
      //var socket = new WebSocket('ws://localhost:8080/events/');
      //setTimeout(function(){
        socket.send(send_data);
      //}, 1000);
        //console.log('WebSocket Error: socket closed' );
    }
    if(socket.readyState == socket.OPEN) {
      socket.send(send_data);
    }
    document.getElementById("age_group").style.display = 'none';
    document.getElementById("divtext").style.display = 'none';
  document.getElementById("register").style.display = 'none';
  var element = document.createElement('div');
    element.id = "someID";
    document.body.appendChild(element);

    element.appendChild(document.createTextNode
     ('Facebook   <p> gfh </p>      Twitter'));
    var d = new Date();
    var tfb = d.getTime();
    var ttwi = d.getTime();


socket.onmessage = function (event) {
  console.log(event.data);
  var myArray = event.data.split(" ");
  //mymapfunc();
  if(count>100) {
  		mymapfunc();
  		count=0;
  		places = [];
  		myPoints = [];
  	} 
  for(var i=1; i<myArray.length; i++) { 
  	myArray[i] = parseInt(myArray[i]);
  	latlong[count] = {"latitude": getlatitude(), "longitude": getlongitude()};
	places.push(count);
  	myPoints.push({"ID" : places[places.length-1], "sentiment" : myArray[i]});
  	count++;
  }

  if(myArray[0]=="fb:"){
    graph.update(myArray);
    console.log("fb_time: " + (tfb - d.getTime()));
    tfb = d.getTime();
  }

  if(myArray[0]=="tw:"){
    graph2.update(myArray);
    console.log("twit_time" + (ttwi - d.getTime()))
    ttwi = d.getTime();
  }
  /*drawfreq++;
  if(drawfreq == 10) {mymapfunc();
  		drawfreq=0;
  }*/
    
 // document.getElementById("someID").firstChild.nodeValue=myArray[1];

}

}

window.onload = function() {
  document.getElementById("register").onclick = register;
}

 AmCharts.ready(function () {
 	mymapfunc();
 });
