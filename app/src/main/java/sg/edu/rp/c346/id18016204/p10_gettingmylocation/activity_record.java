package sg.edu.rp.c346.id18016204.p10_gettingmylocation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class activity_record extends AppCompatActivity {


    ListView lv;
    Button btnRefresh;
    TextView recordsTextview;

    ArrayList<String> coordinates;
    ArrayAdapter<String> recordsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        lv = findViewById(R.id.lv);
        btnRefresh = findViewById(R.id.btnRefresh);
        recordsTextview = findViewById(R.id.recordsTextview);

        coordinates = new ArrayList<>();
        recordsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, coordinates);
        lv.setAdapter(recordsAdapter);

        btnRefresh.setOnClickListener(view -> {
            String folderLocation = getFilesDir().getAbsolutePath() + "/LocationLogs";
            File locationLog = new File(folderLocation, "log.txt");

            if (locationLog.exists()) {
                String data = "";
                try {
                    coordinates.clear();
                    FileReader reader = new FileReader(locationLog);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String line = bufferedReader.readLine();
                    while (line != null) {
                        coordinates.add(line);
                        line = bufferedReader.readLine();
                    }
                    bufferedReader.close();
                    reader.close();
                }
                catch (Exception e) {
                    Toast.makeText(this, "Fail to read file", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                recordsAdapter.notifyDataSetChanged();
                recordsTextview.setText("" + coordinates.size());
            }
        });
    }

}