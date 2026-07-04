<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Live Monitor Panel - M3 JSON Tree</title>
    
    <style>
        /* --- Material Design 3 (M3) Dark Tokens --- */
        :root {
            --md-sys-color-background: #141218;
            --md-sys-color-on-background: #E6E1E5;
            --md-sys-color-surface: #1D1B20;
            --md-sys-color-surface-variant: #49454F;
            --md-sys-color-on-surface: #E6E1E5;
            --md-sys-color-on-surface-variant: #CAC4D0;
            --md-sys-color-primary: #D0BCFF;
            --md-sys-color-on-primary: #381E72;
            --md-sys-color-outline: #938F99;
            --md-sys-color-error: #F2B8B5;
            --md-sys-color-error-container: #8C1D18;
            
            --status-active: #82DC96;
            --status-expired: #FF8A8A;
            
            /* JSON Syntax Colors */
            --json-key: #E6E1E5;
            --json-prop: #CAC4D0;
            --json-string: #E6C07B;
            --json-number: #61AFEF;
            --json-boolean: #98C379;
            
            --motion-duration: 0.2s;
            --motion-easing: cubic-bezier(0.2, 0, 0, 1);
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background-color: var(--md-sys-color-background);
            color: var(--md-sys-color-on-background);
            padding: 24px 16px;
            display: flex;
            flex-direction: column;
            align-items: center;
            min-height: 100vh;
            -webkit-font-smoothing: antialiased;
        }

        .main-container {
            width: 100%;
            max-width: 640px;
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        /* --- Header --- */
        .header-panel {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 4px;
        }

        .header-title {
            font-size: 24px;
            font-weight: 700;
            letter-spacing: -0.5px;
        }

        .header-subtitle {
            font-size: 12px;
            color: var(--md-sys-color-primary);
            text-transform: uppercase;
            letter-spacing: 1px;
            font-weight: 600;
        }

        /* --- Connection Badge --- */
        .status-badge {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            background: #211F24;
            padding: 6px 14px;
            border-radius: 100px;
            font-size: 11px;
            font-weight: 700;
            border: 1px solid var(--md-sys-color-surface-variant);
            color: var(--status-active);
        }

        .status-badge.offline {
            color: var(--md-sys-color-error);
        }

        .pulse-dot {
            width: 6px;
            height: 6px;
            background-color: currentColor;
            border-radius: 50%;
            animation: pulse 1.5s infinite;
        }

        @keyframes pulse {
            0% { opacity: 0.4; transform: scale(0.9); }
            50% { opacity: 1; transform: scale(1.2); }
            100% { opacity: 0.4; transform: scale(0.9); }
        }

        /* --- Counters Area (Keep Intact) --- */
        .counters-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 12px;
        }

        .counter-card {
            background-color: var(--md-sys-color-surface);
            border-radius: 16px;
            padding: 16px;
            text-align: center;
            border: 1px solid rgba(255, 255, 255, 0.03);
        }

        .counter-card .num {
            font-size: 28px;
            font-weight: 700;
            color: var(--md-sys-color-primary);
        }

        .counter-card .label {
            font-size: 10px;
            color: var(--md-sys-color-on-surface-variant);
            font-weight: 600;
            letter-spacing: 0.5px;
            margin-top: 2px;
        }

        /* --- Database Tree Container (M3 Styled) --- */
        .db-tree-surface {
            background-color: var(--md-sys-color-surface);
            border-radius: 28px;
            padding: 24px;
            border: 1px solid rgba(255, 255, 255, 0.04);
            box-shadow: 0 4px 30px rgba(0, 0, 0, 0.2);
            overflow-x: auto;
        }

        .section-header {
            font-size: 12px;
            font-weight: 700;
            color: var(--md-sys-color-on-surface-variant);
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-left: 4px;
        }

        /* --- Firebase Tree System Style Structure --- */
        ul.tree-node-list, ul.tree-children {
            list-style: none;
            padding-left: 24px;
            position: relative;
        }

        ul.tree-node-list {
            padding-left: 8px;
        }

        /* Garis Vertikal Indikator Hubungan Tree */
        ul.tree-children::before {
            content: "";
            position: absolute;
            top: -10px;
            left: 6px;
            bottom: 12px;
            border-left: 1px solid var(--md-sys-color-surface-variant);
        }

        li.tree-branch, li.tree-leaf {
            position: relative;
            margin: 8px 0;
            font-family: 'Roboto Mono', 'JetBrains Mono', monospace;
            font-size: 14px;
            line-height: 24px;
        }

        /* Garis Horizontal Cabang Atribut (Gambar 1000080365_3.jpg) */
        li.tree-leaf::before {
            content: "";
            position: absolute;
            top: 12px;
            left: -18px;
            width: 14px;
            border-top: 1px solid var(--md-sys-color-surface-variant);
        }

        .tree-toggle {
            cursor: pointer;
            display: inline-block;
            width: 14px;
            font-size: 10px;
            color: var(--md-sys-color-on-surface-variant);
            user-select: none;
            transition: transform 0.1s ease;
            margin-right: 4px;
        }

        .tree-branch.collapsed > .tree-toggle {
            transform: rotate(-90deg);
        }

        .tree-branch.collapsed > .tree-children {
            display: none;
        }

        .tree-key {
            color: var(--json-key);
            font-weight: 500;
        }

        .prop-key {
            color: var(--json-prop);
        }

        /* Tipe Data Value Highlighting */
        .val-string { color: var(--json-string); }
        .val-number { color: var(--json-number); }
        .val-boolean { color: var(--json-boolean); font-weight: bold; }

        /* Row wrapper untuk meletakkan tombol sampah pas disamping Key */
        .node-wrapper {
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }

        /* Tombol Sampah Mini Per User */
        .btn-trash-mini {
            width: 26px;
            height: 26px;
            border-radius: 50%;
            background-color: transparent;
            border: none;
            color: var(--md-sys-color-outline);
            display: inline-flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: background-color var(--motion-duration), color var(--motion-duration);
        }

        .btn-trash-mini:hover {
            background-color: var(--md-sys-color-error-container);
            color: var(--md-sys-color-error);
        }

        #treeLoader {
            color: var(--md-sys-color-on-surface-variant);
            font-family: monospace;
            font-size: 13px;
        }
    </style>
