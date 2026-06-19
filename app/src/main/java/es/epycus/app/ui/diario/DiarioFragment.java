package es.epycus.app.ui.diario;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import es.epycus.app.R;
import es.epycus.app.api.RetrofitClient;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.ui.ia.IaChatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiarioFragment extends Fragment {

    private View selectedMood;
    private String selectedMoodText = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diario, container, false);

        View moodGenial = view.findViewById(R.id.moodGenial);
        View moodBien = view.findViewById(R.id.moodBien);
        View moodNormal = view.findViewById(R.id.moodNormal);
        View moodCansado = view.findViewById(R.id.moodCansado);
        View moodEstresado = view.findViewById(R.id.moodEstresado);

        View.OnClickListener moodListener = v -> {
            if (selectedMood != null) {
                selectedMood.setBackgroundResource(R.drawable.bg_card_rounded);
            }
            selectedMood = v;
            v.setBackgroundResource(R.drawable.bg_accent_gradient);

            if (v.getId() == R.id.moodGenial) selectedMoodText = "Genial";
            else if (v.getId() == R.id.moodBien) selectedMoodText = "Bien";
            else if (v.getId() == R.id.moodNormal) selectedMoodText = "Normal";
            else if (v.getId() == R.id.moodCansado) selectedMoodText = "Cansado";
            else if (v.getId() == R.id.moodEstresado) selectedMoodText = "Estresado";
        };

        moodGenial.setOnClickListener(moodListener);
        moodBien.setOnClickListener(moodListener);
        moodNormal.setOnClickListener(moodListener);
        moodCansado.setOnClickListener(moodListener);
        moodEstresado.setOnClickListener(moodListener);

        view.findViewById(R.id.btnGuardarAnimo).setOnClickListener(v -> {
            if (selectedMoodText.isEmpty()) {
                Snackbar.make(v, "Selecciona como te sientes", Snackbar.LENGTH_SHORT).show();
                return;
            }
            guardarAnimo(selectedMoodText);
        });

        view.findViewById(R.id.btnChatEdy).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), IaChatActivity.class));
        });

        cargarPreguntaGuia(view);

        return view;
    }

    private void guardarAnimo(String estado) {
        com.google.gson.JsonObject body = new com.google.gson.JsonObject();
        body.addProperty("estado", estado);

        RetrofitClient.getInstance(requireContext()).getApiEstadoAnimoService()
                .registrar(body).enqueue(new Callback<RespuestaApi<Object>>() {
                    @Override
                    public void onResponse(Call<RespuestaApi<Object>> call,
                                           Response<RespuestaApi<Object>> response) {
                        if (response.isSuccessful()) {
                            Snackbar.make(requireView(), "Estado de animo guardado",
                                    Snackbar.LENGTH_SHORT).show();
                            if (selectedMood != null) {
                                selectedMood.setBackgroundResource(R.drawable.bg_card_rounded);
                                selectedMood = null;
                                selectedMoodText = "";
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RespuestaApi<Object>> call, Throwable t) {
                        Snackbar.make(requireView(), "Error de conexion",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarPreguntaGuia(View view) {
        TextView tvPregunta = view.findViewById(R.id.tvPreguntaGuia);

        RetrofitClient.getInstance(requireContext()).getApiDiarioService()
                .preguntaGuia().enqueue(new Callback<RespuestaApi<Object>>() {
                    @Override
                    public void onResponse(Call<RespuestaApi<Object>> call,
                                           Response<RespuestaApi<Object>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getDatos() != null) {
                            try {
                                com.google.gson.Gson gson = new com.google.gson.Gson();
                                String json = gson.toJson(response.body().getDatos());
                                com.google.gson.JsonObject obj = gson.fromJson(json,
                                        com.google.gson.JsonObject.class);
                                if (obj.has("pregunta")) {
                                    tvPregunta.setText(obj.get("pregunta").getAsString());
                                }
                            } catch (Exception ignored) {}
                        }
                    }

                    @Override
                    public void onFailure(Call<RespuestaApi<Object>> call, Throwable t) {}
                });
    }
}
