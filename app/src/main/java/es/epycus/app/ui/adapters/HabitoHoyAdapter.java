package es.epycus.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.model.dto.HabitoHoyDto;

public class HabitoHoyAdapter extends RecyclerView.Adapter<HabitoHoyAdapter.ViewHolder> {

    private static final long DEBOUNCE_INTERVAL = 500;
    private List<HabitoHoyDto> habitos = new ArrayList<>();
    private OnHabitoListener listener;
    private long lastClickTime = 0;
    private int lastClickedId = -1;

    public interface OnHabitoListener {
        void onCompletar(int id);
        void onFallar(int id);
    }

    public HabitoHoyAdapter(OnHabitoListener listener) {
        this.listener = listener;
    }

    public void setHabitos(List<HabitoHoyDto> habitos) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new HabitoDiffCallback(this.habitos, habitos));
        this.habitos = habitos;
        result.dispatchUpdatesTo(this);
    }

    private static class HabitoDiffCallback extends DiffUtil.Callback {
        private final List<HabitoHoyDto> oldList;
        private final List<HabitoHoyDto> newList;

        HabitoDiffCallback(List<HabitoHoyDto> oldList, List<HabitoHoyDto> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() { return oldList.size(); }

        @Override
        public int getNewListSize() { return newList.size(); }

        @Override
        public boolean areItemsTheSame(int oldPos, int newPos) {
            return oldList.get(oldPos).getId() == newList.get(newPos).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldPos, int newPos) {
            HabitoHoyDto oldItem = oldList.get(oldPos);
            HabitoHoyDto newItem = newList.get(newPos);
            return oldItem.isCompletado() == newItem.isCompletado()
                    && oldItem.isPendiente() == newItem.isPendiente()
                    && oldItem.getNombre().equals(newItem.getNombre());
        }
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
        holder.tvXp.setText(holder.itemView.getContext().getString(
                R.string.xp_formato, habito.getXpPotencial()));

        if (habito.isCompletado()) {
            holder.itemView.setAlpha(0.5f);
        } else {
            holder.itemView.setAlpha(1.0f);
        }

        holder.itemView.setOnClickListener(v -> {
            long now = System.currentTimeMillis();
            int habitoId = habito.getId();
            if (habitoId == lastClickedId && now - lastClickTime < DEBOUNCE_INTERVAL) return;
            lastClickTime = now;
            lastClickedId = habitoId;

            if (habito.isPendiente()) {
                listener.onCompletar(habitoId);
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
