package com.example.samples2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ItemDataAdapter adapter;
    private final List<Drawable> images = new ArrayList<>();
    private File file;
    private static final String divider = ";";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        file = new File(this.getExternalFilesDir(null), "samples_1.txt");

        adapter = new ItemDataAdapter(this, null, position ->  {
                adapter.removeItem(position);
                writeAll();
        });
        fillImages();

        readInfo();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ListView listView = findViewById(R.id.list_view);
        FloatingActionButton floatingButton = findViewById(R.id.floating_button);

        listView.setAdapter(adapter);


        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            ItemData itemData = adapter.getItem(i);
            Toast.makeText(MainActivity.this, itemData.getTitle(), Toast.LENGTH_SHORT).show();
            return true;
        });

        floatingButton.setOnClickListener(view -> {
            ItemData item = generateInformation();
            writeItem(item);
            adapter.addItem(item);
        });
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

    private ItemData generateInformation() {
        Random random = new Random();
        int i = adapter.getCount();
        return new ItemData(
                images.get(random.nextInt(images.size())), "Project #" + (i + 1),
                "Theme of project #" + (i + 1));
    }

    private void readInfo() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] strings = line.split(divider);
                Drawable image = images.get(Integer.parseInt(strings[0]));
                String title = strings[1];
                String subtitle = strings[2];
                adapter.addItem(new ItemData(image, title, subtitle));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeItem(ItemData item) {
        if(isExternalStorageWritable()) {
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writeItem(writer,item);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeAll() {
        if(isExternalStorageWritable()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                for(int i = 0; i < adapter.getCount(); i++) {
                    writeItem(writer, adapter.getItem(i));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeItem(BufferedWriter writer, ItemData item) throws IOException {
        writer.write(images.indexOf(item.getImage()) + divider +
                item.getTitle() + divider +
                item.getSubtitle() + divider);
        writer.newLine();
    }
}