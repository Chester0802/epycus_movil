package es.epycus.app.ui.adapters;

import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.databinding.ItemMisionBinding;
import es.epycus.app.model.dto.MisionDto;

public class MisionAdapter extends RecyclerView.Adapter<MisionAdapter.ViewHolder> {

    private List<MisionDto> misiones = new ArrayList<>();
    private OnMisionListener listener;

    public interface OnMisionListener {
        void onCompletar(int id);
        void onEditar(MisionDto mision);
        void onVerSubtareas(MisionDto mision);
    }

    public MisionAdapter(OnMisionListener listener) {
        this.listener = listener;
    }

    public void setMisiones(List<MisionDto> misiones) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new MisionDiffCallback(this.misiones, misiones));
        this.misiones = misiones;
        result.dispatchUpdatesTo(this);
    }

    private static class MisionDiffCallback extends DiffUtil.Callback {
        private final List<MisionDto> oldList;
        private final List<MisionDto> newList;

        MisionDiffCallback(List<MisionDto> oldList, List<MisionDto> newList) {
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
            MisionDto oldItem = oldList.get(oldPos);
            MisionDto newItem = newList.get(newPos);
            return oldItem.isCompletada() == newItem.isCompletada()
                    && oldItem.getPrioridad().equals(newItem.getPrioridad())
                    && oldItem.getNombre().equals(newItem.getNombre());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMisionBinding binding = ItemMisionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MisionDto mision = misiones.get(position);
        holder.binding.tvMisionNombre.setText(mision.getNombre());
        holder.binding.tvMisionPrioridad.setText(mision.getPrioridad());
        holder.binding.tvMisionFecha.setText(mision.getFechaLimite() != null ? mision.getFechaLimite() : "");

        holder.binding.cbCompletada.setChecked(mision.isCompletada());

        if (mision.isCompletada()) {
            holder.itemView.setAlpha(0.5f);
        } else {
            holder.itemView.setAlpha(1.0f);
        }

        int colorAttr;
        switch (mision.getPrioridad()) {
            case "Alta":
                colorAttr = R.attr.epError;
                break;
            case "Media":
                colorAttr = R.attr.epWarning;
                break;
            default:
                colorAttr = R.attr.epSuccess;
        }
        TypedValue tv = new TypedValue();
        holder.itemView.getContext().getTheme().resolveAttribute(colorAttr, tv, true);
        holder.binding.tvMisionPrioridad.setTextColor(tv.data);

        holder.binding.cbCompletada.setOnClickListener(v -> {
            if (listener != null && !mision.isCompletada()) {
                listener.onCompletar(mision.getId());
            }
        });

        holder.binding.btnEditarMision.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditar(mision);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVerSubtareas(mision);
            }
        });
    }

    @Override
    public int getItemCount() { return misiones.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemMisionBinding binding;

        ViewHolder(ItemMisionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
