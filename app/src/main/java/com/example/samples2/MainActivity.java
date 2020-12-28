package com.example.samples2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ItemDataAdapter adapter;
    private List<Drawable> images = new ArrayList<>();
    private static final int CODE_WRITE_REQUEST = 78;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionStatus = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            generateInformation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_WRITE_REQUEST);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ListView listView = findViewById(R.id.list_view);
        FloatingActionButton floatingButton = findViewById(R.id.floating_button);

        fillImages();
        adapter = new ItemDataAdapter(this, null, new ItemRemoveClickListener() {
            @Override
            public void onRemoveClicked(int position) {
                adapter.removeItem(position);
                file.delete();
                loadInfo();
            }
        });
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ItemData itemData = adapter.getItem(i);
                Toast.makeText(MainActivity.this, itemData.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateInformation();
                loadInfo();
            }
        });
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantedResults) {
        if (requestCode == CODE_WRITE_REQUEST) {
            if (grantedResults.length > 0 &&
                    grantedResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadInfo();
            } else {
                Toast.makeText(this, "Невозможно загрузить информацию без разрешения",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void fillImages() {
        images.add(ContextCompat.getDrawable(this,
                android.R.drawable.ic_menu_report_image));
        images.add(ContextCompat.getDrawable(this,
                android.R.drawable.ic_menu_add));
        images.add(ContextCompat.getDrawable(this,
                android.R.drawable.ic_menu_agenda));
        images.add(ContextCompat.getDrawable(this,
                android.R.drawable.ic_menu_camera));
        images.add(ContextCompat.getDrawable(this,
                android.R.drawable.ic_menu_call));
    }

    private void generateInformation() {
        Random random = new Random();
        int i = adapter.getCount();
        adapter.addItem(new ItemData(
                images.get(random.nextInt(images.size())), "Project #" + (i + 1),
                "Theme of project #" + (i + 1)));
    }

    private void loadInfo() {
        if (isExternalStorageWritable()) {
            file = new File(this.getExternalFilesDir(null),
            "samples.txt");
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(file, true);
                for (int i = 0; i < adapter.getCount(); i++) {
                    fileWriter.write(adapter.getItem(i).getTitle() + " | " +
                            adapter.getItem(i).getSubtitle() + "; ");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}