package com.chatapp.data.network

object ApiConfig {
    // ═══════════════════════════════════════════════════════
    // CLOUD DEPLOYMENT URL (For global online access)
    // Replace with your Railway/Render URL after deployment
    // Example: "https://chatapp-production.up.railway.app"
    // ═══════════════════════════════════════════════════════
    const val BASE_URL = "https://YOUR_RAILWAY_APP.up.railway.app"
    const val SOCKET_URL = "https://YOUR_RAILWAY_APP.up.railway.app"
    
    // ═══════════════════════════════════════════════════════
    // LOCAL TESTING (Comment out the above and uncomment below)
    // For Android emulator: http://10.0.2.2:5000
    // For physical device: http://YOUR_MACHINE_IP:5000
    // ═══════════════════════════════════════════════════════
    // const val BASE_URL = "http://10.0.2.2:5000"
    // const val SOCKET_URL = "http://10.0.2.2:5000"
}
