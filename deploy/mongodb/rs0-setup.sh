#!/usr/bin/env bash
set -euo pipefail

# ------------------------ CONFIG -------------------------
RS_NAME="rs0"
MEMBERS=("10.1.1.18:27017" "10.1.1.19:27017" "10.1.1.20:27017")

ADMIN_USER="admin"
ADMIN_PASS="admin"

APP_DB="joinUs"
APP_USER="joinUs"
APP_PASS="joinUs"

CONTAINER="mongo1" # container name on VM1
# ---------------------------------------------------------

echo "[*] Checking mongod is reachable in container ${CONTAINER}..."
sudo docker exec -i "${CONTAINER}" mongosh --quiet --eval 'db.runCommand({ ping: 1 })' >/dev/null
echo "[+] mongod reachable."

echo "[*] Initiating replica set (or skipping if already initialized)..."
sudo docker exec -i "${CONTAINER}" mongosh \
  -u "${ADMIN_USER}" -p "${ADMIN_PASS}" --authenticationDatabase admin --quiet --eval "
(function() {
  const rsName = '${RS_NAME}';
  const members = [
    { _id: 0, host: '${MEMBERS[0]}' },
    { _id: 1, host: '${MEMBERS[1]}' },
    { _id: 2, host: '${MEMBERS[2]}' },
  ];

  function sleep(ms) { const t = Date.now(); while (Date.now() - t < ms) {} }

  // If already initialized, replSetGetStatus will succeed.
  try {
    const st = db.adminCommand({ replSetGetStatus: 1 });
    print('[+] Replica set already initialized (set=' + st.set + '). Skipping rs.initiate().');
    return;
  } catch (e) {
    const msg = (e && e.message) ? e.message : String(e);
    if (!msg.includes('NotYetInitialized') && !msg.includes('no replset config has been received')) {
      print('[!] Unexpected error checking replSetGetStatus: ' + msg);
      throw e;
    }
  }

  print('[*] Running rs.initiate() for ' + rsName + ' ...');
  rs.initiate({ _id: rsName, members });

  // Wait until PRIMARY elected (may not be this node)
  print('[*] Waiting for PRIMARY election...');
  const deadline = Date.now() + 180000;
  while (Date.now() < deadline) {
    const hello = db.adminCommand({ hello: 1 });
    if (hello.setName === rsName && hello.primary) {
      print('[+] PRIMARY is: ' + hello.primary);
      return;
    }
    sleep(1000);
  }
  throw new Error('Timed out waiting for PRIMARY election');
})();
"

echo "[*] Creating app user if missing and setting default write concern..."
sudo docker exec -i "${CONTAINER}" mongosh \
  -u "${ADMIN_USER}" -p "${ADMIN_PASS}" --authenticationDatabase admin --quiet --eval "
(function() {
  const appDb = '${APP_DB}';
  const appUser = '${APP_USER}';
  const appPass = '${APP_PASS}';

  const adminDb = db.getSiblingDB('admin');

  // Create user if missing
  const existing = adminDb.getUser(appUser);
  if (existing) {
    print('[+] User ' + appUser + ' already exists; skipping createUser.');
  } else {
    print('[*] Creating user ' + appUser + ' with readWrite on db ' + appDb + ' ...');
    adminDb.createUser({
      user: appUser,
      pwd: appPass,
      roles: [{ role: 'readWrite', db: appDb }]
    });
    print('[+] User created.');
  }

  // Set default RW concern (majority writes)
  print('[*] Setting default writeConcern to majority...');
  const res = adminDb.runCommand({
    setDefaultRWConcern: 1,
    defaultWriteConcern: { w: 'majority' }
  });
  if (res.ok !== 1) throw new Error('setDefaultRWConcern failed: ' + JSON.stringify(res));
  print('[+] Default writeConcern set.');
})();
"

echo "[*] Final replica set status (authenticated):"
sudo docker exec -it "${CONTAINER}" mongosh \
  -u "${ADMIN_USER}" -p "${ADMIN_PASS}" --authenticationDatabase admin --quiet --eval "rs.status()"

echo
echo "[+] Done."
echo "    App connection string example:"
echo "    mongodb://${APP_USER}:${APP_PASS}@${MEMBERS[0]},${MEMBERS[1]},${MEMBERS[2]}/${APP_DB}?replicaSet=${RS_NAME}&authSource=admin&readPreference=secondaryPreferred"
