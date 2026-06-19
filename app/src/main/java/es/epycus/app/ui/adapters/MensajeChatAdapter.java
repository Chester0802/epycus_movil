package es.epycus.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.epycus.app.R;

public class MensajeChatAdapter extends RecyclerView.Adapter<MensajeChatAdapter.ViewHolder> {

    private List<Mensaje> mensajes = new ArrayList<>();

    public static class Mensaje {
        private final String texto;
        private final boolean esUsuario;

        public Mensaje(String texto, boolean esUsuario) {
            this.texto = texto;
            this.esUsuario = esUsuario;
        }

        public String getTexto() { return texto; }
        public boolean isEsUsuario() { return esUsuario; }
    }

    public void addMensaje(Mensaje mensaje) {
        mensajes.add(mensaje);
        notifyItemInserted(mensajes.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return mensajes.get(position).isEsUsuario() ? 0 : 1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = viewType == 0 ? R.layout.item_chat_usuario : R.layout.item_chat_edy;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvMensaje.setText(mensajes.get(position).getTexto());
    }

    @Override
    public int getItemCount() { return mensajes.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMensaje;

        ViewHolder(View view) {
            super(view);
            tvMensaje = view.findViewById(R.id.tvMensaje);
        }
    }
}
