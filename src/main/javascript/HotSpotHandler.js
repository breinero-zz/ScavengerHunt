var MongoClient = require('mongodb').MongoClient,
contentTypeRegex = /application\/json/,
Responder = require('./Responder.js');

function HotSpotHandler ( app, path, dao, responder ) {
  this.app = app,
  this.path = path;
  this.dao = dao;
  this.responder = responder;

  app.get( path, this.getNearHotSpots );
  app.post( path, this.updatePointOfInterest );
  app.put( path, this.setPointOfInterest );
  app.delete( path, this.removePointOfInterest );
}

HotSpotHandler.prototype.getNearHotSpots = function(request, response) {
  var url_parts = url.parse(request.url, true);

  var coordinates = [
      parseFloat( url_parts.query.lon ),
      parseFloat( url_parts.query.lat )
  ];

  var max = parseFloat( url_parts.query.max );
  var min = parseFloat( url_parts.query.min );

  console.log( JSON.stringify( coordinates ) );

  this.dao.getHotSpots( 
    coordinates, max, min, 
    function( err, hotspot ) {
      if( err )
        responder.serverErr( response, err );
      else
        responder.sendJSON( response, hotspot );
    }
  );
};

HotSpotHandler.prototype.setPointOfInterest = function ( request, response ) {

  if( !request.headers["content-type"].match( contentTypeRegex ) )
    return this.responder.badRequest(response, "only JSON accepted");

};

HotSpotHandler.prototype.updatePointOfInterest = function(request, response) {
  if( !request.headers["content-type"].match( contentTypeRegex ) )
    return this.responder.badRequest(response, "only JSON accepted");
};

HotSpotHandler.prototype.removePointOfInterest = function(request, response) {

  if( !request.headers["content-type"].match( contentTypeRegex ) )
    return this.responder.badRequest(response, "only JSON accepted");
}

module.exports = HotSpotHandler;