package es.epycus.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.databinding.ItemSubtareaBinding;
import es.epycus.app.model.dto.SubTareaResponse;

public class SubTareaAdapter extends RecyclerView.Adapter<SubTareaAdapter.ViewHolder> {

    private List<SubTareaResponse> subtareas = new ArrayList<>();
    private OnSubTareaListener listener;

    public interface OnSubTareaListener {
        void onCompletar(int misionId, int id);
        void onDescompletar(int misionId, int id);
        void onEditar(SubTareaResponse subtarea);
        void onEliminar(int misionId, int id);
    }

    public SubTareaAdapter(OnSubTareaListener listener) {
        this.listener = listener;
    }

    public void setSubTareas(List<SubTareaResponse> subtareas) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new SubTareaDiffCallback(this.subtareas, subtareas));
        this.subtareas = subtareas;
        result.dispatchUpdatesTo(this);
    }

    private static class SubTareaDiffCallback extends DiffUtil.Callback {
        private final List<SubTareaResponse> oldList;
        private final List<SubTareaResponse> newList;

        SubTareaDiffCallback(List<SubTareaResponse> oldList, List<SubTareaResponse> newList) {
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
            SubTareaResponse oldItem = oldList.get(oldPos);
            SubTareaResponse newItem = newList.get(newPos);
            return oldItem.isEstaCompletada() == newItem.isEstaCompletada()
                    && oldItem.getNombre().equals(newItem.getNombre());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSubtareaBinding binding = ItemSubtareaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubTareaResponse subtarea = subtareas.get(position);
        holder.binding.tvSubTareaNombre.setText(subtarea.getNombre());
        holder.binding.tvSubTareaOrden.setText(holder.itemView.getContext().getString(R.string.orden_formato, subtarea.getOrden()));
        holder.binding.tvSubTareaTiempo.setText(subtarea.getTiempoEnfoqueFormateado() != null ? subtarea.getTiempoEnfoqueFormateado() : "");

        holder.binding.cbSubTareaCompletada.setChecked(subtarea.isEstaCompletada());

        if (subtarea.isEstaCompletada()) {
            holder.itemView.setAlpha(0.5f);
        } else {
            holder.itemView.setAlpha(1.0f);
        }

        holder.binding.cbSubTareaCompletada.setOnClickListener(v -> {
            if (listener != null) {
                if (subtarea.isEstaCompletada()) {
                    listener.onDescompletar(subtarea.getMisionId(), subtarea.getId());
                } else {
                    listener.onCompletar(subtarea.getMisionId(), subtarea.getId());
                }
            }
        });

        holder.binding.btnEditarSubTarea.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditar(subtarea);
            }
        });

        holder.binding.btnEliminarSubTarea.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEliminar(subtarea.getMisionId(), subtarea.getId());
            }
        });
    }

    @Override
    public int getItemCount() { return subtareas.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemSubtareaBinding binding;

        ViewHolder(ItemSubtareaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}