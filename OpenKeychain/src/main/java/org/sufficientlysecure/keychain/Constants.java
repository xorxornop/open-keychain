/*
 * Copyright (C) 2010-2014 Thialfihar <thi@thialfihar.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sufficientlysecure.keychain;

import android.os.Environment;

import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.sufficientlysecure.keychain.securitytoken.KeyFormat;
import org.sufficientlysecure.keychain.securitytoken.RSAKeyFormat;
import org.sufficientlysecure.keychain.service.SaveKeyringParcel;
import org.sufficientlysecure.keychain.service.SaveKeyringParcel.SubkeyAdd;

import java.io.File;
import java.net.Proxy;

public final class Constants {

    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final boolean DEBUG_LOG_DB_QUERIES = false;
    public static final boolean DEBUG_EXPLAIN_QUERIES = false;
    public static final boolean DEBUG_SYNC_REMOVE_CONTACTS = false;
    public static final boolean DEBUG_KEYSERVER_SYNC = false;

    public static final String TAG = DEBUG ? "Keychain D" : "Keychain";

    public static final String PACKAGE_NAME = "org.sufficientlysecure.keychain";

    public static final String ACCOUNT_NAME = DEBUG ? "OpenKeychain D" : "OpenKeychain";
    public static final String ACCOUNT_TYPE = BuildConfig.ACCOUNT_TYPE;
    public static final String CUSTOM_CONTACT_DATA_MIME_TYPE = "vnd.android.cursor.item/vnd.org.sufficientlysecure.keychain.key";

    public static final String PROVIDER_AUTHORITY = BuildConfig.PROVIDER_CONTENT_AUTHORITY;
    public static final String TEMP_FILE_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".tempstorage";

    public static final String CLIPBOARD_LABEL = "Keychain";

    // as defined in http://tools.ietf.org/html/rfc3156
    public static final String MIME_TYPE_KEYS = "application/pgp-keys";
    // NOTE: don't use application/pgp-encrypted It only holds the version number!
    public static final String MIME_TYPE_ENCRYPTED = "application/octet-stream";
    // NOTE: Non-standard alternative, better use this, because application/octet-stream is too unspecific!
    // also see https://tools.ietf.org/html/draft-bray-pgp-message-00
    public static final String MIME_TYPE_ENCRYPTED_ALTERNATE = "application/pgp-message";
    public static final String MIME_TYPE_TEXT = "text/plain";

    public static final String FILE_EXTENSION_PGP_MAIN = ".pgp";
    public static final String FILE_EXTENSION_PGP_ALTERNATE = ".gpg";
    public static final String FILE_EXTENSION_ASC = ".asc";

    public static final String FILE_BACKUP_PREFIX = "backup_";
    public static final String FILE_EXTENSION_BACKUP_SECRET = ".sec.asc";
    public static final String FILE_EXTENSION_BACKUP_PUBLIC = ".pub.asc";
    public static final String FILE_ENCRYPTED_BACKUP_PREFIX = "backup_";
    // actually it is ASCII Armor, so .asc would be more accurate, but Android displays a nice icon for .pgp files!
    public static final String FILE_EXTENSION_ENCRYPTED_BACKUP_SECRET = ".sec.pgp";
    public static final String FILE_EXTENSION_ENCRYPTED_BACKUP_PUBLIC = ".pub.pgp";

    // used by QR Codes (Guardian Project, Monkeysphere compatibility)
    public static final String FINGERPRINT_SCHEME = "openpgp4fpr";

    public static final String BOUNCY_CASTLE_PROVIDER_NAME = BouncyCastleProvider.PROVIDER_NAME;

    // prefix packagename for exported Intents
    // as described in http://developer.android.com/guide/components/intents-filters.html
    public static final String INTENT_PREFIX = PACKAGE_NAME + ".action.";
    public static final String EXTRA_PREFIX = PACKAGE_NAME + ".";

    public static final int TEMPFILE_TTL = 24 * 60 * 60 * 1000; // 1 day

    // the maximal length of plaintext to read in encrypt/decrypt text activities
    public static final int TEXT_LENGTH_LIMIT = 1024 * 50;

    public static final String SAFESLINGER_SERVER = "slinger-openpgp.appspot.com";

    // Intents API
    public static final String ENCRYPT_TEXT = INTENT_PREFIX + "ENCRYPT_TEXT";
    public static final String ENCRYPT_EXTRA_TEXT = EXTRA_PREFIX + "EXTRA_TEXT"; // String
    public static final String ENCRYPT_DATA = INTENT_PREFIX + "ENCRYPT_DATA";
    public static final String ENCRYPT_EXTRA_ASCII_ARMOR = EXTRA_PREFIX + "EXTRA_ASCII_ARMOR"; // boolean
    public static final String DECRYPT_DATA = INTENT_PREFIX + "DECRYPT_DATA";
    public static final String IMPORT_KEY = INTENT_PREFIX + "IMPORT_KEY";
    public static final String IMPORT_EXTRA_KEY_EXTRA_KEY_BYTES = EXTRA_PREFIX + "EXTRA_KEY_BYTES"; // byte[]
    public static final String IMPORT_KEY_FROM_KEYSERVER = INTENT_PREFIX + "IMPORT_KEY_FROM_KEYSERVER";
    public static final String IMPORT_KEY_FROM_KEYSERVER_EXTRA_QUERY = EXTRA_PREFIX + "EXTRA_QUERY"; // String
    public static final String IMPORT_KEY_FROM_KEYSERVER_EXTRA_FINGERPRINT = EXTRA_PREFIX + "EXTRA_FINGERPRINT"; // String
    public static final String IMPORT_KEY_FROM_QR_CODE = INTENT_PREFIX + "IMPORT_KEY_FROM_QR_CODE";

    public static final class Path {
        public static final File APP_DIR = new File(Environment.getExternalStorageDirectory(), "OpenKeychain");
    }

    public static final class Notification {
        public static final int PASSPHRASE_CACHE = 1;
        public static final int KEYSERVER_SYNC_FAIL_ORBOT = 2;
    }

    public static final class Pref {
        public static final String PASSPHRASE_CACHE_TTLS = "passphraseCacheTtls";
        public static final String PASSPHRASE_CACHE_DEFAULT = "passphraseCacheDefault";
        public static final String PASSPHRASE_CACHE_SUBS = "passphraseCacheSubs";
        public static final String LANGUAGE = "language";
        public static final String KEY_SERVERS = "keyServers";
        public static final String PREF_VERSION = "keyServersDefaultVersion";
        public static final String FIRST_TIME = "firstTime";
        public static final String CACHED_CONSOLIDATE = "cachedConsolidate";
        public static final String SEARCH_KEYSERVER = "search_keyserver_pref";
        public static final String SEARCH_KEYBASE = "search_keybase_pref";
        public static final String USE_NUMKEYPAD_FOR_SECURITY_TOKEN_PIN = "useNumKeypadForYubikeyPin";
        public static final String ENCRYPT_FILENAMES = "encryptFilenames";
        public static final String FILE_USE_COMPRESSION = "useFileCompression";
        public static final String TEXT_USE_COMPRESSION = "useTextCompression";
        public static final String USE_ARMOR = "useArmor";
        // proxy settings
        public static final String USE_NORMAL_PROXY = "useNormalProxy";
        public static final String USE_TOR_PROXY = "useTorProxy";
        public static final String PROXY_HOST = "proxyHost";
        public static final String PROXY_PORT = "proxyPort";
        public static final String PROXY_TYPE = "proxyType";
        public static final String THEME = "theme";
        // keyserver sync settings
        public static final String SYNC_CONTACTS = "syncContacts";
        public static final String SYNC_KEYSERVER = "syncKeyserver";
        public static final String ENABLE_WIFI_SYNC_ONLY = "enableWifiSyncOnly";
        // other settings
        public static final String EXPERIMENTAL_ENABLE_WORD_CONFIRM = "experimentalEnableWordConfirm";
        public static final String EXPERIMENTAL_ENABLE_LINKED_IDENTITIES = "experimentalEnableLinkedIdentities";
        public static final String EXPERIMENTAL_ENABLE_KEYBASE = "experimentalEnableKeybase";
        public static final String EXPERIMENTAL_SMARTPGP_VERIFY_AUTHORITY = "smartpgp_authorities_pref";
        public static final String EXPERIMENTAL_SMARTPGP_AUTHORITIES = "smartpgp_authorities";

        public static final class Theme {
            public static final String LIGHT = "light";
            public static final String DARK = "dark";
            public static final String DEFAULT = Constants.Pref.Theme.LIGHT;
        }

        public static final class ProxyType {
            public static final String TYPE_HTTP = "proxyHttp";
            public static final String TYPE_SOCKS = "proxySocks";
        }
    }

    /**
     * information to connect to Orbot's localhost HTTP proxy
     */
    public static final class Orbot {
        public static final String PROXY_HOST = "127.0.0.1";
        public static final int PROXY_PORT = 9050;
        public static final Proxy.Type PROXY_TYPE = Proxy.Type.DProxy.Type.SOCKS;
    }

    public static final class Defaults {
        public static final String KEY_SERVERS = "hkps://hkps.pool.sks-keyservers.net, hkps://pgp.mit.edu";
        public static final int PREF_CURRENT_VERSION = 8;
    }

    public static final class key {
        public static final long none = 0;
        public static final long symmetric = -1;
        public static final long backup_code = -2;
    }

    /**
     * Default key configuration: 3072 bit RSA (certify, sign, encrypt)
     */
    public static void addDefaultSubkeys(SaveKeyringParcel.Builder builder) {
        builder.addSubkeyAdd(SubkeyAdd.createSubkeyAdd(SaveKeyringParcel.Algorithm.RSA,
                3072, null, KeyFlags.CERTIFY_OTHER, 0L));
        builder.addSubkeyAdd(SubkeyAdd.createSubkeyAdd(SaveKeyringParcel.Algorithm.RSA,
                3072, null, KeyFlags.SIGN_DATA, 0L));
        builder.addSubkeyAdd(SubkeyAdd.createSubkeyAdd(SaveKeyringParcel.Algorithm.RSA,
                3072, null, KeyFlags.ENCRYPT_COMMS | KeyFlags.ENCRYPT_STORAGE, 0L));
    }

    /**
     * Default key format for OpenPGP smart cards v2: 2048 bit RSA (sign+certify, decrypt, auth)
     */
    private static final int ELEN = 17; //65537
    public static final KeyFormat SECURITY_TOKEN_V2_SIGN = new RSAKeyFormat(2048, ELEN, RSAKeyFormat.RSAAlgorithmFormat.CRT_WITH_MODULUS);
    public static final KeyFormat SECURITY_TOKEN_V2_DEC = new RSAKeyFormat(2048, ELEN, RSAKeyFormat.RSAAlgorithmFormat.CRT_WITH_MODULUS);
    public static final KeyFormat SECURITY_TOKEN_V2_AUTH = new RSAKeyFormat(2048, ELEN, RSAKeyFormat.RSAAlgorithmFormat.CRT_WITH_MODULUS);

}
