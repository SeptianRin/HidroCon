package io.github.septianrin.hidrocon;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyValueFormatter extends ValueFormatter {

    private long referenceTimestamp; // minimum timestamp in your data set
    private DateFormat mDataFormat;
    private Date mDate;

    public MyValueFormatter(long referenceTimestamp) {
        //this.referenceTimestamp = referenceTimestamp;
        //this.mDataFormat = new SimpleDateFormat("yyyy-MM-dd");
        //this.mDate = new Date();

        this.referenceTimestamp = referenceTimestamp;
        this.mDataFormat = new SimpleDateFormat("HH:mm:ss");
        this.mDate = new Date();
    }

    @Override
    public String getFormattedValue(float value) {
        // convertedTimestamp = originalTimestamp - referenceTimestamp
        long convertedTimestamp = (long) value;

        // Retrieve original timestamp
        long originalTimestamp = referenceTimestamp + convertedTimestamp;

        // Convert timestamp to hour:minute
        return getDateString(originalTimestamp);
    }

    private String getDateString(long timestamp) {
        try {
            mDate.setTime(timestamp);
            return mDataFormat.format(mDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

    private String getHour(long timestamp) {
        try {
            mDate.setTime(timestamp * 1000);
            return mDataFormat.format(mDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

}
