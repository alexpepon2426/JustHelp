package com.ariofrio.justhelp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<String> dataList, dataList2, dataList3, imagenes;
    private String correo;

    public MyAdapter(List<String> dataList, List<String> dataList2, List<String> dataList3, String correo, List<String> imagenes) {
        this.dataList = dataList;
        this.dataList2 = dataList2;
        this.dataList3 = dataList3;
        this.correo = correo;
        this.imagenes = imagenes;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        holder.textView.setText(dataList.get(position));
        holder.textView2.setText(dataList2.get(position));
        holder.textView3.setText(dataList3.get(position));

        if (dataList3.get(position).equals("Necesito")) {
            holder.textView3.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.darkorange));
        }

        // Cargar imagen con Glide
        String imagenUrl = imagenes.get(position);
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imagenUrl)
                    .placeholder(R.drawable.usuariott)
                    .error(R.drawable.usuariott)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.usuariott);
        }

        // Configurar click en cada item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Info.class);
            intent.putExtra("titulo", dataList.get(position));
            intent.putExtra("direccion", dataList2.get(position));
            intent.putExtra("tipo", dataList3.get(position));
            intent.putExtra("correo", correo);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView, textView2, textView3;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView_titulo);
            textView2 = itemView.findViewById(R.id.textView_direccion);
            textView3 = itemView.findViewById(R.id.textView_tipo);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
