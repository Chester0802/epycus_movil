package es.epycus.app.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import es.epycus.app.R;
import es.epycus.app.api.RetrofitClient;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.DashboardResponse;
import es.epycus.app.model.dto.GamificacionResponse;
import es.epycus.app.util.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SetTextI18n")
public class InicioFragment extends Fragment {

    private TextView tvBienvenida, tvFrase, tvFraseAutor;
    private TextView tvHabitosPendientes, tvRacha, tvNivel;
    private ProgressBar pbXp;
    private TextView tvXpText;
    private View loadingView;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        sessionManager = SessionManager.getInstance(requireContext());

        tvBienvenida = view.findViewById(R.id.tvBienvenida);
        tvFrase = view.findViewById(R.id.tvFrase);
        tvFraseAutor = view.findViewById(R.id.tvFraseAutor);
        tvHabitosPendientes = view.findViewById(R.id.tvHabitosPendientes);
        tvRacha = view.findViewById(R.id.tvRacha);
        tvNivel = view.findViewById(R.id.tvNivel);
        pbXp = view.findViewById(R.id.pbXp);
        tvXpText = view.findViewById(R.id.tvXpText);
        loadingView = view.findViewById(R.id.loadingView);

        String nombre = sessionManager.getUserName();
        tvBienvenida.setText("Hola, " + (nombre != null ? nombre : "Epycus"));

        cargarDashboard();
        cargarProgreso();

        return view;
    }

    private void cargarDashboard() {
        RetrofitClient.getInstance(requireContext()).getApiDashboardService()
                .resumen().enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<RespuestaApi<Object>> call,
                                           @NonNull Response<RespuestaApi<Object>> response) {
                        loadingView.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getDatos() != null) {
                            try {
                                com.google.gson.Gson gson = new com.google.gson.Gson();
                                String json = gson.toJson(response.body().getDatos());
                                DashboardResponse data = gson.fromJson(json, DashboardResponse.class);

                                tvHabitosPendientes.setText(
                                        String.valueOf(data.getHabitosPendientes()));

                                if (data.getFrase() != null) {
                                    tvFrase.setText(data.getFrase().getFrase());
                                    tvFraseAutor.setText("- " + data.getFrase().getAutor());
                                }
                            } catch (Exception e) {
                                tvHabitosPendientes.setText("0");
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
                        loadingView.setVisibility(View.GONE);
                    }
                });
    }

    private void cargarProgreso() {
        RetrofitClient.getInstance(requireContext()).getApiGamificacionService()
                .miProgreso().enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<RespuestaApi<Object>> call,
                                           @NonNull Response<RespuestaApi<Object>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getDatos() != null) {
                            try {
                                com.google.gson.Gson gson = new com.google.gson.Gson();
                                String json = gson.toJson(response.body().getDatos());
                                GamificacionResponse data = gson.fromJson(json, GamificacionResponse.class);

                                tvRacha.setText(String.valueOf(data.getRachaActual()));
                                tvNivel.setText("Nv." + data.getNivel());
                                pbXp.setProgress((int) data.getPorcentajeProgreso());
                                tvXpText.setText(data.getXpTotal() + " XP");
                            } catch (Exception ignored) {}
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RespuestaApi<Object>> call, @NonNull Throwable t) {}
                });
    }
}
