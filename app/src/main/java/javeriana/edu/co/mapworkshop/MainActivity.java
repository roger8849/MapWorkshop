package javeriana.edu.co.mapworkshop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void launchLocalization(View view){
        Intent permissions = new Intent(this, LocalizationActivity.class);
        startActivity(permissions);
    }

    public void launchMaps(View view){
        Intent permissions = new Intent(this, MapsActivity.class);
        startActivity(permissions);
    }
}
