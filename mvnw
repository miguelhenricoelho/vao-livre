#!/bin/sh
# Maven Wrapper (Linux e macOS): baixa a versão do Maven fixada em
# .mvn/wrapper/maven-wrapper.properties na primeira vez e repassa os argumentos.
set -e

RAIZ=$(cd "$(dirname "$0")" && pwd)
URL=$(sed -n 's/^distributionUrl=//p' "$RAIZ/.mvn/wrapper/maven-wrapper.properties" | tr -d '\r')
ZIP=$(basename "$URL")
PASTA=${ZIP%-bin.zip}
DESTINO="$HOME/.m2/wrapper/dists/$PASTA"
MVN="$DESTINO/$PASTA/bin/mvn"

if [ ! -x "$MVN" ]; then
  mkdir -p "$DESTINO"
  echo "Baixando $PASTA (só na primeira vez)..."
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL -o "$DESTINO/$ZIP" "$URL"
  else
    wget -q -O "$DESTINO/$ZIP" "$URL"
  fi
  unzip -q -o "$DESTINO/$ZIP" -d "$DESTINO"
  rm -f "$DESTINO/$ZIP"
fi

cd "$RAIZ"
exec "$MVN" "$@"
