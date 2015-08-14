'use strict';

/* Connection */

Parse.initialize("IwjXCfulcA64BVZd002616T56rLALjuhGnBdIRCn", "1PK4dWFuMOopioVSiS0jIyiUTUMwj50SjxY3n4kj");

function statusChangeCallback(response) {
  if (response.status === 'connected') {
  } else if (response.status === 'not_authorized') {
    
  } else {
    
  }
}

function facebookWallPost(name, link, picture, caption, description) {
	var params = {
	  method: 'feed',
	  name: name,
	  link: link,
	  picture: picture,
	  caption: caption,
	  description: description
	};
  FB.ui(params, function(obj) { console.log(obj);});
}

function checkLoginState() {
  FB.getLoginStatus(function(response) {
    statusChangeCallback(response);
  });
}

function FBLogin () {
      Parse.FacebookUtils.logIn(null, {
        success: function(user) {
          if (!user.existed()) {
            setCookie('c_user', 1, 30, '/');
            setCookie('c_username', user.name, 30, '/');
            window.updateScope();
          } else {
            setCookie('c_user', 1, 30, '/');
            setCookie('c_username', user.name, 30, '/');
            window.updateScope();
          }
        },
        error: function(user, error) {
          console.log("User cancelled the Facebook login or did not fully authorize."); 
        }
      });
    }

window.fbAsyncInit = function() {
  Parse.FacebookUtils.init({
    appId      : '1527859724103673',
    cookie     : true,
    xfbml      : true,
    version    : 'v2.0'
  });
  
  FB.getLoginStatus(function(response) {
    statusChangeCallback(response);
  });
  
  FB.Event.subscribe('auth.authResponseChange', function(response) {
    removeCSSRule(".fb-comments > span");
    addCSSRule(".btnLoginToFb", "display:none !important;");
  });
  
  FB.Event.subscribe('auth.login', function(response) {
  
  });
  
  FB.api('/me', function(response) {
    setCookie('myUserName', response.name, 180, location.href);
  });
};

(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/en_US/all.js";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));