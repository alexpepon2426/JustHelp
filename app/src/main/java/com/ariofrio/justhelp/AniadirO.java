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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;




public class AniadirO extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String correo;
    private Spinner spin;
    private String tipo;
    Map<String, Object> anuncio = new HashMap<>();
    String s_direccion, s_titulo, s_descripcion, s_categoria, id_anuncio,fecha;//COncat correo y el titulo del anuncio
    EditText e_direccion, e_titulo, e_descripcion, e_categoria;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aniadir_o);
        Intent intent=getIntent();
        correo=intent.getStringExtra("correo");

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








        //****************************************



        myfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s_direccion = e_direccion.getText().toString();
                s_titulo = e_titulo.getText().toString();
                s_descripcion = e_descripcion.getText().toString();
                id_anuncio=correo+s_titulo;



                db.collection("Anuncios").document(id_anuncio)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // El documento ya existe
                                Toast.makeText(AniadirO.this, "Ya tienes un anuncio con ese título.", Toast.LENGTH_SHORT).show();
                            } else {
// El documento no existe, puedes proceder a registrarlo
                                registrarFirebase(id_anuncio, s_titulo, s_direccion,s_categoria, correo, tipo,s_descripcion);

                            }
                        })
                        .addOnFailureListener(e -> {
                            // Manejar el error al intentar comprobar la existencia
                            Toast.makeText(AniadirO.this, "Error. Vuelve a intentarlo en unos minutos... " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });


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
    private void registrarFirebase(String id_anuncio,String s_titulo, String s_direccion,String s_categoria, String correo, String tipo, String s_descripcion) {
        if(!(s_titulo.isEmpty())&&!(s_descripcion.isEmpty())&&!(s_direccion.isEmpty())){
            Map<String, Object> anuncio = new HashMap<>();
            //usuario.put("id", 12345); // Número entero
            anuncio.put("id", id_anuncio); // Cadena
            anuncio.put("titulo", s_titulo); // Cadena
            anuncio.put("direccion", s_direccion); // String
            anuncio.put("categoria", s_categoria); // String
            anuncio.put("correo", correo); // Número entero
            anuncio.put("tipo", tipo); // Cadena (pero recuerda encriptar contraseñas en producción)
            anuncio.put("descripcion", s_descripcion);
            anuncio.put("fecha", new Timestamp(new Date()));
            //Posibilidad de subir la fecha de alta ***
            //usuario.put("edad", 30); // Ejemplo adicional de un campo numérico
            //usuario.put("activo", true); // Booleano, puede usarse para indicar si un usuario está activo




            db.collection("Anuncios").document(id_anuncio)
                    .set(anuncio)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AniadirO.this, "Anuncio agregado exitosamente!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(AniadirO.this, MainActivity.class);
                            intent.putExtra("correo",correo);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AniadirO.this, "Error al agregar anuncio.", Toast.LENGTH_SHORT).show();
                            //Log.w(TAG, "Error al agregar usuario", e);
                        }
                    });
        }else{
            Toast.makeText(this, "Por favor complete todos los compos", Toast.LENGTH_SHORT).show();
        }
    }

    public void goPerfil(View view) {
        Intent intent = new Intent(this,Perfil.class);
        intent.putExtra("correo",correo);
        startActivity(intent);
        finish();
    }

    public void goMain(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("correo",correo);
        startActivity(intent);
        finish();
    }
}

