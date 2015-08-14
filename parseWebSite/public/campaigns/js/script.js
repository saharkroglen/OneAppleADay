'use strict';

/* Helpers */

function setCookie(cname, cvalue, exdays, path) {
  var d = new Date();
  d.setTime(d.getTime() + (exdays*24*60*60*1000));
  var expires = "expires="+d.toGMTString();
  document.cookie = cname + "=" + cvalue + "; " + expires + "; " + path;
}

function getCookie(cname) {
  var name = cname + "=";
  var ca = document.cookie.split(';');
  for(var i=0; i<ca.length; i++) {
    var c = ca[i].trim();
    if (c.indexOf(name) == 0) return c.substring(name.length,c.length);
  }
  return "";
}

function addScript (url, callback) {
  var script = document.createElement('script');
  script.type = 'text/javascript';
  script.src = url;
  document.getElementsByTagName('head')[0].appendChild(script);
  if (callback) script.onload = callback;
}

var findMobileOs = function () {
  if (navigator.userAgent.match(/Android/i)) 
    return 'android';
  else if (navigator.userAgent.match(/Mac OS/i)) 
    return 'ios';
  else 
    return 'ios';
};

var shuffle = function (o){
  for(var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
  return o;
};

var scrollDown = function () {
  $("html, body").animate({ scrollTop: $('body').height() }, 100);
};

var checkDir = function (txt) {
  var wordArr = txt.replace(/[,)(\[\]\\*&^%$#{}?!><;:@'",.~\/]/g, ' ').split(' ');
  var reg_hebrew_word = new RegExp('[\u0590-\u05FF]+[\"]?[\u0590-\u05FF]+');
  var reg_arabic_word = new RegExp("[\u0600-\u06FF]+[\"]?[\u0600-\u06FF]+");
  var rtlWord = 0;
  var wordNum = wordArr.length;
  for (var i = 0; i < wordArr.length; i++) {
    if (reg_hebrew_word.test(wordArr[i]) || reg_hebrew_word.test(wordArr[i])) {
      rtlWord++;
    }
  }
  var isRtl = ((wordNum / 2) > rtlWord)? 'ltr' : 'rtl';
  return isRtl;
};

function getQueryParams(name) {
  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regexS = "[\\?&]"+name+"=([^&#]*)";
  var regex = new RegExp( regexS );
  var results = regex.exec( window.location.href );
  if( results == null )
    return null;
  else
    return results[1];
}

var getCSSRule = function (getRule, newRule){
  var styles=document.styleSheets;
  for(var i=0,l=styles.length; i<l; ++i){
    var sheet=styles[i]; 
    if(sheet.href && sheet.href.indexOf('style.css') != -1){
      var rules = sheet.cssRules;
      for(var j=0, l2=rules.length; j<l2; j++){
        var rule=rules[j];
        if(getRule === rule.selectorText){  
          eval(newRule);
          break;
        };
      };
    };
  };
};

var addCSSRule = function (getRule, newRule){
  var styles=document.styleSheets;
  for(var i=0,l=styles.length; i<l; ++i){
    var sheet=styles[i]; 
    if(sheet.href && sheet.href.indexOf('style.css') != -1){
      var rules = sheet.cssRules;
      var rule = rules[0];
      rule.parentStyleSheet.addRule(getRule, newRule, rule.parentStyleSheet.rules.length);
      break;
    };
  };
};

var removeCSSRule = function (getRule){
  var styles=document.styleSheets;
  for(var i=0,l=styles.length; i<l; ++i){
    var sheet=styles[i]; 
    if(sheet.href && sheet.href.indexOf('style.css') != -1){
      var rules = sheet.cssRules;
      for(var j=0, l2=rules.length; j<l2; j++){
        var rule=rules[j];
        if(getRule === rule.selectorText){
          rule.parentStyleSheet.deleteRule(j);
          break;
        };
      };
    };
  };
};
