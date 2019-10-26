package android.bignerdbranch.com.CrimeDbSchema;

import android.bignerdbranch.com.CheckIn;
import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

public class CheckInCursorWrapper extends CursorWrapper {
    public CheckInCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public CheckIn getCheckIn() {
        String uuidString = getString(getColumnIndex(CheckInDbSchema.CheckInTable.Cols.UUID));
        String title = getString(getColumnIndex(CheckInDbSchema.CheckInTable.Cols.TITLE));
        Long date = getLong(getColumnIndex(CheckInDbSchema.CheckInTable.Cols.DATE));
        String details = getString(getColumnIndex(CheckInDbSchema.CheckInTable.Cols.DETAILS));
        String place = getString(getColumnIndex(CheckInDbSchema.CheckInTable.Cols.PLACE));
        String location = getString(getColumnIndex(CheckInDbSchema.CheckInTable.Cols.LOCATION));


        CheckIn check = new CheckIn(UUID.fromString(uuidString));
        check.setTitle(title);
        check.setDate(new Date(date));
        check.setDetails(details);
        check.setPlace(place);
        check.getLocation();
        return check;
    }
}

