#!/bin/bash

CERT_DIR="/app/certs"

if [ ! -f "$CERT_DIR/private_key.pem" ]; then
  echo "Generating RSA keys..."
  mkdir -p "$CERT_DIR"
  openssl genrsa -out "$CERT_DIR/private_key_tmp.pem" 2048
  openssl rsa -in "$CERT_DIR/private_key_tmp.pem" -pubout -out "$CERT_DIR/public_key.pem"
  openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in "$CERT_DIR/private_key_tmp.pem" -out "$CERT_DIR/private_key.pem"
  rm "$CERT_DIR/private_key_tmp.pem"
  echo "Keys generated successfully."
else
  echo "Keys already exist. Skipping generation."
fi

exec java -jar app.jar