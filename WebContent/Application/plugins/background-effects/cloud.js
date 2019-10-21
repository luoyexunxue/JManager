var thisURL = $("script").last().attr("src");
var baseURL = thisURL.substr(0, thisURL.lastIndexOf('/') + 1);
document.write('<link href="' + baseURL + 'css/cloud.css" rel="stylesheet" />');

(function() {
	"use strict";
	var background = document.createElement("div");
	var midground = document.createElement("div");
	var foreground = document.createElement("div");
	background.setAttribute("class", "fx_cloud fx_cloud_background");
	midground.setAttribute("class", "fx_cloud fx_cloud_midground");
	foreground.setAttribute("class", "fx_cloud fx_cloud_foreground");
	document.body.appendChild(background);
	document.body.appendChild(midground);
	document.body.appendChild(foreground);
})();