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
        this.misiones = misiones;
        notifyDataSetChanged();
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
            case "Alta": color = 0xFFEF4444; break;
            case "Media": color = 0xFFF59E0B; break;
            default: color = 0xFF22C55E;
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
