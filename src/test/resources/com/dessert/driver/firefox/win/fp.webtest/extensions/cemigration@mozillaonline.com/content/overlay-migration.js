(function() {

let Cc = Components.classes,
    Ci = Components.interfaces;

let tracker = {
  descToMigratorKeys: {
    "360\u5b89\u5168\u6d4f\u89c8\u5668": ["360se6", "360se"]
  },

  handleEvent: function(aEvt) {
    switch(aEvt.type) {
      case "load":
        this.init(aEvt);
        break;
      case "unload":
        this.uninit(aEvt);
        break;
    }
  },

  getNonBuiltInBrowser: function() {
    // already detected
    if (window.MigrationWizard._source) {
      return;
    }

    let browserDesc = "";
    try {
      browserDesc =
        Cc["@mozilla.org/uriloader/external-protocol-service;1"].
        getService(Ci.nsIExternalProtocolService).
        getApplicationDescription("http");

      // just report it if no migrator available for the detected browser
      let migratorKeys = this.descToMigratorKeys[browserDesc];
      if (!migratorKeys) {
        return browserDesc;
      }

      let group = document.getElementById("importSourceGroup");
      migratorKeys.some(function(key) {
        let item = group.querySelector('[id="' + key + '"]');
        if (item.hidden) {
          return false;
        }

        window.MigrationWizard._source = key;
        group.selectedItem = item;
        return true;
      });
    } catch(ex) {};

    return;
  },

  init: function(aEvt) {
    window.removeEventListener(aEvt.type, this, false);

    if (!window.arguments || !window.arguments[2]) {
      return;
    }
    if(!window.arguments[2].QueryInterface(Ci.nsIProfileStartup)) {
      return;
    }

    // prefix for tracker.track
    this.track("Migration:Load", this.getNonBuiltInBrowser());

    window.addEventListener("unload", this, false);

    let obs = Cc["@mozilla.org/observer-service;1"].
      getService(Ci.nsIObserverService);
    obs.addObserver(this, "Migration:ItemAfterMigrate", false);
    obs.addObserver(this, "Migration:ItemError", false);
    obs.addObserver(this, "Migration:Ended", false);
  },

  uninit: function(aEvt) {
    window.removeEventListener(aEvt.type, this, false);

    let obs = Cc["@mozilla.org/observer-service;1"].
      getService(Ci.nsIObserverService);
    obs.removeObserver(this, "Migration:ItemAfterMigrate");
    obs.removeObserver(this, "Migration:ItemError");
    obs.removeObserver(this, "Migration:Ended");
  },

  observe: function(aSubject, aTopic, aData) {
    switch(aTopic) {
      case "Migration:ItemAfterMigrate":
      case "Migration:ItemError":
        if (aData == Ci.nsIBrowserProfileMigrator.BOOKMARKS) {
          this.track(aTopic);
        }
        break;
      case "Migration:Ended":
        this.track(aTopic);
        break;
    }
  },

  track: function(aTopic, aExtra) {
    let source = !!aExtra ? encodeURIComponent(aExtra) :
      (window.MigrationWizard._source || "NA");
    let topic = aTopic.slice("Migration:".length).toLowerCase();

    // do not respect non-existing user choice at this moment
    let urlTemplate = "http://addons.g-fox.cn/startupMigration.gif?" +
                      "r=%RANDOM%&source=%SOURCE%&topic=%TOPIC%";
    let url = urlTemplate.replace("%TOPIC%", topic).
                          replace("%SOURCE%", source).
                          replace("%RANDOM%", Math.random());
    let xhr = Cc["@mozilla.org/xmlextras/xmlhttprequest;1"].
                createInstance(Ci.nsIXMLHttpRequest);
    xhr.open("GET", url, true);
    xhr.send();
  }
};

window.addEventListener("load", tracker, false);

})();
