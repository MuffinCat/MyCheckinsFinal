package database;

public class ItemDbSchema {

    public static final class ItemTable {
        public static final String NAME = "items";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String FRIEND = "friend";
            public static final String PLACE = "place";
            public static final String DETAILS = "details";
        }
    }
}
