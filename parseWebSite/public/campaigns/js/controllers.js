'use strict';

/* Controllers */

function LP ($scope, $http) {

	

  $scope.campaign = null;
  $scope.campaignId = null;
  $scope.deals = null;
  $scope.total = null;
  $scope.promoterName = null;
  $scope.amount = 1;
  $scope.discountApplied = false;
  
  $scope.itemUrl = "";
  $scope.itemTitle = "";
  $scope.itemBody = "";
  $scope.itemBackgroundImage = "";
  Parse.initialize("gFWZRPLmGg1MRH8TeCNAg4erKgcWrk5OO4uqyJ67","MtK1eXVHsKvCOKBazWTbLZxKnZhVipaSd116WTzy");
  var ItemsObject = Parse.Object.extend("Items");
  var query = new Parse.Query(ItemsObject);
  
  if (getQueryParams('cid')) {
	$scope.itemUrl = "http://onceaday.parseapp.com/campaigns/?cid=" + getQueryParams('cid');
    query.equalTo("objectId", getQueryParams('cid'));
    query.find({
	  error: function(error) {		
		alert(error);
	  },
      success: function(results) {		
		$scope.itemTitle = results[0].attributes.itemTitle;
        $scope.itemBody = results[0].attributes.itemBody;
		$scope.itemBackgroundImage = results[0].attributes.backgroundImage.url();
		getImageDataURL($scope.itemBackgroundImage, onSuccess, onError)  

		/*//add title facebook metadata
		var meta = document.createElement('meta');		
		meta.setAttribute('property','og:title');
		meta.content = $scope.itemTitle;
		document.getElementsByTagName('head')[0].appendChild(meta);			
		
		
		//add image facebook metadata
		meta = document.createElement('meta');		
		meta.setAttribute('property','og:image');
		meta.content = $scope.itemBackgroundImage;
		document.getElementsByTagName('head')[0].appendChild(meta);		
		
		//add site name facebook metadata
		meta = document.createElement('meta');		
		meta.setAttribute('property','og:site_name ');
		meta.content = 'תפוח אחד ביום';
		document.getElementsByTagName('head')[0].appendChild(meta);		
		*/
		
		$scope.$apply();        
      }
    });
  }
  
  $('.spinner .btn:first-of-type').on('click', function() {
    if (parseInt($('.spinner input').val()) >= 0) {
      $('.spinner input').val( parseInt($('.spinner input').val(), 10) + 1);
      $scope.amount = $scope.amount + 1;
      $scope.$apply();
    }
  });
  $('.spinner .btn:last-of-type').on('click', function() {
    if (parseInt($('.spinner input').val()) > 0) {
      $('.spinner input').val( parseInt($('.spinner input').val(), 10) - 1);
      $scope.amount = $scope.amount - 1;
      $scope.$apply();
    }
  });
  $scope.totalPrice = function (a,b) {
    $scope.total = a*b;
    return $scope.total;
  };
  $scope.selectedDeal = function () {
    return ($('#dealSelect').val() == "") ? false : true;
  };
  $scope.userStatus = function () {
    var isLoggedin = (getCookie('c_user') == "1") ? true : false;
    return isLoggedin;
  };
  $scope.pay = function () {
    
    $('.modal-body').height($('.modal-body').height())
    $('.modal-body').empty();
    $('.modal-body').append('<div class="glyphicon glyphicon-repeat loading"></div>');
    
    setTimeout(function () {
      $('.modal-body .loading').remove();
      $('.modal-body').append('<div>Congratulations!<br/>Your order has been placed and your confirm email was sent.<br/><br/>To redeem your deal, please contact the merchent and provide your order number: <b>1234</b><br/><br/>Want to share this deal with your friends and start winning rewards? <a hreh="#">You can start here now</a></div>');
    }, 3000);
    
  };
  
  window.updateScope = function () {
    $scope.$apply();
  };
  
  $scope.applyDiscount = ($scope.promoterName && ($('promoterId').val() != "" && $('promoterId').val() != " ")) ? true : false;
  $scope.applyDiscountUpdate = function () {
    $scope.applyDiscount = ($scope.promoterName && ($('#promoterId').val() != "" && $('#promoterId').val() != " ")) ? true : false;
    return $scope.applyDiscount;
  };
  $scope.apply = function (discount) {
    if ($scope.applyDiscountUpdate() && discount)
	{
      //alert('Your code was successfully applied. You saved '+discount+'% off! \nYou can now order and enjoy your special member price.')	  
	  $('#discountAppliedMessage').html('Your code was successfully applied. You saved '+discount+'% off! <br>You can now order and enjoy your special member price.');
	  $scope.discountApplied = true;
	  $('#discountAppliedMessage').show();
	}
	else
	{
		$scope.discountApplied = false;
		$('#discountAppliedMessage').hide();
	}
  };  

}
/**
 * Converts image URLs to dataURL schema using Javascript only.
 *
 * @param {String} url Location of the image file
 * @param {Function} success Callback function that will handle successful responses. This function should take one parameter
 *                            <code>dataURL</code> which will be a type of <code>String</code>.
 * @param {Function} error Error handler.
 *
 * @example
 * var onSuccess = function(e){
 *  document.body.appendChild(e.image);
 *  alert(e.data);
 * };
 *
 * var onError = function(e){
 *  alert(e.message);
 * };
 *
 * getImageDataURL('myimage.png', onSuccess, onError);
 *
 */
