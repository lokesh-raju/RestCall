var mapData = "";

function initialization() {
	markers = [];
	directionsService = new google.maps.DirectionsService();
	infowindow = new google.maps.InfoWindow();
	var rendererOptions = {
		draggable : true
	};
	directionsDisplay = new google.maps.DirectionsRenderer(rendererOptions);
	google.maps.event.addDomListener(window, 'load', initMap);
}

function initializationDiv(mData) {
	mapData = mData;
	gMap = mData.mapDiv;
	if (mData.hasOwnProperty("height")) {
		document.getElementById(gMap).style.height = mData.height;
	} else {
		document.getElementById(gMap).style.height = "500px";
	}
	if (mData.hasOwnProperty("width")) {
		document.getElementById(gMap).style.width = mData.width;
	} else {
		document.getElementById(gMap).style.width = "50%";
	}
	markers = [];
	directionsService = new google.maps.DirectionsService();
	infowindow = new google.maps.InfoWindow();
	var rendererOptions = {
		draggable : true
	};
	directionsDisplay = new google.maps.DirectionsRenderer(rendererOptions);
	initMap();
}

function initMap() {
	var latlong = mapData;
	if (latlong == "") {
		latlong = JSON.parse(window.mapDataObj);
	}
	if (latlong.hasOwnProperty('fromLocation')) {
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(drivDirPos, showError);
		}
	} else if (latlong.hasOwnProperty('markerInfo')) {
		var currPos = {};
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(showPosition, showError);
		} else {
			var markers = mapData;
			if (markers == "") {
				markers = JSON.parse(window.mapDataObj);
			}
			initializeMaps(markers.markerInfo);
		}
	} else if (latlong.hasOwnProperty('nearbyplaces')) {
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(getPosition, showError);
		}
	}
}

function showError(error) {
	switch (error.code) {
	case error.PERMISSION_DENIED:
		alert("User denied the request for Geolocation.")
		break;
	case error.POSITION_UNAVAILABLE:
		alert("Location information is unavailable.")
		break;
	case error.TIMEOUT:
		alert("The request to get user location timed out.")
		break;
	case error.UNKNOWN_ERROR:
		alert("An unknown error occurred.")
		break;
	}
}

// current location
function initializeMaps(markers) {
	var myOptions = {
		zoom : 12,
		mapTypeId : google.maps.MapTypeId.ROADMAP,
		navigationControlOptions : {
			style : google.maps.NavigationControlStyle.SMALL
		},
		mapTypeControl : false
	};
	if (mapData == "") {
		var map = new google.maps.Map(document.getElementById("map_canvas"),
				myOptions);
	} else {
		var map = new google.maps.Map(document.getElementById(gMap), myOptions);
	}
	var infowindow = new google.maps.InfoWindow();
	var marker, i;
	var bounds = new google.maps.LatLngBounds();
	for (i = 0; i < markers.length; i++) {

		var pos = new google.maps.LatLng(markers[i].locationLatitude,
				markers[i].locationLongitude);
		bounds.extend(pos);
		if (markers.length - 1 == i) {
			if (mapData == "") {
				marker = new google.maps.Marker({
					position : pos,
					map : map,
					icon : '../../../appzillon/container/img/blue-dot.png'
				});
			} else {
				marker = new google.maps.Marker({
					position : pos,
					map : map,
					icon : 'appzillon/container/img/blue-dot.png'
				});
			}

		} else {
			if (mapData == "") {
				marker = new google.maps.Marker({
					position : pos,
					map : map,
					icon : '../../../appzillon/container/img/red-dot.png'
				});
			} else {
				marker = new google.maps.Marker({
					position : pos,
					map : map,
					icon : 'appzillon/container/img/red-dot.png'
				});
			}
		}
		google.maps.event.addListener(marker, 'click', (function(marker, i) {
			return function() {
				infowindow.setContent(markers[i].locationName + ","
						+ markers[i].locationDescription);
				infowindow.open(map, marker);
			}
		})(marker, i));
	}
	map.fitBounds(bounds);
	// map.setCenter(pos);//if required to set the zoom appropriately comment
	// and above line and enable this
}

function showPosition(position) {
	var currPos = {};
	currPos.locationName = "I am here";
	currPos.locationDescription = "";
	currPos.locationLatitude = position.coords.latitude;
	currPos.locationLongitude = position.coords.longitude;
	var markers = mapData;
	if (markers == "") {
		markers = JSON.parse(window.mapDataObj);
	}
	markers['markerInfo'].push(currPos);
	initializeMaps(markers.markerInfo);
}

function drivDirPos(position) {
	markers = mapData;
	if (markers == "") {
		markers = JSON.parse(window.mapDataObj);
	}
	currlat = position.coords.latitude;
	currlng = position.coords.longitude;
	currposition = new google.maps.LatLng(currlat, currlng);
	initialize(markers);
}

function initialize(markers) {
	var mapOptions = {
		zoom : 7,
		center : currposition
	};
	if (mapData == "") {
		var map = new google.maps.Map(document.getElementById("map_canvas"),
				mapOptions);
	} else {
		var map = new google.maps.Map(document.getElementById(gMap), mapOptions);
	}

	directionsDisplay.setMap(map);
	calcRoute(markers);
}

function calcRoute(markers) {
	var frmLat;
	var frmLongt;
	if (markers.fromLocation == "") {
		frmLat = currlat;
		frmLongt = currlng;
	} else {
		var frmFields = markers.fromLocation.split(/,/);
		frmLat = frmFields[0];
		frmLongt = frmFields[1];
	}

	var toFields = markers.toLocation.split(/,/);
	var toLat = toFields[0];
	var toLongt = toFields[1];

	var start = new google.maps.LatLng(frmLat, frmLongt);
	var end = new google.maps.LatLng(toLat, toLongt);
	var request = {
		origin : start,
		destination : end,
		travelMode : google.maps.TravelMode.DRIVING
	};

	directionsService.route(request, function(response, status) {
		if (status == 'OK') {
			directionsDisplay.setDirections(response);
		} else {
			alert('Directions request failed due to ' + status);
		}
	});
}

