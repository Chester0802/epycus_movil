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
import es.epycus.app.model.dto.HabitoHoyDto;

public class HabitoHoyAdapter extends RecyclerView.Adapter<HabitoHoyAdapter.ViewHolder> {

    private List<HabitoHoyDto> habitos = new ArrayList<>();
    private OnHabitoListener listener;

    public interface OnHabitoListener {
        void onCompletar(int id);
        void onFallar(int id);
    }

    public HabitoHoyAdapter(OnHabitoListener listener) {
        this.listener = listener;
    }

    public void setHabitos(List<HabitoHoyDto> habitos) {
        this.habitos = habitos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habito_hoy, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HabitoHoyDto habito = habitos.get(position);
        holder.tvNombre.setText(habito.getNombre());
        holder.tvCategoria.setText(habito.getCategoria());
        holder.tvXp.setText(habito.getXpPotencial() + " XP");

        if (habito.isCompletado()) {
            holder.itemView.setAlpha(0.5f);
        } else {
            holder.itemView.setAlpha(1.0f);
        }

        holder.itemView.setOnClickListener(v -> {
            if (habito.isPendiente()) {
                listener.onCompletar(habito.getId());
            }
        });
    }

    @Override
    public int getItemCount() { return habitos.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCategoria, tvXp;

        ViewHolder(View view) {
            super(view);
            tvNombre = view.findViewById(R.id.tvHabitoNombre);
            tvCategoria = view.findViewById(R.id.tvHabitoCategoria);
            tvXp = view.findViewById(R.id.tvHabitoXp);
        }
    }
}
