


// These two lines are required to initialize Express in Cloud Code.
var express = require('express');
var app = express();

app.set('views', 'cloud/views');  // Specify the folder to find templates
app.set('view engine', 'ejs');    // Set the template engine
app.use(express.bodyParser());    // Middleware for reading request body

// This is an example of hooking up a request handler with a specific request
// path and HTTP verb using the Express routing API.
app.get('/Item', function(req, res) {
	var cid = req.param('cid');
	
  //if (cid) {
	var ItemsObject = Parse.Object.extend("Items");
	var query = new Parse.Query(ItemsObject);
	var itemUrl = "http://onceaday.parseapp.com/item?cid=" + cid;
    query.equalTo("objectId", cid);
    query.find({
	  error: function(error) {		
		response.error("error");
	  },
      success: function(results) {		
		var itemTitle = results[0].attributes.itemTitle;
        var itemBody = results[0].attributes.itemBody;
		var itemImageUrl = results[0].attributes.backgroundImage.url();
		res.render('Item', {itemObjectIDParam: cid, itemUrlParam: itemUrl, itemImageUrlParam: itemImageUrl,itemTitleParam: itemTitle });
      }
    });
  //}
  //res.render('hello', { message: 'Congrats, you just set up your app!',itemObjectIDParam: cid, itemUrlParam: itemUrl });
  //res.render('hello', { message: 'Congrats, you just set up your app!',itemObjectID: cid });
});

// // Example reading from the request query string of an HTTP get request.
// app.get('/test', function(req, res) {
//   // GET http://example.parseapp.com/test?message=hello
//   res.send(req.query.message);
// });

// // Example reading from the request body of an HTTP post request.
// app.post('/test', function(req, res) {
//   // POST http://example.parseapp.com/test (with request body "message=hello")
//   res.send(req.body.message);
// });

// Attach the Express app to Cloud Code.
app.listen();
