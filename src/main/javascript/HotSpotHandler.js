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
  app.put( path, this.updatePointOfInterest );
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

  var self = this;

  var url_parts = url.parse(request.url, true);

  if ( !request.body )
    this.responder.badRequest();

  var candidate = request.body;
  // constraint checking

  console.log( candidate );
  this.dao.setHotSpots( candidate );

};

HotSpotHandler.prototype.updatePointOfInterest = function(request, response) {

  if( !request ) {
    console.log( "Request is null");
  }

  if( !request.headers["content-type"].match( contentTypeRegex ) )
    return this.responder.badRequest(response, "only JSON accepted");

  if ( !request.body )
    this.responder.badRequest();

  var candidate = request.body;
  // constraint checking
  console.log( candidate );
  this.dao.setHotSpot( candidate, 
    function( err ) {
        this.responder.badRequest( response, "Could not persist POI. "+err );
    } );
};

HotSpotHandler.prototype.removePointOfInterest = function(request, response) {

  if( !request.headers["content-type"].match( contentTypeRegex ) )
    return this.responder.badRequest(response, "only JSON accepted");
}

module.exports = HotSpotHandler;