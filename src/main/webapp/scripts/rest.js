/**
 * 
 */
		function ajaxSyncRequest(reqURL, id) {
			//Creating a new XMLHttpRequest object
			var xmlhttp;
			if (window.XMLHttpRequest) {
				xmlhttp = new XMLHttpRequest(); //for IE7+, Firefox, Chrome, Opera, Safari
			} else {
				xmlhttp = new ActiveXObject("Microsoft.XMLHTTP"); //for IE6, IE5
			}
			//Create a asynchronous GET request
			xmlhttp.open("POST", reqURL, false);
			xmlhttp.send(null);

			//Execution blocked till server send the response
			if (xmlhttp.readyState == 4) {
				if (xmlhttp.status == 200) {
					document.getElementById("myDiv" + id).innerHTML = xmlhttp.responseText;
					//
				} else {
					alert(reqURL + xmlhttp.status);
				}
			}
		};
		
		function ajaxFanRequest(reqURL, id) {
			//Creating a new XMLHttpRequest object
			var xmlhttp;
			if (window.XMLHttpRequest) {
				xmlhttp = new XMLHttpRequest(); //for IE7+, Firefox, Chrome, Opera, Safari
			} else {
				xmlhttp = new ActiveXObject("Microsoft.XMLHTTP"); //for IE6, IE5
			}
			//Create a asynchronous GET request
			xmlhttp.open("GET", reqURL, false);
			xmlhttp.send(null);

			//Execution blocked till server send the response
			if (xmlhttp.readyState == 4) {
				if (xmlhttp.status == 200) {
					document.getElementById(id).innerHTML = xmlhttp.responseText;
					//
				} else {
					alert(reqURL + xmlhttp.status);
				}
				;
			}
			;
		};
		
		function checkActivityRequest(reqURL, id) {
			var top = (id - 1) * 83;
			var top2 = new String(top + "px 0px 0px 0px");
			//Creating a new XMLHttpRequest object
			var xmlhttp;
			if (window.XMLHttpRequest) {
				xmlhttp = new XMLHttpRequest(); //for IE7+, Firefox, Chrome, Opera, Safari
			} else {
				xmlhttp = new ActiveXObject("Microsoft.XMLHTTP"); //for IE6, IE5
			}
			//Create a asynchronous GET request
			xmlhttp.open("GET", reqURL, false);
			xmlhttp.send(null);

			//Execution blocked till server send the response
			if (xmlhttp.readyState == 4) {
				if (xmlhttp.status == 200) {
					document.getElementById("myDiv").innerHTML = xmlhttp.responseText;
					document.getElementById("social_activity").style.margin = top2;
				} else {
					alert(reqURL + xmlhttp.status);
				}
			}
		};