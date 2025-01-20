package com.ariofrio.justhelp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<String> datalist = Arrays.asList("Elemento1","Elemento2","Elemento3","Elemento4","Elemento5","Elemento1","Elemento2","Elemento3","Elemento4","Elemento5");
        List<String> datalist2 = Arrays.asList("DIRnto1","DIRo2","DIR3","DIR4","DIR5","DIRnto1","DIRo2","DIR3","DIR4","DIR5");
        List<String> datalist3 = Arrays.asList("Ofrece","NEcesito","Ofrece","sdad","zasfasf","Ofrece","NEcesito","Ofrece","sdad","zasfasf");
        MyAdapter adapter = new MyAdapter(datalist, datalist2, datalist3);
        recyclerView.setAdapter(adapter);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void goAnadir(View view) {
        Intent intent = new Intent(MainActivity.this,AniadirO.class);
        startActivity(intent);
    }

    public void goPerfil(View view) {
        Intent intent = new Intent(MainActivity.this,Perfil.class);
        startActivity(intent);
    }
}