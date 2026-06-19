package es.epycus.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.databinding.ItemChatEdyBinding;
import es.epycus.app.databinding.ItemChatUsuarioBinding;

public class MensajeChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MAX_MENSAJES = 200;
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
        if (mensajes.size() > MAX_MENSAJES) {
            mensajes.remove(0);
            notifyItemRemoved(0);
            notifyItemInserted(mensajes.size() - 1);
        } else {
            notifyItemInserted(mensajes.size() - 1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mensajes.get(position).isEsUsuario() ? 0 : 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 0) {
            ItemChatUsuarioBinding binding = ItemChatUsuarioBinding.inflate(inflater, parent, false);
            return new UsuarioViewHolder(binding);
        } else {
            ItemChatEdyBinding binding = ItemChatEdyBinding.inflate(inflater, parent, false);
            return new EdyViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);
        if (holder instanceof UsuarioViewHolder) {
            ((UsuarioViewHolder) holder).binding.tvMensaje.setText(mensaje.getTexto());
        } else if (holder instanceof EdyViewHolder) {
            ((EdyViewHolder) holder).binding.tvMensaje.setText(mensaje.getTexto());
        }
    }

    @Override
    public int getItemCount() { return mensajes.size(); }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        final ItemChatUsuarioBinding binding;

        UsuarioViewHolder(ItemChatUsuarioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    static class EdyViewHolder extends RecyclerView.ViewHolder {
        final ItemChatEdyBinding binding;

        EdyViewHolder(ItemChatEdyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
