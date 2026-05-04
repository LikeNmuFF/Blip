# 🚀 How to Build Your APK on GitHub (For 8GB RAM PCs)

Since your PC has limited RAM, we will use GitHub's powerful servers to build the APK for you.

## Step 1: Create a GitHub Repository
1. Go to [github.com/new](https://github.com/new).
2. Name it `ChatAppKotlin`.
3. Click **Create repository**.

## Step 2: Push Your Code to GitHub
Open **PowerShell** on your PC and run these commands:

```powershell
cd "C:\Users\Hp\Documents\Python Projects\Python_WebApp\ChatAppKotlin"
git init
git add .
git commit -m "Initial commit"

# Replace YOUR_USERNAME with your actual GitHub username
git remote add origin https://github.com/YOUR_USERNAME/ChatAppKotlin.git
git push -u origin main
```

## Step 3: Wait for the Build
1. Go to your repository on GitHub.
2. Click the **Actions** tab at the top.
3. You will see a workflow named "Build Android APK" running.
4. Wait for it to turn **Green** (Success). This takes about 2-5 minutes.

## Step 4: Download the APK
1. Click on the completed workflow run (the green checkmark).
2. Scroll to the bottom to **Artifacts**.
3. Click **ChatApp-Debug-APK**.
4. The `app-debug.apk` will download.

## Step 5: Install on Your Phone
1. Send the `app-debug.apk` file to your phone (via WhatsApp, email, or USB).
2. Open the file on your phone and click **Install**.
3. You might need to allow "Install from unknown sources" in your phone settings.
4. **Done!** 🎉 You can now use the chat app.

## Troubleshooting
- **Build Failed?** Check the **Actions** logs. If it says "API level not found", make sure your `app/build.gradle.kts` matches the standard versions (API 34).
- **URL Error?** If the app can't connect, make sure you updated `ApiConfig.kt` with your Railway backend URL before pushing to GitHub.
