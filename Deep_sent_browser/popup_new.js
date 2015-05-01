var socket = new WebSocket('ws://localhost:8080/events/');
var map;
var minBulletSize = 3;
var maxBulletSize = 70;
var min = Infinity;
var max = -Infinity;
var myPoints = [];
//var usright = [28, 81.6];
//var usleft = [47 , 122];

socket.onmessage = function (event) {
  console.log(event.data);
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
    var count = 0;
socket.onmessage = function (event) {
  console.log(event.data);
  count++;
  var myArray = event.data.split(" ");

  if(myArray[0]=="twmap:") {
    for(var i=1; i<myArray.length-1; i++) {
      var point = myArray[i].split(",");
      //point[0] = usleft[1] + Math.random()*(usright[1]-usleft[1]);
      //point[1] = usleft[0] + Math.random()*(usright[0]-usleft[0]);
      
      var obj = {"latitude" : point[0] , "longitude" : point[1] , "sentiment" : point[2]};
  //    if(count % 4)
  //    {
  //      var obj = {"latitude" :"\"" + p1 + "\"" , "longitude" : "\"" + p2 + "\"", "sentiment" : point[2]};
  //    }
      myPoints.push(obj);

       
        //if(myPoints.length==100) { 
          mymapfunc();
          //myPoints = [];
        //}
    }
  } else {
    for(var i=1; i<myArray.length; i++) { myArray[i] = parseInt(myArray[i]); }
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
  }
 // document.getElementById("someID").firstChild.nodeValue=myArray[1];

}

}

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

  map.addTitle("Sentiment Map of the World", 14);
  map.addTitle("source: Gapminder", 11);
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
        var size = 7;
        //var id = dataItem.code;

        dataProvider.images.push({
            type: "circle",
            width: size,
            height: size,
            color: getColor(dataItem.sentiment),//dataItem.color,
            longitude: dataItem.longitude,
            latitude: dataItem.latitude,
        });
        
    }
	map.dataProvider = dataProvider;

        map.write("chartdiv");

}
 AmCharts.ready(function () {
        mymapfunc();
      });
window.onload = function() {
  document.getElementById("register").onclick = register;
}
