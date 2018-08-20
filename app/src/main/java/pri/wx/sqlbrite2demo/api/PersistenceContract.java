package pri.wx.sqlbrite2demo.api;

import android.provider.BaseColumns;

public class PersistenceContract {

    private PersistenceContract() {
    }

    public static abstract class UserEntry implements BaseColumns{
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
    }
}
