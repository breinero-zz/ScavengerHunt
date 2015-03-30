var MongoClient = require('mongodb').MongoClient;

function HotSpotDAO( dbURL ) {

    console.log( "HotSpotDAO constructor" );

    var dao = this;
    
    MongoClient.connect( dbURL, function(err, db) {
        if( err )
            console.log( err );
        else {
            dao.db = db;
            dao.hotspots = dao.db.collection('hotspots');
        }
    });
}

HotSpotDAO.prototype.getHotSpots = function ( coords, maxR, minR, callback ) {

    var query = {
        "Location.geometry" : {
            "$near" : {
                "$geometry" : {
                    "type" : "Point",
                    "coordinates" : coords
                },
            "$maxDistance" : maxR
            }
        }
    }

    dao.hotspots.findOne( query, function(err, hotspots ) {
        if( err )
            callback( err );
        else 
            callback( null, hotspots );
    });
};

HotSpotDAO.prototype.setBallot = function ( ballot, callback ) { 
    var query = { "_id" : ballot.ballot };
    var update = { 
        "$set" : { "projects" : ballot.projects, "rank" : ballot.rank }
    };

    this.ballots.update( query, update, function ( err ) {
        callback( err );
    });
}

HotSpotDAO.prototype.getRound = function ( roundID, callback ) {
    var id = parseInt( roundID );
    var query = { "_id" : id };
    var fields = { "_id" : 0 };

    this.rounds.findOne( query, fields, function(err, round ) {
        if( err )
            callback( err );

        else if( !round ) 
            callback( "no such round" );
        else 
            callback( null, round );
    });
};

HotSpotDAO.prototype.setRound = function ( round, callback ) {

    var query = { "_id" : round.round };
    round._id = parseInt ( round.round );   
    delete round.round;

    this.rounds.update( query, round, { upsert : true }, function( err ) {
        if( err )
            callback( err );
        else 
            callback( null );
    });
};

HotSpotDAO.prototype.aggregate = function( pipeline, callback ) {
    this.ballots.aggregate( pipeline, function(err, result ) {
        if( err ) 
          callback( err, null );
        else
          callback( null, result ); 
    });
};

module.exports = HotSpotDAO;