var sys = require("sys"),  
http = require("http"),
path = require("path")  
url = require("url"),  
filesys = require("fs"),
express = require("express"),
HotSpotHandler = require( "./HotSpotHandler.js" ),
HotSpotDAO = require('./HotSpotDAO.js'),
Responder = require( "./Responder.js" ),
bodyParser = require('body-parser'),
app = express(),
app.use(bodyParser.json()),
dbURL = "mongodb://localhost:27017/walkingtour";


// TourHandler = require( "./TourHandler.js" ),
// UserHandler = require( "./UserHandler.js" ),

port = 8081;
responder = new Responder( console );
dao = new HotSpotDAO( dbURL );

poiHandler = new HotSpotHandler(  app, "/poi*", dao, responder );


app.all("*", function(request, response, next) {
    next();
});

app.get("/hotspot.html", function(request, response) {
  response.sendFile(
    "//Users/breinero/Documents/workspace/Walking\ Tour/src/main/javascript/hotspot.html");
});

app.get("*", function(request, response) {
  response.end("404!");
});

http.createServer(app).listen( port );
sys.puts("Server Running on "+port);