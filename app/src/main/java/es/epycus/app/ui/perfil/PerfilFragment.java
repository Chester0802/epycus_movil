package es.epycus.app.ui.perfil;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.api.RetrofitClient;
import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.entity.CacheEntity;
import es.epycus.app.data.local.entity.UsuarioEntity;
import es.epycus.app.databinding.FragmentPerfilBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.PerfilResponse;
import es.epycus.app.repository.AuthRepository;
import es.epycus.app.ui.auth.LoginActivity;
import es.epycus.app.util.NetworkUtils;
import es.epycus.app.util.SessionManager;
import es.epycus.app.util.ThemeManager;
import retrofit2.Call;
import retrofit2.Callback;

public class PerfilFragment extends Fragment {

    private static final String TAG = "PerfilFragment";
    private FragmentPerfilBinding binding;
    private SessionManager sessionManager;
    private AuthRepository authRepository;
    private AppDatabase database;
    private final List<Call<?>> activeCalls = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        sessionManager = SessionManager.getInstance(requireContext());
        authRepository = new AuthRepository(requireContext());
        database = AppDatabase.getInstance(requireContext());

        binding.swipeRefresh.setOnRefreshListener(this::cargarPerfil);
        binding.swipeRefresh.setColorSchemeResources(R.color.light_accent, R.color.light_accent_secondary);

        cargarPerfil();

        binding.btnCerrarSesion.setOnClickListener(v -> mostrarDialogoConfirmacionCierre());
        binding.btnPersonajes.setOnClickListener(v -> cargarPersonajes());
        binding.btnLogros.setOnClickListener(v -> cargarLogros());
        binding.btnEstadisticas.setOnClickListener(v -> mostrarEstadisticas());
        binding.btnConfiguracion.setOnClickListener(v -> mostrarDialogoConfiguracion());
        binding.btnToggleTheme.setOnClickListener(v -> {
            ThemeManager.getInstance(requireContext()).toggle();
            requireActivity().recreate();
        });
        actualizarTextoTema();

