package com.ariofrio.justhelp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import android.service.controls.actions.FloatAction;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class AniadirO extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner spin;
    private String tipo;
    private String id_anuncio; //COncat correo y el titulo del anuncio
    String s_direccion, s_titulo, s_descripcion, s_categoria;
    EditText e_direccion, e_titulo, e_descripcion, e_categoria;

    String correo; //TIENE QUE RECIBIRSE DESDE EL INTENT

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aniadir_o);

        tipo = "Ofrezco";
        TextView titulo = findViewById(R.id.titulo);
        Switch s_tipo = findViewById(R.id.switch_tipo);
        e_direccion = findViewById(R.id.direccion2);
        e_descripcion= findViewById(R.id.desc2);
        e_titulo= findViewById(R.id.titulo2);



        s_tipo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // Código cuando el Switch está encendido
                    tipo = "Necesito";
                    Log.d("Switch", "El Switch está ENCENDIDO");
                } else {
                    // Código cuando el Switch está apagado

                    tipo = "Ofrezco";
                }
                titulo.setText(tipo);
            }
        });

        spin=findViewById(R.id.spinner);
        ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this,R.array.categorias, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s_categoria = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                s_categoria = adapterView.getItemAtPosition(0).toString();
            }
        });
        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
        FloatingActionButton myfab = findViewById(R.id.fab);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //*************** CONEXION A BASE DE DATOS



                // Comprobar que el correo no exista en BBDD














        //****************************************



        myfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s_direccion = e_direccion.getText().toString();
                s_titulo = e_titulo.getText().toString();
                s_descripcion = e_descripcion.getText().toString();

                //chequear que s_titulo no exista en los anuncios del usuario

                //Copiar a partir de la linea 67 de Registro.java *************************** !!!


                Toast.makeText(AniadirO.this, "Vas a crear un Anuncio", Toast.LENGTH_SHORT).show();
            }
        });
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.person)
                    Toast.makeText(AniadirO.this, "Has pulsado usuario", Toast.LENGTH_SHORT).show();
                if( item.getItemId()==R.id.location)
                    Toast.makeText(AniadirO.this, "Has pulsado localización", Toast.LENGTH_SHORT).show();

                return false;
            }
        });


    }
}

