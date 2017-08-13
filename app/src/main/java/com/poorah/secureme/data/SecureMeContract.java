package com.poorah.secureme.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by rahul on 11/8/17.
 */

public class SecureMeContract {

    public static final String CONTENT_AUTHORITY = SecureMeContentProvider.class.getName();

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SECURITY_MASTER = "security_master";
    public static final String PATH_SECURITY_TYPES = "security_types";
    public static final String PATH_SECURITY_SETTINGS = "security_settings";


    public static class SecurityMaster implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SECURITY_MASTER).build();

        public static final String TABLE_NAME = "security_master";

        public static final String COLUMN_TYPE = "type";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_DESCRIPTION = "description";

        public static final String COLUMN_USERNAME = "username";

        public static final String COLUMN_SECRET = "secret";

        public static final String COLUMN_LAST_CHANGED = "last_changed";

        public static Uri buildUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static class SecurityTypes implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SECURITY_TYPES).build();

        public static final String TABLE_NAME = "security_type";

        public static final String COLUMN_TYPE = "type";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_LAST_CHANGED = "last_changed";

        public static Uri buildUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static class SecuritySettings implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SECURITY_SETTINGS).build();

        public static final String TABLE_NAME = "security_settings";

        public static final String COLUMN_KEY = "key";

        public static final String COLUMN_VALUE = "value";

        public static final String COLUMN_LAST_CHANGED = "last_changed";

        public static Uri buildUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
