this.EXPORTED_SYMBOLS = [
  "delayedSuggestBaidu", "Frequent", "getPref", "Homepage",
  "nxdomainMitigation", "Session", "SignatureVerifier"
];

const {classes: Cc, interfaces: Ci, results: Cr, utils: Cu} = Components;

Cu.import("resource://gre/modules/XPCOMUtils.jsm");
XPCOMUtils.defineLazyModuleGetter(this, "PlacesUtils",
  "resource://gre/modules/PlacesUtils.jsm");
XPCOMUtils.defineLazyModuleGetter(this, "Services",
  "resource://gre/modules/Services.jsm");
XPCOMUtils.defineLazyModuleGetter(this, "setTimeout",
  "resource://gre/modules/Timer.jsm");
XPCOMUtils.defineLazyModuleGetter(this, "clearTimeout",
  "resource://gre/modules/Timer.jsm");

XPCOMUtils.defineLazyModuleGetter(this, "NTabDB",
  "resource://ntab/NTabDB.jsm");
XPCOMUtils.defineLazyModuleGetter(this, "Tracking",
  "resource://ntab/Tracking.jsm");

let delayedSuggestBaidu = {
  attribute: "mozCNDelayedSuggestBaidu",
  delay: 10e3,
  icon: "chrome://ntab/skin/delayed-suggest-baidu.png",
  knownStatus: [Cr.NS_ERROR_NET_RESET, Cr.NS_ERROR_NET_TIMEOUT],
  notificationKey: "mozcn-delayed-suggest-baidu",
  prefKey: "moa.delayedsuggest.baidu",
  version: 1, // bump this to ignore existing nomore

  get baidu() {
    delete this.baidu;
    return this.baidu = Services.search.getEngineByName("\u767e\u5ea6");
  },

  get bundle() {
    let url = "chrome://ntab/locale/overlay.properties";
    delete this.bundle;
    return this.bundle = Services.strings.createBundle(url);
  },

  get enabled() {
    return (getPref(this.prefKey, 0) < this.version) && this.baidu;
  },

  init: function() {
    Services.search.init();
  },

  isGoogleSearch: function(aURI) {
    try {
      let publicSuffix = Services.eTLD.getPublicSuffix(aURI);
      let hostMatch = ["google.", "www.google."].some(function(aPrefix) {
        return (aPrefix + publicSuffix) == aURI.asciiHost;
      });

      if (!hostMatch) {
        return false;
      }
    } catch(e) {
      return false;
    }

    return (aURI.path == "/" || aURI.path.startsWith("/search?"));
  },

  attach: function(aBrowser, aRequest) {
    this.remove(aBrowser, aRequest);
    if (!this.enabled) {
      return;
    }

    let timeoutId = setTimeout((function() {
      this.notify(aBrowser, aRequest);
    }).bind(this), this.delay);
    aBrowser.setAttribute(this.attribute, timeoutId);
  },

  remove: function(aBrowser, aRequest) {
    if (aBrowser.hasAttribute(this.attribute)) {
      let timeoutId = aBrowser.getAttribute(this.attribute);
      aBrowser.removeAttribute(this.attribute);
      clearTimeout(parseInt(timeoutId, 10));
    }

    if (this.knownStatus.indexOf(aRequest.status) < 0) {
      let gBrowser = aBrowser.ownerGlobal.gBrowser;
      let notificationBox = gBrowser.getNotificationBox(aBrowser);
      let notification = notificationBox.
        getNotificationWithValue(this.notificationKey);
      if (notification) {
        notificationBox.removeNotification(notification);
      }
    }
  },

  extractKeyword: function(aURI) {
    let keyword = "";

    try {
      let query = aURI.QueryInterface(Ci.nsIURL).query;

      if (query) {
        query.split("&").some(function(aChunk) {
          let pair = aChunk.split("=");

          let match = pair[0] == "q";
          if (match) {
            keyword = decodeURIComponent(pair[1]).replace(/\+/g, " ");
          }
          return match;
        })
      }
    } catch(e) {}

    return keyword;
  },

  notify: function(aBrowser, aRequest) {
    if (!this.enabled) {
      return;
    }

    let keyword = this.extractKeyword(aRequest.URI);

    let gBrowser = aBrowser.ownerGlobal.gBrowser;
    let notificationBox = gBrowser.getNotificationBox(aBrowser);

    let self = this;
    let prefix = "delayedsuggestbaidu.notification.";
    let message = this.bundle.GetStringFromName(prefix + "message");
    let positive = this.bundle.GetStringFromName(prefix + "positive");
    let negative = this.bundle.GetStringFromName(prefix + "negative");

    let notificationBar = notificationBox.appendNotification(message,
      this.notificationKey,
      this.icon,
      notificationBox.PRIORITY_INFO_HIGH,
      [{
        label: positive,
        accessKey: "Y",
        callback: function() {
          self.searchAndSwitchEngine(aBrowser, keyword);
        }
      }, {
        label: negative,
        accessKey: "N",
        callback: function() {
          self.markNomore()
        }
      }]);
    notificationBar.persistence = 1;
    Tracking.track({
      type: "delayedsuggestbaidu",
      action: "notify",
      sid: "dummy"
    });
  },

  searchAndSwitchEngine: function(aBrowser, aKeyword) {
    let w = aBrowser.ownerGlobal;
    if (aKeyword) {
      let submission = this.baidu.getSubmission(aKeyword);
      // always replace in the current tab
      w.openUILinkIn(submission.uri.spec, "current", null, submission.postData);
    } else {
      w.openUILinkIn(this.baidu.searchForm, "current");
    }

    if (Services.search.currentEngine.name == "Google") {
      this.baidu.hidden = false;
      Services.search.currentEngine = this.baidu;

      Tracking.track({
        type: "delayedsuggestbaidu",
        action: "click",
        sid: "switch"
      });
    }

    Tracking.track({
      type: "delayedsuggestbaidu",
      action: "click",
      sid: "search"
    });
  },

  markNomore: function() {
    try {
      Services.prefs.setIntPref(this.prefKey, this.version);
    } catch(e) {}

    Tracking.track({
      type: "delayedsuggestbaidu",
      action: "click",
      sid: "nomore"
    });
  }
};

