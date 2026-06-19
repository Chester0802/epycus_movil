package es.epycus.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.databinding.ItemHabitoHoyBinding;
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
        void onEditar(int id);
        void onEliminar(int id);
    }

    public HabitoHoyAdapter(OnHabitoListener listener) {
        this.listener = listener;
    }

    public OnHabitoListener getListener() {
        return listener;
    }

    public HabitoHoyDto getHabitoAt(int position) {
        return habitos.get(position);
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
        ItemHabitoHoyBinding itemBinding = ItemHabitoHoyBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HabitoHoyDto habito = habitos.get(position);
        holder.binding.tvHabitoNombre.setText(habito.getNombre());
        holder.binding.tvHabitoCategoria.setText(habito.getCategoria());
        holder.binding.tvHabitoXp.setText(holder.binding.getRoot().getContext().getString(
                R.string.xp_formato, habito.getXpPotencial()));

        if (habito.isCompletado()) {
            holder.binding.cbCompletado.setChecked(true);
            holder.itemView.setAlpha(0.5f);
        } else if (habito.isFallado()) {
            holder.binding.cbCompletado.setChecked(false);
            holder.itemView.setAlpha(0.5f);
        } else {
            holder.binding.cbCompletado.setChecked(false);
            holder.itemView.setAlpha(1.0f);
        }

        holder.binding.cbCompletado.setOnClickListener(v -> {
            long now = System.currentTimeMillis();
            int habitoId = habito.getId();
            if (habitoId == lastClickedId && now - lastClickTime < DEBOUNCE_INTERVAL) return;
            lastClickTime = now;
            lastClickedId = habitoId;

            if (habito.isPendiente() || habito.isFallado()) {
                listener.onCompletar(habitoId);
            } else {
                listener.onFallar(habitoId);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            String[] opciones = {"Completar", "Fallar", "Editar", "Eliminar"};
            androidx.appcompat.app.AlertDialog.Builder builder =
                    new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
            builder.setItems(opciones, (dialog, which) -> {
                switch (which) {
                    case 0: listener.onCompletar(habito.getId()); break;
                    case 1: listener.onFallar(habito.getId()); break;
                    case 2: listener.onEditar(habito.getId()); break;
                    case 3: listener.onEliminar(habito.getId()); break;
                }
            });
            builder.show();
            return true;
        });
    }

    @Override
    public int getItemCount() { return habitos.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemHabitoHoyBinding binding;

        ViewHolder(ItemHabitoHoyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
