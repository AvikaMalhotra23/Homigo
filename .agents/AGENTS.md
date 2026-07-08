# Homigo Project Rules

## 🔁 After EVERY code change — mandatory workflow

After making **any** code change in this project, ALWAYS do these steps in one go automatically — do NOT wait to be asked:

### Step 1: Check active tunnel URL
Read the latest tunnel log to get the current active `lhr.life` URL:
```
task log: ddb117b6-15ed-492b-9de4-120304908742/task-1210 (or latest tunnel task)
```
Look for the most recent line like:
```
xxxxxxxxxxxxxxxx.lhr.life tunneled with tls termination
```

### Step 2: Update ApiClient.kt BASE_URL
File: `app/src/main/java/com/example/homigo/data/api/ApiClient.kt`
Update `BASE_URL` to `"https://<new-tunnel-id>.lhr.life/api/"` if it has changed.

### Step 3: Build release APK
```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleRelease
```

### Step 4: Deploy to emulator
```bash
~/.local/bin/android run --device=emulator-5554 --apks=app/build/outputs/apk/release/app-release.apk
```

### Step 5: Git commit and push ALL changes
```bash
git add . && git commit -m "<descriptive message>" && git push origin main
```

### Step 6: Give user the correct download link
Always tell the user:
```
https://<current-tunnel-id>.lhr.life/download-apk
```

## ⚠️ Never tell the user an old tunnel URL
Always read the CURRENT tunnel log before giving any download link.
The tunnel URL changes every time it reconnects — always verify it first.

## 🔒 GitHub push is mandatory
Every change — no matter how small — must be committed and pushed to:
https://github.com/AvikaMalhotra23/Homigo
