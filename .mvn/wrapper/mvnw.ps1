# Maven Wrapper (Windows), parte do download: baixa e extrai a versão do Maven fixada em
# maven-wrapper.properties para ~/.m2/wrapper. Quem roda o Maven é o mvnw.cmd.
$ErrorActionPreference = 'Stop'

$propriedades = Join-Path $PSScriptRoot 'maven-wrapper.properties'
$linha = Get-Content $propriedades | Where-Object { $_ -match '^distributionUrl=' } | Select-Object -First 1
if (-not $linha) {
  Write-Error 'distributionUrl não encontrado em .mvn/wrapper/maven-wrapper.properties'
}
$url = $linha.Substring('distributionUrl='.Length).Trim()
$nomeZip = Split-Path $url -Leaf
$nomePasta = $nomeZip -replace '-bin\.zip$', ''
$destino = Join-Path $HOME ".m2\wrapper\dists\$nomePasta"

New-Item -ItemType Directory -Force $destino | Out-Null
$zip = Join-Path $destino $nomeZip
Write-Host "Baixando $nomePasta (só na primeira vez)..."
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
Invoke-WebRequest -Uri $url -OutFile $zip -UseBasicParsing
Expand-Archive -Path $zip -DestinationPath $destino -Force
Remove-Item $zip
