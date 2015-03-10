var socket = new WebSocket('ws://localhost:8080/events/');

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

socket.onmessage = function (event) {
  console.log(event.data);
  var myArray = event.data.split(" ");
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
    
 // document.getElementById("someID").firstChild.nodeValue=myArray[1];

}

}

window.onload = function() {
  document.getElementById("register").onclick = register;
}
