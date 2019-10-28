package android.bignerdranch.mycheckins;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.ItemBaseHelper;
import database.ItemCursorWrapper;
import database.ItemDbSchema.ItemTable;

public class ItemLab {

    private static ItemLab sItemLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    /* Constructor */
    private ItemLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ItemBaseHelper(mContext).getWritableDatabase();
    }

    /* Methods */
    public static ItemLab get(Context context) {
        if (sItemLab == null) {
            sItemLab = new ItemLab(context);
        }
        return sItemLab;
    }

    /**
     * Add an item to the database
     * @param i
     */
    public void addItem(Item i) {
        ContentValues values = getContentValues(i);
        mDatabase.insert(ItemTable.NAME, null, values);
    }

    /**
     * Get the item with this id
     * @param id
     * @return
     */
    public Item getItem(UUID id) {
        ItemCursorWrapper cursor = queryItems(
                ItemTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getItem();

        } finally {
            cursor.close();
        }
    }

    /**
     * Get a list of items
     * @return
     */
    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        ItemCursorWrapper cursor = queryItems(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                items.add(cursor.getItem());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return items;
    }

    public void updateItem(Item item) {
        String uuidString = item.getId().toString();
        ContentValues values = getContentValues(item);
        mDatabase.update(ItemTable.NAME, values,
                ItemTable.Cols.UUID + " = ?",
                new String[] {uuidString});
    }

    public void deleteItem(Item item) {
        String uuidString = item.getId().toString();
        mDatabase.delete(ItemTable.NAME, ItemTable.Cols.UUID + " = ?",
                new String[] {uuidString});
    }

    public File getPhotoFile(Item item) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, item.getPhotoFilename());
    }

    private ItemCursorWrapper queryItems(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ItemTable.NAME,
                null, // all columns selected
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new ItemCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Item item) {
        ContentValues values = new ContentValues();
        values.put(ItemTable.Cols.UUID, item.getId().toString());
        values.put(ItemTable.Cols.TITLE, item.getTitle());
        values.put(ItemTable.Cols.DATE, item.getDate().getTime());
        values.put(ItemTable.Cols.FRIEND, item.getFriend());
        values.put(ItemTable.Cols.PLACE, item.getPlace());
        values.put(ItemTable.Cols.DETAILS, item.getDetails());

        return values;
    }
}
