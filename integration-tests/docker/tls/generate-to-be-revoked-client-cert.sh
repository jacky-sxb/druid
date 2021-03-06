#!/bin/bash -eu

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

tls_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=set-docker-host-ip.sh
source "$tls_dir/set-docker-host-ip.sh"

# Generate a client cert that will be revoked
cat <<EOT > revoked_csr.conf
[req]
default_bits = 1024
prompt = no
default_md = sha256
req_extensions = req_ext
distinguished_name = dn

[ dn ]
C=DR
ST=DR
L=Druid City
O=Druid
OU=RevokedIntegrationTests
emailAddress=revoked-it-cert@druid.apache.org
CN = localhost

[ req_ext ]
subjectAltName = @alt_names
basicConstraints=CA:FALSE,pathlen:0

[ alt_names ]
IP.1 = ${DOCKER_HOST_IP}
IP.2 = 127.0.0.1
IP.3 = 172.172.172.1
IP.4 = ${DOCKER_MACHINE_IP:=127.0.0.1}
DNS.1 = ${HOSTNAME}
DNS.2 = localhost

EOT

# Generate a client certificate for this machine
openssl genrsa -out revoked_client.key 4096
openssl req -new -out revoked_client.csr -key revoked_client.key -reqexts req_ext -config revoked_csr.conf
openssl x509 -req -days 3650 -in revoked_client.csr -CA root.pem -CAkey root.key -set_serial 0x11111113 -out revoked_client.pem -sha256 -extfile revoked_csr.conf -extensions req_ext

# Create a Java keystore containing the generated certificate
openssl pkcs12 -export -in revoked_client.pem -inkey revoked_client.key -out revoked_client.p12 -name revoked_druid -CAfile root.pem -caname druid-it-root -password pass:druid123
keytool -importkeystore -srckeystore revoked_client.p12 -srcstoretype PKCS12 -destkeystore revoked_client.jks -deststoretype pkcs12 -srcstorepass druid123 -deststorepass druid123