        return view;
    }

    private void cargarPerfil() {
        binding.loadingView.setVisibility(View.VISIBLE);

        Call<RespuestaApi<PerfilResponse>> call = RetrofitClient.getInstance(requireContext()).getApiPerfilService()
                .obtenerPerfil();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<PerfilResponse>> call,
                                   @NonNull retrofit2.Response<RespuestaApi<PerfilResponse>> response) {
                activeCalls.remove(call);
                binding.loadingView.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null
                        && response.body().isExito() && response.body().getDatos() != null) {
                    try {
                        PerfilResponse perfilResp = response.body().getDatos();
                        Gson gson = new Gson();
                        String json = gson.toJson(perfilResp);
                        AppDatabase.getWriteExecutor().execute(() ->
                                database.cacheDao().insert(
                                        new CacheEntity("perfil", json)));

                        PerfilResponse.Perfil perfil = perfilResp.getPerfil();
                        binding.tvNombre.setText(perfil.getNombre());
                        binding.tvCorreo.setText(perfil.getCorreoElectronico());
                        binding.tvNivel.setText(getString(R.string.nivel_formato, perfil.getNivelActual()));
                        binding.tvRacha.setText(getString(R.string.dias_formato, perfil.getRachaActual()));
                        binding.tvXp.setText(getString(R.string.xp_formato, perfil.getXpTotal()));
                        binding.tvCarrera.setText(perfil.getCarreraNombre() != null ?
                                perfil.getCarreraNombre() : getString(R.string.sin_carrera));
                        binding.tvMiembroDesde.setText(getString(R.string.miembro_desde_formato,
                                perfil.getFechaRegistro()));

                        sessionManager.saveSession(
                                sessionManager.getToken(),
                                sessionManager.getRefreshToken(),
                                sessionManager.getUserId(),
                                perfil.getNombre(),
                                perfil.getCorreoElectronico()
                        );
                        AppDatabase.getWriteExecutor().execute(() ->
                                database.usuarioDao().insert(new UsuarioEntity(
                                        sessionManager.getUserId(),
                                        perfil.getNombre(),
                                        perfil.getCorreoElectronico(),
                                        sessionManager.getToken(),
                                        sessionManager.getRefreshToken(),
                                        perfil.getFechaRegistro()
                                )));
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing perfil", e);
                        cargarDatosLocales();
                    }
                } else {
                    cargarDatosLocales();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<PerfilResponse>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                binding.loadingView.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                if (!cargarPerfilDesdeCache()) {
                    cargarDatosLocales();
                }
                mostrarErrorRed(t);
            }
        });
    }

    private boolean cargarPerfilDesdeCache() {
        String json = database.cacheDao().getValue("perfil");
        if (json == null) return false;

        try {
            Gson gson = new Gson();
            PerfilResponse perfilResp = gson.fromJson(json, PerfilResponse.class);
            PerfilResponse.Perfil perfil = perfilResp.getPerfil();
            binding.tvNombre.setText(perfil.getNombre());
            binding.tvCorreo.setText(perfil.getCorreoElectronico());
            binding.tvNivel.setText(getString(R.string.nivel_formato, perfil.getNivelActual()));
            binding.tvRacha.setText(getString(R.string.dias_formato, perfil.getRachaActual()));
            binding.tvXp.setText(getString(R.string.xp_formato, perfil.getXpTotal()));
            binding.tvCarrera.setText(perfil.getCarreraNombre() != null ?
                    perfil.getCarreraNombre() : getString(R.string.sin_carrera));
            binding.tvMiembroDesde.setText(getString(R.string.miembro_desde_formato,
                    perfil.getFechaRegistro()));
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error loading cached perfil", e);
            return false;
        }
    }

    private void actualizarTextoTema() {
        boolean isLight = ThemeManager.getInstance(requireContext()).isLightTheme();
        binding.tvToggleTheme.setText(isLight ? R.string.modo_oscuro : R.string.modo_claro);
    }

    private void cargarDatosLocales() {
        if (sessionManager.getUserId() > 0) {
            UsuarioEntity cachedUser = database.usuarioDao().getById(sessionManager.getUserId());
            if (cachedUser != null) {
                binding.tvNombre.setText(cachedUser.getNombre());
                binding.tvCorreo.setText(cachedUser.getCorreoElectronico());
                return;
            }
        }
        binding.tvNombre.setText(sessionManager.getUserName());
        binding.tvCorreo.setText(sessionManager.getUserEmail());
        binding.tvNivel.setText(getString(R.string.nivel_formato, 1));
        binding.tvRacha.setText(getString(R.string.dias_formato, 0));
        binding.tvXp.setText(getString(R.string.xp_formato, 0));
    }

    private void cargarPersonajes() {
        Call<RespuestaApi<Object>> call = RetrofitClient.getInstance(requireContext()).getApiPerfilService()
                .personajes();
        activeCalls.add(call);
        call.enqueue(new Callback<RespuestaApi<Object>>() {
            @Override
            public void onResponse(Call<RespuestaApi<Object>> call, retrofit2.Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    try {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body().getDatos());
                        mostrarPersonajesDialog(json);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), R.string.proximamente, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), R.string.proximamente, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<Object>> call, Throwable t) {
                activeCalls.remove(call);
                Toast.makeText(getContext(), R.string.error_conexion, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarPersonajesDialog(String json) {
        try {
            JsonArray arr = com.google.gson.JsonParser.parseString(json).getAsJsonArray();
            String[] nombres = new String[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                JsonObject obj = arr.get(i).getAsJsonObject();
                nombres[i] = obj.has("nombre") ? obj.get("nombre").getAsString() : "Personaje " + (i + 1);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(getString(R.string.mis_personajes));
            builder.setItems(nombres, (dialog, which) -> {
                seleccionarPersonaje(arr.get(which).getAsJsonObject());
            });
            builder.show();
        } catch (Exception e) {
            Toast.makeText(getContext(), R.string.proximamente, Toast.LENGTH_SHORT).show();
        }
    }

    private void seleccionarPersonaje(JsonObject personaje) {
        JsonObject body = new JsonObject();
        if (personaje.has("id")) {
            body.addProperty("personajeId", personaje.get("id").getAsInt());
        } else if (personaje.has("nombre")) {
            body.addProperty("personaje", personaje.get("nombre").getAsString());
        }

        Call<RespuestaApi<Object>> call = RetrofitClient.getInstance(requireContext()).getApiPerfilService()
                .cambiarPersonaje(body);
        activeCalls.add(call);
        call.enqueue(new Callback<RespuestaApi<Object>>() {
            @Override
            public void onResponse(Call<RespuestaApi<Object>> call, retrofit2.Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful()) {
                    Snackbar.make(binding.getRoot(), "Personaje actualizado", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<Object>> call, Throwable t) {
                activeCalls.remove(call);
                mostrarErrorRed(t);
            }
        });
    }

    private void cargarLogros() {
        Call<RespuestaApi<Object>> call = RetrofitClient.getInstance(requireContext()).getApiPerfilService()
                .logros();
        activeCalls.add(call);
        call.enqueue(new Callback<RespuestaApi<Object>>() {
            @Override
            public void onResponse(Call<RespuestaApi<Object>> call, retrofit2.Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    try {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body().getDatos());
                        mostrarLogrosDialog(json);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), R.string.proximamente, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), R.string.proximamente, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<Object>> call, Throwable t) {
                activeCalls.remove(call);
                Toast.makeText(getContext(), R.string.error_conexion, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarLogrosDialog(String json) {
        try {
            JsonArray arr = com.google.gson.JsonParser.parseString(json).getAsJsonArray();
            String[] nombres = new String[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                JsonObject obj = arr.get(i).getAsJsonObject();
                String nombre = obj.has("nombre") ? obj.get("nombre").getAsString() : "Logro " + (i + 1);
                String desc = obj.has("descripcion") ? obj.get("descripcion").getAsString() : "";
                nombres[i] = desc.isEmpty() ? nombre : nombre + " - " + desc;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(getString(R.string.logros));
            builder.setItems(nombres, null);
            builder.show();
        } catch (Exception e) {
            Toast.makeText(getContext(), R.string.proximamente, Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarEstadisticas() {
        try {
            String cachedProgreso = database.cacheDao().getValue("progreso");
            String cachedPerfil = database.cacheDao().getValue("perfil");

            StringBuilder stats = new StringBuilder();

            if (cachedProgreso != null) {
                JsonObject obj = com.google.gson.JsonParser.parseString(cachedProgreso).getAsJsonObject();
                stats.append("• XP Total: ").append(obj.has("xpTotal") ? obj.get("xpTotal").getAsString() : "0").append("\n");
                stats.append("• Nivel: ").append(obj.has("nivel") ? obj.get("nivel").getAsString() : "1").append("\n");
                stats.append("• Racha: ").append(obj.has("rachaActual") ? obj.get("rachaActual").getAsString() : "0").append(" días\n");
            }

            if (cachedPerfil != null) {
                JsonObject obj = com.google.gson.JsonParser.parseString(cachedPerfil).getAsJsonObject();
                if (obj.has("perfil")) {
                    JsonObject perfil = obj.get("perfil").getAsJsonObject();
                    if (perfil.has("rachaMaxima")) {
                        stats.append("• Mejor racha: ").append(perfil.get("rachaMaxima").getAsString()).append(" días\n");
                    }
                    if (perfil.has("totalHabitosCompletados")) {
                        stats.append("• Hábitos completados: ").append(perfil.get("totalHabitosCompletados").getAsString()).append("\n");
                    }
                }
            }

            if (stats.length() == 0) {
                stats.append("Carga tu perfil para ver estadísticas.");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Estadísticas");
            builder.setMessage(stats.toString());
            builder.setPositiveButton("Cerrar", null);
            builder.show();
        } catch (Exception e) {
            Toast.makeText(getContext(), R.string.proximamente, Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoConfiguracion() {
        String[] opciones = {
                "Cambiar contraseña",
                "Preferencias de notificaciones",
                "Acerca de Epycus"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Configuración");
        builder.setItems(opciones, (dialog, which) -> {
            switch (which) {
                case 0:
                    mostrarDialogoCambiarContrasena();
                    break;
                case 1:
                    Snackbar.make(binding.getRoot(), "Notificaciones: próximamente", Snackbar.LENGTH_SHORT).show();
                    break;
                case 2:
                    mostrarAcercaDe();
                    break;
            }
        });
        builder.show();
    }

    private void mostrarDialogoCambiarContrasena() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_cambiar_contrasena, null);
        android.widget.EditText etActual = view.findViewById(R.id.etContrasenaActual);
        android.widget.EditText etNueva = view.findViewById(R.id.etNuevaContrasena);
        android.widget.EditText etConfirmar = view.findViewById(R.id.etConfirmarNuevaContrasena);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cambiar contraseña");
        builder.setView(view);
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String actual = etActual.getText().toString().trim();
            String nueva = etNueva.getText().toString().trim();
            String confirmar = etConfirmar.getText().toString().trim();

            if (actual.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
                Snackbar.make(binding.getRoot(), "Completa todos los campos", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (!nueva.equals(confirmar)) {
                Snackbar.make(binding.getRoot(), "Las contraseñas no coinciden", Snackbar.LENGTH_SHORT).show();
                return;
            }

            JsonObject body = new JsonObject();
            body.addProperty("contrasenaActual", actual);
            body.addProperty("nuevaContrasena", nueva);
            body.addProperty("confirmarNuevaContrasena", confirmar);

            Call<RespuestaApi<Object>> call = RetrofitClient.getInstance(requireContext()).getApiPerfilService()
                    .cambiarContrasena(body);
            activeCalls.add(call);
            call.enqueue(new Callback<RespuestaApi<Object>>() {
                @Override
                public void onResponse(Call<RespuestaApi<Object>> call, retrofit2.Response<RespuestaApi<Object>> response) {
                    activeCalls.remove(call);
                    if (response.isSuccessful()) {
                        Snackbar.make(binding.getRoot(), "Contraseña actualizada", Snackbar.LENGTH_SHORT).show();
                    } else {
                        String msg = NetworkUtils.getErrorMessage(requireContext(), response);
                        Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RespuestaApi<Object>> call, Throwable t) {
                    activeCalls.remove(call);
                    mostrarErrorRed(t);
                }
            });
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarAcercaDe() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Acerca de Epycus");
        builder.setMessage("Epycus v" + es.epycus.app.BuildConfig.VERSION_NAME + "\n\n" +
                "Tu compañero de productividad y bienestar.\n\n" +
                "Desarrollado para ayudarte a mantener hábitos, \n" +
                "gestionar tu tiempo y mejorar tu bienestar.");
        builder.setPositiveButton("Cerrar", null);
        builder.show();
    }

    private void cerrarSesion() {
        Call<RespuestaApi<Void>> call = authRepository.logout();
        activeCalls.add(call);
        call.enqueue(new Callback<RespuestaApi<Void>>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<Void>> call,
                                   @NonNull retrofit2.Response<RespuestaApi<Void>> response) {
                activeCalls.remove(call);
                logoutAndRedirect();
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<Void>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                logoutAndRedirect();
            }
        });
    }

    private void mostrarDialogoConfirmacionCierre() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cerrar sesión");
        builder.setMessage("¿Estás seguro de que quieres cerrar sesión?");
        builder.setPositiveButton("Sí, cerrar sesión", (dialog, which) -> cerrarSesion());
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void logoutAndRedirect() {
        sessionManager.logout();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void mostrarErrorRed(Throwable t) {
        Snackbar.make(requireView(),
                getString(NetworkUtils.getNetworkErrorResId(t)), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        for (Call<?> call : activeCalls) {
            if (!call.isCanceled()) call.cancel();
        }
        activeCalls.clear();
        super.onDestroyView();
        binding = null;
    }
}