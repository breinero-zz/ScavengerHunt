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

HotSpotDAO.prototype.setHotSpot = function ( hotspot, callback ) { 

    if( !hotspot ) {
        callback( "there's no hotspot for me to insert" );
        return;
    }

    dao.hotspots.insert( hotspot, 
        function ( err ) {
            callback( err );
        }
    );
};

HotSpotDAO.prototype.aggregate = function( pipeline, callback ) {
    this.hotspot.aggregate( pipeline, function(err, result ) {
        if( err ) 
          callback( err, null );
        else
          callback( null, result ); 
    });
};

module.exports = HotSpotDAO;