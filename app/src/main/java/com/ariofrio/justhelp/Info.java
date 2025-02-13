package com.ariofrio.justhelp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Info extends AppCompatActivity {
TextView e_tipo,e_titulo,e_descripcion,e_correo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info);
        Intent intent = getIntent();
        String titulo =intent.getStringExtra("titulo");
       // String direccion = intent.getStringExtra("direccion");
        String tipo = intent.getStringExtra("tipo");

        e_tipo = findViewById(R.id.texto_ofrece);
        e_tipo.setText(tipo);
        e_titulo = findViewById(R.id.titulo_anuncio);
        e_titulo.setText(titulo);
        //e_descripcion = findViewById(R.id.desctipcion_info);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}