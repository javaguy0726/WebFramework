let Cc = Components.classes;
let Ci = Components.interfaces;
let Cu = Components.utils;

const LOCAL_FILE_CID = "@mozilla.org/file/local;1";
const BUNDLE_MIGRATION = "chrome://cemigrator/locale/migration.properties";

Cu.import("resource://gre/modules/XPCOMUtils.jsm");
Cu.import("resource://gre/modules/Services.jsm");
Cu.import("resource://gre/modules/PlacesUtils.jsm");
Cu.import("resource://gre/modules/NetUtil.jsm");
Cu.import("resource://gre/modules/AddonManager.jsm");
Cu.import("resource://gre/modules/Log.jsm");
Cu.import("resource:///modules/MigrationUtils.jsm");

let logger = Log.repository.getLogger("360se6");
let loggingEnabled = Services.prefs.getBoolPref("extensions.logging.enabled", false);
logger.level = Log.Level[loggingEnabled ? "Debug" : "Warn"];
logger.addAppender(new Log.ConsoleAppender(new Log.BasicFormatter()));

XPCOMUtils.defineLazyGetter(this, "bookmarksSubfolderTitle", function () {
  let strbundle =
    Services.strings.createBundle(BUNDLE_MIGRATION);
  let sourceName = strbundle.GetStringFromName("sourceName360se6");
  return strbundle.formatStringFromName("importedBookmarksFolder",
                                        [sourceName],
                                        1);
});

function parseINIStrings(file) {
  var factory = Cc["@mozilla.org/xpcom/ini-parser-factory;1"].
                getService(Ci.nsIINIParserFactory);
  var parser = factory.createINIParser(file);
  var obj = {};
  var en = parser.getKeys("NowLogin");
  while (en.hasMore()) {
    var key = en.getNext();
    obj[key] = parser.getString("NowLogin", key);
  }
  return obj;
}
function getHash(aStr,algorithm) {
  algorithm = algorithm || Ci.nsICryptoHash.MD5;
  // return the two-digit hexadecimal code for a byte
  function toHexString(charCode)
    ("0" + charCode.toString(16)).slice(-2);

  var hasher = Cc["@mozilla.org/security/hash;1"].
               createInstance(Ci.nsICryptoHash);
  hasher.init(algorithm);
  var stringStream = Cc["@mozilla.org/io/string-input-stream;1"].
                     createInstance(Ci.nsIStringInputStream);
                     stringStream.data = aStr ? aStr : "null";
  hasher.updateFromStream(stringStream, -1);

  // convert the binary hash data to a hex string.
  var binary = hasher.finish(false);
  var hash = [toHexString(binary.charCodeAt(i)) for (i in binary)].join("").toLowerCase();
  return hash ;
}
function createStatement(conn, sql) {
  if (!conn) return;

  try {
    var statement = conn.createStatement(sql);
    return statement;
  } catch(e) {
    logger.error(conn.lastErrorString);
  }
}

function insertBookmarkItems(aParentId, aFolderId, dbConn)
{
  var sql = 'SELECT id, title, url, is_folder FROM tb_fav WHERE parent_id = :parent_id ORDER BY "pos"';
  var places = [];
  var statement = createStatement(dbConn, sql);
  statement.params.parent_id = aParentId
  try {
    while (statement.executeStep()) {
      places.push({
        id        : statement.getUTF8String(0),
        title     : statement.getUTF8String(1),
        url       : statement.getUTF8String(2),
        is_folder : statement.getUTF8String(3),
      });
    }
  } finally {
    statement.reset();
  }
  for (let i = 0; i < places.length; i++) {
    let item = places[i];

    try {
      if (item.is_folder == "0") {
        PlacesUtils.bookmarks.insertBookmark(aFolderId,
                                             NetUtil.newURI(item.url),
                                             PlacesUtils.bookmarks.DEFAULT_INDEX,
                                             item.title);
      } else if (item.is_folder == "1") {
        let newFolderId =
          PlacesUtils.bookmarks.createFolder(aFolderId,
                                             item.title,
                                             PlacesUtils.bookmarks.DEFAULT_INDEX);

        insertBookmarkItems(item.id, newFolderId, dbConn);
      }
    } catch (e) {
      logger.error(e);
    }
  }

}

function Bookmarks(aBookmarksFile) {
  this._file = aBookmarksFile;
}
Bookmarks.prototype = {
  type: MigrationUtils.resourceTypes.BOOKMARKS,

  migrate: function B_migrate(aCallback) {
    PlacesUtils.bookmarks.runInBatchMode({
      runBatched: (function migrateBatched() {

        let dbConn = Services.storage.openUnsharedDatabase(this._file);
        // Toolbar
        let parentFolder = PlacesUtils.toolbarFolderId;
        if (!MigrationUtils.isStartupMigration) {
          parentFolder =
            PlacesUtils.bookmarks.createFolder(parentFolder,
                                               bookmarksSubfolderTitle,
                                               PlacesUtils.bookmarks.DEFAULT_INDEX);
        }
        insertBookmarkItems("0",parentFolder, dbConn)

        aCallback(true);
      }).bind(this)
    }, null);
  },

};

function ns360se6Migrator() {
}

ns360se6Migrator.prototype = Object.create(MigratorPrototype);

ns360se6Migrator.prototype.getResources = function M360_getResources() {
  let resources = [];
  let path = Services.dirsvc.get("AppData", Ci.nsIFile).path + "\\360se6\\apps\\data\\users\\";
  let userDir = "default";
  try {
    let userDataDir = Cc[LOCAL_FILE_CID].createInstance(Ci.nsILocalFile);
    userDataDir.initWithPath(path);
    if (!userDataDir.exists() || !userDataDir.isReadable())
      return resources;

      let localState = Cc[LOCAL_FILE_CID].createInstance(Ci.nsILocalFile);
      localState.initWithPath(path + "login.ini");
      if (!localState.exists())
        throw new Components.Exception("360se6's 'login.ini' does not exist.",
                                       Cr.NS_ERROR_FILE_NOT_FOUND);
      if (!localState.isReadable())
        throw new Components.Exception("360se6's 'login.ini' file could not be read.",
                                       Cr.NS_ERROR_FILE_ACCESS_DENIED);
      var localStateObj = parseINIStrings(localState);
      if (localStateObj.IsAuto == 1 && localStateObj.UserMd5)
        userDir = localStateObj.UserMd5;
  } catch (e) {
    Cu.reportError(e);
  }

  let profileDir = Cc[LOCAL_FILE_CID].createInstance(Ci.nsILocalFile);
  profileDir.initWithPath(path + userDir);

  logger.debug("path + userDir     " + path + userDir)

  if (!profileDir.exists())
    return resources;

  let file = profileDir.clone();
  file.append("360sefav.db");
  if (file.exists())
    resources.push(new Bookmarks(file));

  return resources;
};

ns360se6Migrator.prototype.classDescription = "Qihu360se6 Profile Migrator";
ns360se6Migrator.prototype.contractID = "@mozilla.org/profile/migrator;1?app=browser&type=360se6";
ns360se6Migrator.prototype.classID = Components.ID("{73b378c3-ce11-1700-d699-0795d610ea14}");

const NSGetFactory = XPCOMUtils.generateNSGetFactory([ns360se6Migrator]);
