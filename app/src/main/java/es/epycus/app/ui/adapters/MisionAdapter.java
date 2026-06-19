package es.epycus.app.ui.adapters;

import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.model.dto.MisionDto;

public class MisionAdapter extends RecyclerView.Adapter<MisionAdapter.ViewHolder> {

    private List<MisionDto> misiones = new ArrayList<>();
    private OnMisionListener listener;

    public interface OnMisionListener {
        void onCompletar(int id);
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mision, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MisionDto mision = misiones.get(position);
        holder.tvNombre.setText(mision.getNombre());
        holder.tvPrioridad.setText(mision.getPrioridad());
        holder.tvFecha.setText(mision.getFechaLimite());

        if (mision.isCompletada()) {
            holder.itemView.setAlpha(0.5f);
        }

        int color;
        switch (mision.getPrioridad()) {
            case "Alta":
                color = ContextCompat.getColor(holder.itemView.getContext(), R.color.priority_alta);
                break;
            case "Media":
                color = ContextCompat.getColor(holder.itemView.getContext(), R.color.priority_media);
                break;
            default:
                color = ContextCompat.getColor(holder.itemView.getContext(), R.color.priority_baja);
        }
        holder.tvPrioridad.setTextColor(color);
    }

    @Override
    public int getItemCount() { return misiones.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPrioridad, tvFecha;

        ViewHolder(View view) {
            super(view);
            tvNombre = view.findViewById(R.id.tvMisionNombre);
            tvPrioridad = view.findViewById(R.id.tvMisionPrioridad);
            tvFecha = view.findViewById(R.id.tvMisionFecha);
        }
    }
}