function getImageDataURL(url, success, error) {
    
    var data, canvas, ctx;
    	
    var img = new Image();
	img.setAttribute('crossOrigin', 'anonymous');
	img.src = url;
	
    img.onload = function(){
        
        // Create the canvas element.
        canvas = document.createElement('canvas');
        canvas.width = img.width;
        canvas.height = img.height;
        // Get '2d' context and draw the image.
        ctx = canvas.getContext("2d");
        ctx.drawImage(img, 0, 0);
        // Get canvas data URL
        try{           
            data = canvas.toDataURL();            
            document.getElementById('itemImage').src = data;
            success({image:img, data:data});
        }catch(e){
            error(e);
        }
    }
    // Load image URL.
    try{
        //document.getElementById('i').src = url;
    }catch(e){        
        error(e);
    }
}
function getAverageRGB(imgEl) {
    
    var blockSize = 5, // only visit every 5 pixels
        defaultRGB = {r:0,g:0,b:0}, // for non-supporting envs
        canvas = document.createElement('canvas'),
        context = canvas.getContext && canvas.getContext('2d'),
        data, width, height,
        i = -4,
        length,
        rgb = {r:0,g:0,b:0},
        count = 0;
        
    if (!context) {
        return defaultRGB;
    }
    
    height = canvas.height = imgEl.naturalHeight || imgEl.offsetHeight || imgEl.height;
    width = canvas.width = imgEl.naturalWidth || imgEl.offsetWidth || imgEl.width;
    
    context.drawImage(imgEl, 0, 0);
    
    try {
        data = context.getImageData(0, 0, width, height);
    } catch(e) {
        /* security error, img on diff domain */alert('x');
        return defaultRGB;
    }
    
    length = data.data.length;
    
    while ( (i += blockSize * 4) < length ) {
        ++count;
        rgb.r += data.data[i];
        rgb.g += data.data[i+1];
        rgb.b += data.data[i+2];
    }
    
    // ~~ used to floor values
    rgb.r = ~~(rgb.r/count);
    rgb.g = ~~(rgb.g/count);
    rgb.b = ~~(rgb.b/count);
    
    return rgb;
    
}

function AllCampaigns ($scope, $http) {
  $scope.camps = [];
  Parse.initialize("gFWZRPLmGg1MRH8TeCNAg4erKgcWrk5OO4uqyJ67","MtK1eXVHsKvCOKBazWTbLZxKnZhVipaSd116WTzy");
  var CampaignPromoter = Parse.Object.extend("CampaignPromoter");
  var query = new Parse.Query(CampaignPromoter);
  query.find({
    success: function(results) {
      $scope.camps = results;
      $scope.$apply();
    }
  });
}


var onSuccess = function(e){  
    var rgb = getAverageRGB(document.getElementById('itemImage'));
	$( ".container" ).css("background-color",'rgba('+rgb.r+','+rgb.g+','+rgb.b+',0.7)'); //set background according to image average color
	var o = Math.round(((parseInt(rgb[0]) * 299) + (parseInt(rgb[1]) * 587) + (parseInt(rgb[2]) * 114)) /1000);    
    if(o > 125) {
        $('.page-header').css('color', 'black');
    }else{ 
        $('.page-header').css('color', 'white');
    }
    //document.body.style.backgroundColor = 'rgb('+rgb.r+','+rgb.b+','+rgb.g+')';
 };

 var onError = function(e){
  alert(e.message);
 };







