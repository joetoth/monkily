var Utils = new function() {

// ************************************************************************************************
// Components

    var _CI = Components.interfaces;
    var _CC = Components.classes;

    this.CC = function(cName)
    {
        return _CC[cName];
    };

    this.CI = function(ifaceName)
    {
        return _CI[ifaceName];
    };

    this.CCSV = function(cName, ifaceName)
    {
        return _CC[cName].getService(_CI[ifaceName]);
    };

    this.CCIN = function(cName, ifaceName)
    {
        return _CC[cName].createInstance(_CI[ifaceName]);
    };

    this.QI = function(obj, iface)
    {
        return obj.QueryInterface(iface);
    };    

// ************************************************************************************************
// Window iteration

    this.iterateWindows = function(win, handler)
    {
        if (!win || !win.document)
            return;

        handler(win);

        if (win == top) return; // XXXjjb hack for chromeBug

        for (var i = 0; i < win.frames.length; ++i)
        {
            var subWin = win.frames[i];
            if (subWin != win)
                this.iterateWindows(subWin, handler);
        }
    };

    this.getRootWindow = function(win)
    {
        for (; win; win = win.parent)
        {
            if (!win.parent || win == win.parent)
                return win;
        }
        return null;
    };

// ************************************************************************************************
// Misc

    this.removeChildrenFromNode = function(node)
    {
       if(node == undefined || node == null) {
          return;
       }

       var len = node.childNodes.length;

       while (node.hasChildNodes())
       {
           node.removeChild(node.firstChild);
       }
    };

    this.cloneArray = function(array, fn)
    {
       var newArray = [];

       if (fn)
           for (var i = 0; i < array.length; ++i)
               newArray.push(fn(array[i]));
       else
           for (var i = 0; i < array.length; ++i)
               newArray.push(array[i]);

       return newArray;
    };

    this.bindFixed = function()
    {
        var args = Utils.cloneArray(arguments), fn = args.shift(), object = args.shift();
        return function() { return fn.apply(object, args); }
    };

    this.$ = function () {
      var elements = new Array();

      for (var i = 0; i < arguments.length; i++) {
        var element = arguments[i];
        if (typeof element == 'string')
          element = document.getElementById(element);

        if (arguments.length == 1)
          return element;

        elements.push(element);
      }

      return elements;
    };
    
    this.extend = function(l, r)
    {
        var newOb = {};
        for (var n in l)
            newOb[n] = l[n];
        for (var n in r)
            newOb[n] = r[n];
        return newOb;
    };


    this.isSystemURL = function(url)
    {
        if (!url) return true;
        if (url.length == 0) return true; // spec for about:blank
        if (url.substr(0, 9) == "resource:")
            return true;
        else if (url.substr(0, 17) == "chrome://firebug/")
            return true;
        else if (url.substr(0, 6) == "about:")
            return true;
        else if (url.indexOf("firebug-service.js") != -1)
            return true;
        else
            return false;
    };

    this.isSystemPage = function(win)
    {
        try
        {
            var doc = win.document;
            if (!doc)
                return false;

            // Detect pages for pretty printed XML
            if ((doc.styleSheets.length && doc.styleSheets[0].href
                    == "chrome://global/content/xml/XMLPrettyPrint.css")
                || (doc.styleSheets.length > 1 && doc.styleSheets[1].href
                    == "chrome://browser/skin/feeds/subscribe.css"))
                return true;

            return FBL.isSystemURL(win.location.href);
        }
        catch (exc)
        {
            // Sometimes documents just aren't ready to be manipulated here, but don't let that
            // gum up the works
            FireMeld._log("tabWatcher.isSystemPage document not ready:"+ exc);
            return false;
        }
    }


}

