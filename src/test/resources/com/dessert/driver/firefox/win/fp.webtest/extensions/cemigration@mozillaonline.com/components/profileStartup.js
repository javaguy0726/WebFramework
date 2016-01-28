"use strict";

const { classes: Cc, interfaces: Ci, utils: Cu } = Components;

Cu.import("resource://gre/modules/Services.jsm");
Cu.import("resource://gre/modules/XPCOMUtils.jsm");
Cu.import("resource:///modules/MigrationUtils.jsm");

function ProfileStartup() { }

ProfileStartup.prototype = {
  classID: Components.ID("{a2c72c16-106d-4746-a6fa-06712a7bd031}"),

  _xpcom_factory: XPCOMUtils.generateSingletonFactory(ProfileStartup),

  QueryInterface: XPCOMUtils.generateQI([Ci.nsIObserver, Ci.nsIProfileStartup]),

  directory: Services.dirsvc.get("ProfD", Ci.nsIFile),

  doStartup: function() {
    // do nothing
  },

  observe: function (aSubject, aTopic, aData)
  {
    if (aTopic != "profile-after-change") {
      Cu.reportError("Unexpected observer notification.");
      return;
    }

    // not first run after profile creation
    if (Services.appinfo.replacedLockTime) {
      return;
    }

    // not the only profile, or not the default profile
    let profileService = Cc["@mozilla.org/toolkit/profile-service;1"].
      getService(Ci.nsIToolkitProfileService);
    if (profileService.profileCount > 1 ||
        profileService.selectedProfile.name != "default") {
      return;
    }

    // startup migration not disabled, or not disabled by us
    let override = Services.dirsvc.get("CurProcD", Ci.nsIFile);
    override.append("override.ini");
    if (!override.exists() || !override.isFile()) {
      return;
    }

    let iniParser = Cc["@mozilla.org/xpcom/ini-parser-factory;1"].
      getService(Ci.nsIINIParserFactory).createINIParser(override);
    let disabledByMozillaOnline = false;

    try {
      let ePM = iniParser.getString("XRE", "EnableProfileMigrator");
      disabledByMozillaOnline = ePM == "false-mozillaonline";
    } catch(e) {}

    if (!disabledByMozillaOnline) {
      return;
    }

    MigrationUtils.startupMigration(this);
  },
};

this.NSGetFactory = XPCOMUtils.generateNSGetFactory([ProfileStartup]);