let nxdomainMitigation = {
  allowedDomains: ["firefoxchina.net"],
  prefs: {
    "home": {
      "key": "extensions.cehomepage.abouturl",
      "val": "http://i.firefoxchina.cn/"
    },
    "ntab": {
      "key": NTabDB.altSpecPref,
      "val": "http://offlintab.firefoxchina.cn/"
    }
  },

  get timer() {
    delete this.timer;
    return this.timer = Cc["@mozilla.org/timer;1"].
      createInstance(Ci.nsITimer);
  },
  get updateUrl() {
    let u = "http://check.17firefox.com/v1/mitigations.json?cachebust=20150121";
    delete this.updateUrl;
    return this.updateUrl = u;
  },

  _fetch: function(aUrl, aCallback) {
    if (!aUrl) {
      return;
    }
    let xhr = Cc["@mozilla.org/xmlextras/xmlhttprequest;1"]
                .createInstance(Ci.nsIXMLHttpRequest);
    xhr.open('GET', aUrl, true);
    xhr.onload = function(evt) {
      if (xhr.status == 200) {
        try {
          aCallback(JSON.parse(xhr.responseText));
        } catch(e) {
          aCallback();
        }
      } else {
        aCallback();
      }
    };
    xhr.onerror = function(evt) {
      aCallback();
    };
    try {
      xhr.send();
    } catch(e) {
      aCallback();
    }
  },

  _maybeSwitchHosts: function(aData) {
    if (isNaN(aData.validUntil) || Date.now() > aData.validUntil * 86400e3) {
      this._resetHosts();
      return;
    }

    let self = this;
    Object.keys(this.prefs).forEach(function(aType) {
      let val = self.prefs[aType].val;
      try {
        let uri = Services.io.newURI(aData[aType], null, null);
        let baseDomain = Services.eTLD.getBaseDomain(uri);
        if (self.allowedDomains.indexOf(baseDomain) > -1) {
          val = uri.spec;
        }
      } catch(e) {}

      self._setPref(self.prefs[aType].key, val);
    });
  },

  _resetHosts: function() {
    let self = this;
    Object.keys(this.prefs).forEach(function(aType) {
      let { key, val } = self.prefs[aType];

      self._setPref(key, val);
    });
  },

  _setPref: function(aKey, aVal) {
    let localizedStr = Cc["@mozilla.org/pref-localizedstring;1"].
      createInstance(Ci.nsIPrefLocalizedString);

    localizedStr.data = "data:text/plain," + aKey + "=" + aVal;
    Services.prefs.getDefaultBranch("").setComplexValue(aKey,
      Ci.nsIPrefLocalizedString, localizedStr);
  },

  notify: function() {
    let self = this;
    this._fetch(this.updateUrl, function(aData) {
      if (!aData) {
        self._resetHosts();
        return;
      }

      if (SignatureVerifier.verify(aData.data, aData.signature)) {
        self._maybeSwitchHosts(JSON.parse(aData.data));
      } else {
        self._resetHosts();
      }
    });
  },

  init: function() {
    let self = this;
    Object.keys(this.prefs).forEach(function(aType) {
      let { key, val } = self.prefs[aType];
      self.prefs[aType].val = (getPref(key, val,
        Ci.nsIPrefLocalizedString, true) || val);
    });

    this.notify();
    this.timer.initWithCallback(this, (30 * 60e3),
      Ci.nsITimer.TYPE_REPEATING_PRECISE_CAN_SKIP);
  }
};

