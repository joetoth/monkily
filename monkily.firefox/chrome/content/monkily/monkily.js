/**
 * Console Service
 */
const
consoleService = Utils.CCSV("@mozilla.org/consoleservice;1",
		"nsIConsoleService");

/**
 * Http Service
 */
const
observerService = Utils.CCSV("@mozilla.org/observer-service;1",
		"nsIObserverService");

const
nsIHttpChannel = Utils.CI("nsIHttpChannel");

const
httpModifyRequest = "http-on-modify-request";
const
httpExamineResponse = "http-on-examine-response";

const
LOGS_HOSTNAME = "monkily-logs.s3.amazonaws.com";

const BOUNDARY = "--j893qr9835th735v78";

const COOKIE_USER_ID = "userId";

//const COOKIE_HOSTNAME = ".monkily.com";

const COOKIE_HOSTNAME = "localhost";

const HOSTNAME = "localhost:8080";

/**
 * Preferences Service
 */
const
PrefService = Utils.CC("@mozilla.org/preferences-service;1");

window.addEventListener("load", function(e) {
	Monkily.initialize();
}, false);

var Monkily = {

	requestQueue: [],
	ip: null,
    lastLocation: null,

	initialize : function() {
		Monkily.determineIp();
		observerService.addObserver(this, httpModifyRequest, false);
		observerService.addObserver(this, httpExamineResponse, false);
		window.addEventListener('close', this.onWindowClose, false);
	},

	onWindowClose : function() {
		dump('sending data!\n');
		Monkily.sendData();
	},

	onWindowClick : function() {
		dump('loc:'+Monkily.getBrowserWindow().location+'\n');
	},
	
	observe : function(subject, topic, data) {

		if (topic == httpExamineResponse ) {

			var httpChannel = subject.QueryInterface(nsIHttpChannel);
			var contentType = httpChannel.getResponseHeader('Content-Type');
            var currentLocation = Monkily.getBrowserWindow().location; 

//            httpChannel.URI.spec

            if (httpChannel.URI.host != LOGS_HOSTNAME && currentLocation != Monkily.lastLocation) {
                Monkily.lastLocation = currentLocation;
				Monkily.addToQueue(currentLocation.href, new Date().getTime(), contentType, navigator.userAgent);
			}
	
		} else if (topic == httpModifyRequest) {
		}

	},
	
	addToQueue: function(url, datetime, contentType, userAgent) {

		var request = new Monkily.Request(url, datetime, contentType, userAgent);
        dump('adding: ' + request.url + '\n');
        Monkily.requestQueue.push(request);


for (var i = 0; i < Monkily.requestQueue.length; i++) {
    dump(Monkily.requestQueue[i].url + '\n');
    dump(Monkily.requestQueue[i].datetime + '\n');
}

        
    },	
	
	sendData : function() {
		try {	
			var userId = Monkily.getUserId();
			
			if (userId == null) {
				userId = "00000000-0000-0000-0000-000000000000";
			}
			
			var key = userId + "_" + Monkily.ip + "_" + new Date().getTime() + ".log"; 
		
			var parameters = Monkily.queueToText();
			
			dump(parameters);
			
			 // Attempt to save the file
            S3Ajax.put("monkily-logs", key, parameters, 
	            function(req, obj) {
	                dump("success\n");
	            },
	            function(req, obj) {
	                dump("error\n");
	            }
	        );

		} catch (e) {
			/* silent failure see a lot of crappy execptions on this call */
			dump(e + '\n');
		}
		
	},
	
	determineIp: function() {

        var request = new XMLHttpRequest();

        request.onreadystatechange = function (event) {
            // wait till readyState is COMPLETED
            if (request.readyState != 4)
                return;

            // successful login
            if (request.status == 200) {
                Monkily.ip = request.responseText;
            }
        };

        request.open("GET", "http://" + HOSTNAME + "/getIp", true);
//        request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
//        request.setRequestHeader("Content-length", params.length);
        request.setRequestHeader("Connection", "close");
        request.send("");
	},
	
	getUserId: function() {
		var cookieMgr = Components.classes["@mozilla.org/cookiemanager;1"].getService(Components.interfaces.nsICookieManager);

        for (var e = cookieMgr.enumerator; e.hasMoreElements();) {
            var cookie = e.getNext().QueryInterface(Components.interfaces.nsICookie);
            if (cookie.host == (COOKIE_HOSTNAME) && cookie.name == COOKIE_USER_ID)
                return cookie.value;
        }
        return null;
	},

	queueToText: function() {
		var text = 'url\tdatetime\tcontentType\n';
        for (var i in Monkily.requestQueue) {

        	var request = Monkily.requestQueue[i];
        	dump(i+' '+Monkily.requestQueue[i].url+'\n' );
        	text += request.url + '\t' + request.datetime + '\t' + request.contentType + '\n';
        }
        return text;
	},
	
    toggle: function(event)
    {
        if (event.button != 0)
            return;

        var container = document.getElementById('monkilyContainer');
        var splitter = document.getElementById('monkilySplitter');

        var collapsed = container.getAttribute("collapsed");

        if (collapsed == 'false') {
            splitter.setAttribute("collapsed", true);
            container.setAttribute("collapsed", true);
        } else {
            container.setAttribute("collapsed", false);
            splitter.setAttribute("collapsed", false);
        }
    },

 getBrowserWindow: function() {
        var browser = Components.classes["@mozilla.org/appshell/window-mediator;1"].getService(Components.interfaces.nsIWindowMediator).getMostRecentWindow("navigator:browser");
        return browser.getBrowser().contentWindow;
    }


};


Monkily.Request = function(url, datetime, contentType, userAgent) {
    this.url = url;
    this.datetime = datetime;
    this.contentType = contentType;
    this.userAgent = userAgent;
}
