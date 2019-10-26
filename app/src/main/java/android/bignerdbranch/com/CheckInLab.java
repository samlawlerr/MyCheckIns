package android.bignerdbranch.com;

import android.bignerdbranch.com.CrimeDbSchema.CheckInCursorWrapper;
import android.bignerdbranch.com.CrimeDbSchema.CheckInDbSchema;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.bignerdbranch.com.CrimeDbSchema.CheckInBaseHelper;

public class CheckInLab {
    private static CheckInLab sCheckLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CheckInLab get(Context context) {
        if (sCheckLab == null) {
            sCheckLab = new CheckInLab(context);
        }
        return sCheckLab;
    }
    private CheckInLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CheckInBaseHelper(mContext)
                .getWritableDatabase();

    }

    public void addCheckIn(CheckIn c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(CheckInDbSchema.CheckInTable.NAME, null, values);

    }


    public List<CheckIn> getCheckIn() {
        List<CheckIn> checks = new ArrayList<>();
        CheckInCursorWrapper cursor = queryChecks(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                checks.add(cursor.getCheckIn());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return checks;
    }

    public CheckIn getCheckIn(UUID id) {
        CheckInCursorWrapper cursor = queryChecks(
                CheckInDbSchema.CheckInTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCheckIn();
        } finally {
            cursor.close();
        }

    }

    public File getPhotoFile(CheckIn check) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, check.getPhotoFilename());
    }

    public void deleteCheckIn(CheckIn check) {
        String uuidString = check.getId().toString();
        ContentValues values = getContentValues(check);

        mDatabase.delete(CheckInDbSchema.CheckInTable.NAME, "uuid =" + '"' + check.getId() + '"', null);
    }

    public void updateCheckIn(CheckIn check) {
        String uuidString = check.getId().toString();
        ContentValues values = getContentValues(check);
        mDatabase.update(CheckInDbSchema.CheckInTable.NAME, values,
                CheckInDbSchema.CheckInTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private CheckInCursorWrapper queryChecks(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CheckInDbSchema.CheckInTable.NAME,
                null,
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new CheckInCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(CheckIn check) {
        ContentValues values = new ContentValues();
        values.put(CheckInDbSchema.CheckInTable.Cols.UUID, check.getId().toString());
        values.put(CheckInDbSchema.CheckInTable.Cols.TITLE, check.getTitle());
        values.put(CheckInDbSchema.CheckInTable.Cols.DATE, check.getDate().getTime());
        values.put(CheckInDbSchema.CheckInTable.Cols.DETAILS, check.getDetails());
        values.put(CheckInDbSchema.CheckInTable.Cols.PLACE, check.getPlace());
        values.put(CheckInDbSchema.CheckInTable.Cols.LOCATION, check.getLocation());
        return values;
    }
}

