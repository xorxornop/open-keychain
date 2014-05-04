/*
 * Copyright (C) 2014 Dominik Schürmann <dominik@dominikschuermann.de>
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

package org.sufficientlysecure.keychain.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.sufficientlysecure.keychain.Constants;
import org.sufficientlysecure.keychain.R;
import org.sufficientlysecure.keychain.pgp.PgpKeyHelper;
import org.sufficientlysecure.keychain.provider.KeychainContract.KeyRings;
import org.sufficientlysecure.keychain.provider.KeychainContract.Keys;
import org.sufficientlysecure.keychain.ui.adapter.ViewKeyKeysAdapter;
import org.sufficientlysecure.keychain.util.Log;

import java.util.Date;


public class ViewKeyKeysFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_DATA_URI = "uri";

    private LinearLayout mContainer;
    private TextView mAlgorithm;
    private TextView mKeyId;
    private TextView mExpiry;
    private TextView mCreation;
    private TextView mFingerprint;
    private TextView mSecretKey;

    private ListView mKeys;

    private static final int LOADER_ID_UNIFIED = 0;
    private static final int LOADER_ID_KEYS = 1;

    private ViewKeyKeysAdapter mKeysAdapter;

    private Uri mDataUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_key_keys_fragment, container, false);

        mContainer = (LinearLayout) view.findViewById(R.id.container);
        mKeyId = (TextView) view.findViewById(R.id.key_id);
        mAlgorithm = (TextView) view.findViewById(R.id.algorithm);
        mCreation = (TextView) view.findViewById(R.id.creation);
        mExpiry = (TextView) view.findViewById(R.id.expiry);
        mFingerprint = (TextView) view.findViewById(R.id.view_key_fingerprint);
        mSecretKey = (TextView) view.findViewById(R.id.secret_key);
        mKeys = (ListView) view.findViewById(R.id.keys);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Uri dataUri = getArguments().getParcelable(ARG_DATA_URI);
        if (dataUri == null) {
            Log.e(Constants.TAG, "Data missing. Should be Uri of key!");
            getActivity().finish();
            return;
        }

        loadData(dataUri);
    }

    private void loadData(Uri dataUri) {
        getActivity().setProgressBarIndeterminateVisibility(true);
        mContainer.setVisibility(View.GONE);

        mDataUri = dataUri;

        Log.i(Constants.TAG, "mDataUri: " + mDataUri.toString());

        mKeysAdapter = new ViewKeyKeysAdapter(getActivity(), null, 0);
        mKeys.setAdapter(mKeysAdapter);

        // Prepare the loaders. Either re-connect with an existing ones,
        // or start new ones.
        getLoaderManager().initLoader(LOADER_ID_UNIFIED, null, this);
        getLoaderManager().initLoader(LOADER_ID_KEYS, null, this);
    }

    static final String[] UNIFIED_PROJECTION = new String[] {
        KeyRings._ID, KeyRings.MASTER_KEY_ID, KeyRings.HAS_ANY_SECRET,
            KeyRings.USER_ID, KeyRings.FINGERPRINT,
            KeyRings.ALGORITHM, KeyRings.KEY_SIZE, KeyRings.CREATION, KeyRings.EXPIRY,

    };
    static final int INDEX_UNIFIED_MKI = 1;
    static final int INDEX_UNIFIED_HAS_ANY_SECRET = 2;
    static final int INDEX_UNIFIED_UID = 3;
    static final int INDEX_UNIFIED_FINGERPRINT = 4;
    static final int INDEX_UNIFIED_ALGORITHM = 5;
    static final int INDEX_UNIFIED_KEY_SIZE = 6;
    static final int INDEX_UNIFIED_CREATION = 7;
    static final int INDEX_UNIFIED_EXPIRY = 8;

    static final String[] KEYS_PROJECTION = new String[] {
            Keys._ID,
            Keys.KEY_ID, Keys.RANK, Keys.ALGORITHM, Keys.KEY_SIZE, Keys.HAS_SECRET,
            Keys.CAN_CERTIFY, Keys.CAN_ENCRYPT, Keys.CAN_SIGN, Keys.IS_REVOKED,
            Keys.CREATION, Keys.EXPIRY, Keys.FINGERPRINT
    };
    static final int KEYS_INDEX_CAN_ENCRYPT = 7;

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID_UNIFIED: {
                Uri baseUri = KeyRings.buildUnifiedKeyRingUri(mDataUri);
                return new CursorLoader(getActivity(), baseUri, UNIFIED_PROJECTION, null, null, null);
            }
            case LOADER_ID_KEYS: {
                Uri baseUri = Keys.buildKeysUri(mDataUri);
                return new CursorLoader(getActivity(), baseUri, KEYS_PROJECTION, null, null, null);
            }

            default:
                return null;
        }
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        /* TODO better error handling? May cause problems when a key is deleted,
         * because the notification triggers faster than the activity closes.
         */
        // Avoid NullPointerExceptions...
        if(data.getCount() == 0) {
            return;
        }
        // Swap the new cursor in. (The framework will take care of closing the
        // old cursor once we return.)
        switch (loader.getId()) {
            case LOADER_ID_UNIFIED: {
                if (data.moveToFirst()) {
                    if (data.getInt(INDEX_UNIFIED_HAS_ANY_SECRET) != 0) {
                        mSecretKey.setTextColor(getResources().getColor(R.color.emphasis));
                        mSecretKey.setText(R.string.secret_key_yes);
                    } else {
                        mSecretKey.setTextColor(Color.BLACK);
                        mSecretKey.setText(getResources().getString(R.string.secret_key_no));
                    }

                    // get key id from MASTER_KEY_ID
                    long masterKeyId = data.getLong(INDEX_UNIFIED_MKI);
                    String keyIdStr = PgpKeyHelper.convertKeyIdToHex(masterKeyId);
                    mKeyId.setText(keyIdStr);

                    // get creation date from CREATION
                    if (data.isNull(INDEX_UNIFIED_CREATION)) {
                        mCreation.setText(R.string.none);
                    } else {
                        Date creationDate = new Date(data.getLong(INDEX_UNIFIED_CREATION) * 1000);

                        mCreation.setText(
                                DateFormat.getDateFormat(getActivity().getApplicationContext()).format(
                                        creationDate));
                    }

                    // get expiry date from EXPIRY
                    if (data.isNull(INDEX_UNIFIED_EXPIRY)) {
                        mExpiry.setText(R.string.none);
                    } else {
                        Date expiryDate = new Date(data.getLong(INDEX_UNIFIED_EXPIRY) * 1000);

                        mExpiry.setText(
                                DateFormat.getDateFormat(getActivity().getApplicationContext()).format(
                                        expiryDate));
                    }

                    String algorithmStr = PgpKeyHelper.getAlgorithmInfo(
                            getActivity(),
                            data.getInt(INDEX_UNIFIED_ALGORITHM),
                            data.getInt(INDEX_UNIFIED_KEY_SIZE)
                    );
                    mAlgorithm.setText(algorithmStr);

                    byte[] fingerprintBlob = data.getBlob(INDEX_UNIFIED_FINGERPRINT);
                    String fingerprint = PgpKeyHelper.convertFingerprintToHex(fingerprintBlob);
                    mFingerprint.setText(PgpKeyHelper.colorizeFingerprint(fingerprint));

                    break;
                }
            }

            case LOADER_ID_KEYS:
                mKeysAdapter.swapCursor(data);
                break;
        }
        getActivity().setProgressBarIndeterminateVisibility(false);
        mContainer.setVisibility(View.VISIBLE);
    }

    /**
     * This is called when the last Cursor provided to onLoadFinished() above is about to be closed.
     * We need to make sure we are no longer using it.
     */
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LOADER_ID_KEYS:
                mKeysAdapter.swapCursor(null);
                break;
        }
    }

}
