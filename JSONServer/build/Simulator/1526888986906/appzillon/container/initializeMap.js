/*takes json array containing location details*/
function initializeMaps(markers) {
	//alert(markers.length);
	var myOptions = {
		zoom : 15,
		mapTypeId : google.maps.MapTypeId.ROADMAP,
		navigationControlOptions : {
			style : google.maps.NavigationControlStyle.SMALL
		},
		mapTypeControl : false
	};
	var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
	var infowindow = new google.maps.InfoWindow();
	var marker, i;
	var bounds = new google.maps.LatLngBounds();
	for( i = 0; i < markers.length; i++) {
		var pos = new google.maps.LatLng(markers[i].locationLatitude, markers[i].locationLongitude);
		bounds.extend(pos);
		//alert('1'+pos);
		if(markers.length - 1 == i) {
			marker = new google.maps.Marker({
				position : pos,
				//alert("POS1"+pos);
				map : map,
				icon : '../scripts/container/img/blue-dot.png'
			});
		} 
		google.maps.event.addListener(marker, 'click', (function(marker, i) {
			return function() {
				infowindow.setContent(markers[i].locationName + "," + markers[i].locationDescription);
				infowindow.open(map, marker);
			}
		})(marker, i));

	}
	//map.fitBounds(bounds);
	map.setCenter(pos);
}

 google.maps.event.addDomListener(window, 'load', function() {
     debugger;
	var currPos = {};	
	//alert('initializeMap');	
	var markers = JSON.parse(localStorage.getItem("mapData"));	
    initializeMaps(markers.markerInfo);
		
}); 

function showPosition(markerobj){

}