let Frequent = {
  excludes: [
    /^http:\/\/i.firefoxchina.cn\/n(ew)?tab/,
    /^http:\/\/i.firefoxchina.cn\/parts\/google_rdr/,
    /^http:\/\/i.firefoxchina.cn\/redirect\/adblock/,
    /^http:\/\/i.firefoxchina.cn\/(redirect\/)?search/,
    /^http:\/\/i.g-fox.cn\/(rd|search)/,
    /^http:\/\/www5.1616.net\/q/
  ],
  needsDeduplication: false,
  order: Ci.nsINavHistoryQueryOptions.SORT_BY_FRECENCY_DESCENDING,

  query: function(aCallback, aLimit) {
    let options = PlacesUtils.history.getNewQueryOptions();
    options.maxResults = aLimit + 16;
    options.sortingMode = this.order;

    let deduplication = {};
    let links = [];
    let self = this;

    let callback = {
      handleResult: function (aResultSet) {
        let row;

        while (row = aResultSet.getNextRow()) {
          if (links.length >= aLimit) {
            break;
          }
          let url = row.getResultByIndex(1);
          let title = row.getResultByIndex(2);

          if (self.needsDeduplication) {
            if (deduplication[title]) {
              continue;
            }
            deduplication[title] = 1;
          }

          if (!self.excludes.some(function(aExclude) {
            return aExclude.test(url);
          })) {
            links.push({url: url, title: title});
          }
        }
      },

      handleError: function (aError) {
        aCallback([]);
      },

      handleCompletion: function (aReason) {
        aCallback(links);
      }
    };

    let query = PlacesUtils.history.getNewQuery();
    let db = PlacesUtils.history.QueryInterface(Ci.nsPIPlacesDatabase);
    db.asyncExecuteLegacyQueries([query], 1, options, callback);
  },

  remove: function(aUrls) {
    let urls = [];
    aUrls.forEach(function(aUrl) {
      urls.push(Services.io.newURI(aUrl, null, null));
    });
    PlacesUtils.bhistory.removePages(urls, urls.length);
  }
};

let getPref = function(prefName, defaultValue, valueType, useDefaultBranch) {
  valueType = valueType || Ci.nsISupportsString;
  let prefs = !!useDefaultBranch ?
    Services.prefs.getDefaultBranch("") :
    Services.prefs;

  switch (prefs.getPrefType(prefName)) {
    case Ci.nsIPrefBranch.PREF_STRING:
      return prefs.getComplexValue(prefName, valueType).data;

    case Ci.nsIPrefBranch.PREF_INT:
      return prefs.getIntPref(prefName);

    case Ci.nsIPrefBranch.PREF_BOOL:
      return prefs.getBoolPref(prefName);

    case Ci.nsIPrefBranch.PREF_INVALID:
      return defaultValue;
  }
};

let Homepage = {
  defaultAboutpage: "http://i.firefoxchina.cn/",
  defaultHomepage: "about:cehome",
  // When an empty string is set as pref value, display this.defaultAboutpage.
  get aboutpage() {
    return getPref("extensions.cehomepage.abouturl", this.defaultAboutpage,
      Ci.nsIPrefLocalizedString) || this.defaultAboutpage;
  },
  get homepage() {
    return getPref("browser.startup.homepage", this.defaultHomepage,
      Ci.nsIPrefLocalizedString);
  },
  get page() {
    return getPref("browser.startup.page", 1);
  },
  isHomepage: function(aSpec, aReferenceURI) {
    if (this.page !== 1) {
      return false;
    }

    aSpec = this.normalizeSpec(aSpec, aReferenceURI);
    if (!aSpec) {
      return true;
    }

    return (this.homepage === this.defaultHomepage ||
            this.homepage.split("?")[0] === this.aboutpage.split("?")[0] ||
            this.homepage === aSpec);
  },
  normalizeSpec: function(aSpec, aReferenceURI) {
    try {
      let uri = Services.uriFixup.createFixupURI(aSpec,
        Services.uriFixup.FIXUP_FLAG_NONE);
      if (uri.prePath !== aReferenceURI.prePath) {
        return;
      }

      // ignore the "?cachebust=***" when comparing with this.aboutpage.
      if (uri.spec.split("?")[0] === this.aboutpage.split("?")[0]) {
        return this.defaultHomepage;
      } else {
        return uri.spec;
      }
    } catch(e) {
      return;
    }
  },
  setHomepage: function(aSpec, aReferenceURI) {
    aSpec = this.normalizeSpec(aSpec, aReferenceURI);
    if (!aSpec) {
      return;
    }

    Services.prefs.clearUserPref("browser.startup.homepage");
    Services.prefs.clearUserPref("browser.startup.page");

    if (this.homepage === aSpec) {
      return;
    }
    Services.prefs.setCharPref("browser.startup.homepage", aSpec);
  }
};

let Session = Object.create(Frequent, {
  needsDeduplication: {
    value: true
  },
  order: {
    value: Ci.nsINavHistoryQueryOptions.SORT_BY_DATE_DESCENDING
  }
});

let SignatureVerifier = {
  get verifier() {
    delete this.verifier;
    return this.verifier = Cc["@mozilla.org/security/datasignatureverifier;1"].
      getService(Ci.nsIDataSignatureVerifier);
  },

  get key() {
    delete this.key;
    return this.key = getPref('moa.signatureverifier.key', '');
  },

  verify: function(aData, aSignature) {
    try {
      return this.verifier.verifyData(aData, aSignature, this.key);
    } catch(e) {
      return false;
    }
  }
};
