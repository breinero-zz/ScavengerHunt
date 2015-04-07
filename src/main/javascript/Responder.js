var http = require("http");

function Responder ( logger ) {
  this.logger = logger;
};

Responder.prototype.Ok = function( response ) {
  response.statusCode = 200;
  response.end(); 
};

Responder.prototype.sendJSON = function( response, object ) {
  response.statusCode = 200;
  response.setHeader("Content-Type", "text/json");
  response.write( JSON.stringify( object ) );
  response.end(); 
};

Responder.prototype.badRequest = function( response, message ) {
  response.statusCode = 400;
  response.setHeader("Content-Type", "text");
  response.write( message );
  response.end();
};

Responder.prototype.noSuchItem = function( response, id ) {
  response.statusCode = 404;
  response.setHeader("Content-Type", "text");
  response.write( JSON.stringify( "Unknown id: "+id ) );
  response.end();
};

Responder.prototype.authenticationFalure = function( response, message ) {
  response.statusCode = 404;
  response.setHeader("Content-Type", "text");
  response.write( JSON.stringify( "Authentication failed."+message ) );
  response.end();
};

Responder.prototype.serverErr = function( response, message ) {
  this.logger.log(message);
  response.statusCode = 500;
  response.setHeader("Content-Type", "text");
  response.write( JSON.stringify( "Uh oh! Having some issues. Please try again." ) );
  response.end();
};

module.exports = Responder;