</head>
<body>

    <div class="main-container">
        
        <!-- Header Panel -->
        <header class="header-panel">
            <div>
                <span class="header-subtitle">Realtime Database Console</span>
                <h1 class="header-title">Live Management</h1>
            </div>
            <div id="connStatus" class="status-badge">
                <div class="pulse-dot"></div>
                <span id="connText">Connecting</span>
            </div>
        </header>

        <!-- Tonal Analytics Counters -->
        <div class="counters-grid">
            <div class="counter-card">
                <div class="num" id="statTotal">0</div>
                <div class="label">TOTAL LISENSI</div>
            </div>
            <div class="counter-card" style="border-bottom: 3px solid var(--status-active);">
                <div class="num" id="statActive">0</div>
                <div class="label">AKTIF LISENSI</div>
            </div>
            <div class="counter-card" style="border-bottom: 3px solid var(--status-expired);">
                <div class="num" id="statInactive">0</div>
                <div class="label">MATI / EXPIRED</div>
            </div>
        </div>

        <!-- M3 Tree View Data Panel -->
        <div class="section-header">Database Hierarchy</div>
        <div class="db-tree-surface" id="treeViewerContainer">
            <div id="treeLoader">Menghubungkan & mendeteksi struktur objek di cloud...</div>
        </div>

    </div>

    <script type="module">
        import { initializeApp } from "https://www.gstatic.com/firebasejs/10.12.0/firebase-app.js";
        import { getDatabase, ref, onValue, remove } from "https://www.gstatic.com/firebasejs/10.12.0/firebase-database.js";

        // Konfigurasi Project Firebase Client
        const firebaseConfig = {
            apiKey: "AIzaSyAJG1bFDMCr9jDyG7sHn2n3cEjXXGR5eAs",
            authDomain: "hijacx-fba68.firebaseapp.com",
            databaseURL: "https://hijacx-fba68-default-rtdb.asia-southeast1.firebasedatabase.app",
            projectId: "hijacx-fba68",
            storageBucket: "hijacx-fba68.firebasestorage.app",
            messagingSenderId: "855064733267",
            appId: "1:855064733267:android:a85047d5fef49b011e48a6"
        };

        const app = initializeApp(firebaseConfig);
        const db = getDatabase(app);
        const keysRef = ref(db, 'keys');
        const connectedRef = ref(db, '.info/connected');

        // Monitor Status Jaringan Koneksi
        onValue(connectedRef, (snap) => {
            const badge = document.getElementById('connStatus');
            const text = document.getElementById('connText');
            if (snap.val() === true) {
                badge.className = "status-badge";
                text.innerText = "LIVE DATABASE";
            } else {
                badge.className = "status-badge offline";
                text.innerText = "OFFLINE";
            }
        });

        // Trigger Aksi Hapus Lisensi Per User Node
        window.deleteLicenseKey = function(keyStr) {
            if (confirm(`Hapus lisensi "${keyStr}" secara permanen dari Firebase?`)) {
                const targetRef = ref(db, `keys/${keyStr}`);
                remove(targetRef)
                    .then(() => alert("Node lisensi berhasil dibersihkan."))
                    .catch((err) => alert("Gagal menghapus: " + err.message));
            }
        };

        // Kontrol Eksplorasi Node (Expand/Collapse)
        window.toggleBranch = function(element) {
            const parentLi = element.parentElement;
            parentLi.classList.toggle('collapsed');
            element.innerText = parentLi.classList.contains('collapsed') ? '▶' : '▼';
        };

        // Render Struktur JSON Tree Persis Seperti Di Web Console Asli
        onValue(keysRef, (snapshot) => {
            const container = document.getElementById('treeViewerContainer');
            
            if (!snapshot.exists()) {
                container.innerHTML = `<div style="color: var(--md-sys-color-on-surface-variant); font-family: monospace;">keys: null</div>`;
                updateCounters(0, 0, 0);
                return;
            }

            container.innerHTML = '';
            const rootUl = document.createElement('ul');
            rootUl.className = 'tree-node-list';

            // Root Node Utama: "keys"
            const rootLi = document.createElement('li');
            rootLi.className = 'tree-branch';
            rootLi.innerHTML = `
                <span class="tree-toggle" onclick="toggleBranch(this)">▼</span>
                <span class="tree-key">keys</span>
            `;

            const childrenUl = document.createElement('ul');
            childrenUl.className = 'tree-children';

            let totalKeys = 0, activeKeys = 0, inactiveKeys = 0;
            const now = Date.now();

            // Loop seluruh data user di bawah rumpun "keys"
            snapshot.forEach((childSnap) => {
                totalKeys++;
                const childKey = childSnap.key;
                const childData = childSnap.val();

                // Hitung Statistik Status Lisensi
                const isActive = childData.is_active !== undefined ? childData.is_active : true;
                const isExpired = childData.expired_timestamp !== 0 && now > childData.expired_timestamp;
                if (!isExpired && isActive) activeKeys++; else inactiveKeys++;

                // Membuat Cabang User Key + Tombol Sampah Inline Berdampingan
                const branchLi = document.createElement('li');
                branchLi.className = 'tree-branch';
                branchLi.innerHTML = `
                    <span class="tree-toggle" onclick="toggleBranch(this)">▼</span>
                    <div class="node-wrapper">
                        <span class="tree-key">${childKey}</span>
                        <button class="btn-trash-mini" onclick="deleteLicenseKey('${childKey}')" title="Hapus User">
                            <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor"><path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/></svg>
                        </button>
                    </div>
                `;

                const leafUl = document.createElement('ul');
                leafUl.className = 'tree-children';

                // Format susunan property persis seperti screenshot 1000080365_3.jpg
                const sortedProps = ['created_at', 'device_id', 'device_limit', 'expired_date', 'expired_timestamp', 'is_active'];
                
                sortedProps.forEach(prop => {
                    if (childData[prop] !== undefined) {
                        const val = childData[prop];
                        let valSpan = '';

                        if (typeof val === 'string') {
                            valSpan = `<span class="val-string">"${val}"</span>`;
                        } else if (typeof val === 'number') {
                            valSpan = `<span class="val-number">${val}</span>`;
                        } else if (typeof val === 'boolean') {
                            valSpan = `<span class="val-boolean">${val}</span>`;
                        }

                        const leafLi = document.createElement('li');
                        leafLi.className = 'tree-leaf';
                        leafLi.innerHTML = `<span class="prop-key">${prop}:</span> ${valSpan}`;
                        leafUl.appendChild(leafLi);
                    }
                });

                branchLi.appendChild(leafUl);
                childrenUl.appendChild(branchLi);
            });

            rootLi.appendChild(childrenUl);
            rootUl.appendChild(rootLi);
            container.appendChild(rootUl);

            // Update panel pencatat total data di atas
            updateCounters(totalKeys, activeKeys, inactiveKeys);
        });

        function updateCounters(total, active, inactive) {
            document.getElementById('statTotal').innerText = total;
            document.getElementById('statActive').innerText = active;
            document.getElementById('statInactive').innerText = inactive;
        }
    </script>
</body>
</html>
