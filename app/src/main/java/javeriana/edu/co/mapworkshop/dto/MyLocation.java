package javeriana.edu.co.mapworkshop.dto;

import android.os.Build;
import android.support.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneOffset.UTC;

public class MyLocation {
    private String latitude;
    private String longitude;
    private String dateString;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MyLocation(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.setDateString();
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDateString(){
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE LLL dd HH:mm:ss");
        this.dateString = localDateTime.format(formatter);
    }

    public String getDateString() {
        return this.dateString;
    }

    public JSONObject toJSON () {
        JSONObject obj = new JSONObject();
        try {
            obj.put("latitud", getLatitude());
            obj.put("longitud", getLongitude());
            obj.put("date", getDateString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public String toString() {
        OffsetDateTime utc = OffsetDateTime.now(UTC);
        StringBuilder locationBuilder = new StringBuilder(System.getProperty("line.separator"));
        locationBuilder.append(" ").append(getLatitude());
        locationBuilder.append(" ").append(getLongitude()).append(" on ");
        locationBuilder.append( this.dateString );
        return super.toString();
    }
}