function getPosition(position) {
	srchLocations = mapData;
	if (srchLocations == "") {
		srchLocations = JSON.parse(window.mapDataObj);

	}
	var pos = new google.maps.LatLng(position.coords.latitude,
			position.coords.longitude);
	initializeSelector(pos);
}

function initializeSelector(pos) {
	geocoder = new google.maps.Geocoder();
	var center = pos;
	radius_circle = parseFloat(srchLocations.radius); // in km

	// draw map
	var mapOptions = {
		center : center,
		zoom : 15,
	};
	if (mapData == "") {
		map = new google.maps.Map(document.getElementById("map_canvas"),
				mapOptions);
	} else {
		map = new google.maps.Map(document.getElementById(gMap), mapOptions);
	}
	showMarkers(center);
	google.maps.event.addListener(map, 'click', function(e) {
		var clickPos = new google.maps.LatLng(e.latLng.lat(), e.latLng.lng());
		showMarkers(clickPos);
	});
}

function showMarkers(clickPos) {
	clearMarkers();
	createCurrentMarker(clickPos);
	checkNearByPlaces(clickPos);
}

function clearMarkers() {
	setAllMap(null);
}

function setAllMap(map) {
	for (var i = 0; i < markers.length; i++) {
		markers[i].setMap(map);
	}
	markers = [];
}

function checkNearByPlaces(clickPos) {
	/*
	 * circle = drawCircle(clickPos); cityCircle.setMap(null);
	 */
	var flg;

	for (var i = 0; i < srchLocations.nearbyplaces.length; i++) {
		var searchLat = parseFloat(srchLocations.nearbyplaces[i].locationLatitude);
		var searchLong = parseFloat(srchLocations.nearbyplaces[i].locationLongitude);
		var Latlng = new google.maps.LatLng(searchLat, searchLong);

		ceateMarkers(Latlng);
		flag = 1;
	}

	if (flag == 1) {
		call();
	} else {
	}
}

function ceateMarkers(Latlng) {
	var marks = new google.maps.Marker({
		position : Latlng,
		map : map
	});
	markers.push(marks);

	google.maps.event.addListener(marks, 'click', (function() {
		var placeNameval1;
		var latlng = new google.maps.LatLng(this.getPosition().lat(), this
				.getPosition().lng());
		geocoder.geocode({
			'latLng' : latlng
		}, function(results, status) {
			if (status == google.maps.GeocoderStatus.OK) {
				if (results[0]) {
					placeNameval1 = results[0].formatted_address;
					infowindow.setContent(placeNameval1);
				}
			} else {
				alert('Request failed due to ' + status);
			}
		});
		infowindow.open(map, this);
	}));
}

function createCurrentMarker(clickPos) {
	geocoder.geocode({
		'latLng' : clickPos
	}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			if (results[0]) {
				placeNameval = results[0].formatted_address;
			}
		} else {
			alert("The Geocode was not successful for the following reason: "
					+ status);
		}
	});

	marker = new google.maps.Marker({
		map : map,
		position : clickPos
	});
	markers.push(marker);

	circle = drawCircle(clickPos);
	cityCircle.setMap(null);
	map.fitBounds(circle.getBounds());

	google.maps.event.addListener(marker, 'click', function() {
		infowindow.setContent(placeNameval);
		infowindow.open(map, this);
	});
}

function drawCircle(clickPos) {
	var populate = {
		// strokeColor: '#0000FF',
		// strokeOpacity: 0.8,
		strokeWeight : 1,
		// fillColor: '#0000FF',
		// fillOpacity: 0.35,
		map : map,
		center : clickPos,
		radius : radius_circle
	};
	cityCircle = new google.maps.Circle(populate);
	return cityCircle;
}

function call() {
	for (var i = 0; i < srchLocations.nearbyplaces.length + 1; i++) {
		var distance = calculateDistance(markers[i].getPosition().lat(),
				markers[i].getPosition().lng(), circle.getCenter().lat(),
				circle.getCenter().lng(), "K");

		if (distance * 1000 < radius_circle) { // radius is in meter; distance
												// in km
			var nearby = new google.maps.LatLng(markers[i].getPosition().lat(),
					markers[i].getPosition().lng());
			if (mapData == "") {
				markers[i]
						.setIcon('../../../appzillon/container/img/blue-dot.png');
				markers[0]
						.setIcon('../../../appzillon/container/img/red-dot.png');
			} else {
				markers[i].setIcon('appzillon/container/img/blue-dot.png');
				markers[0].setIcon('appzillon/container/img/red-dot.png');
			}

		} else {
			markers[i].setMap(null);
		}
	}
}

function calculateDistance(lat1, lon1, lat2, lon2, unit) {
	var radlat1 = Math.PI * lat1 / 180;
	var radlat2 = Math.PI * lat2 / 180;
	var radlon1 = Math.PI * lon1 / 180;
	var radlon2 = Math.PI * lon2 / 180;
	var theta = lon1 - lon2;
	var radtheta = Math.PI * theta / 180;
	var dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1)
			* Math.cos(radlat2) * Math.cos(radtheta);
	dist = Math.acos(dist);
	dist = dist * 180 / Math.PI;
	dist = dist * 60 * 1.1515;
	if (unit == "K") {
		dist = dist * 1.609344;
	}
	if (unit == "N") {
		dist = dist * 0.8684;
	}
	return dist;
}
