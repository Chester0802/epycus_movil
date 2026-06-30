# Ejecuta los flujos de Maestro cargando las credenciales locales (no versionadas).
#
# Uso:
#   .\.maestro\run.ps1                  # ejecuta todos los flujos de la carpeta
#   .\.maestro\run.ps1 00_smoke.yaml    # ejecuta un flujo concreto
#
# Requisitos: emulador/dispositivo conectado, app instalada (./gradlew installDebug),
# Maestro en el PATH, y .maestro/secrets.local.ps1 con $env:EMAIL / $env:PASSWORD.
param([string]$Flow)

$secrets = Join-Path $PSScriptRoot "secrets.local.ps1"
if (Test-Path $secrets) { . $secrets }

if (-not $env:EMAIL -or -not $env:PASSWORD) {
    Write-Error "Faltan credenciales. Crea .maestro/secrets.local.ps1 con `$env:EMAIL y `$env:PASSWORD."
    exit 1
}

if (-not $Flow) {
    $Flow = $PSScriptRoot
} elseif ($Flow -notmatch '[\\/]') {
    $Flow = Join-Path $PSScriptRoot $Flow
}

maestro test -e "EMAIL=$($env:EMAIL)" -e "PASSWORD=$($env:PASSWORD)" $Flow
