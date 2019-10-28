package database;

import android.bignerdranch.mycheckins.Item;
import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import database.ItemDbSchema.ItemTable;

public class ItemCursorWrapper extends CursorWrapper {

    public ItemCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Item getItem() {
        String uuidString = getString(getColumnIndex(ItemTable.Cols.UUID));
        String title = getString(getColumnIndex(ItemTable.Cols.TITLE));
        long date = getLong(getColumnIndex(ItemTable.Cols.DATE));
        String friend = getString(getColumnIndex(ItemTable.Cols.FRIEND));
        String place = getString(getColumnIndex(ItemTable.Cols.PLACE));
        String details = getString(getColumnIndex(ItemTable.Cols.DETAILS));

        // Fill item variables
        Item item = new Item(UUID.fromString(uuidString));
        item.setTitle(title);
        item.setDate(new Date(date));
        item.setFriend(friend);
        item.setPlace(place);
        item.setDetails(details);

        return item;
    }
}
