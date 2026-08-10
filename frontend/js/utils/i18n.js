/* ===========================================================
   XRPShield — Internationalization (i18n) Engine
   Supports English (en), Japanese (ja), Spanish (es), German (de), Chinese (zh)
   =========================================================== */

export const TRANSLATIONS = {
    en: {
        nav_landing: "Landing overview",
        nav_treasury: "Treasury overview",
        nav_vaults: "Vault management",
        nav_policies: "Confidential policies",
        nav_decisions: "Decision engine",
        nav_executions: "Protected executions",
        nav_ai: "AI intelligence",
        nav_status: "System status",
        nav_settings: "Settings and security",
        
        btn_new_vault: "+ New FXRP Vault",
        btn_ai_builder: "🤖 AI Policy Builder",
        btn_save_prefs: "💾 Save Preferences & Language",
        btn_refresh_session: "🔑 Refresh Real Web3 Session Signature Token",
        
        title_settings_page: "Platform Settings & Profile Security",
        subtitle_settings_page: "Manage platform preferences, notifications, real Web3 wallet authentication, and Flare contract configurations",
        card_user_profile: "User Profile & Display Preferences",
        card_contracts_security: "Flare Coston2 Smart Contracts & Security",
        
        label_display_name: "Display Name",
        label_email_addr: "Email Address",
        label_display_lang: "Display Language",
        label_default_tz: "Default Timezone",
        
        title_treasury: "Treasury Portfolio Overview",
        subtitle_treasury: "Real-time confidential FXRP reserve tracking, risk metrics, and Flare TEE attestation status",
        card_connected_wallet: "Connected Web3 Wallet",
        card_active_vaults: "Active Protected Vaults",
        card_active_policies: "Active Risk Policies",
        card_total_reserves: "Total Treasury Reserves",
        status_not_connected: "Not Connected",
        
        title_vaults: "Confidential Vault Management",
        title_policies: "Confidential Risk Policies",
        title_decisions: "Flare TEE Decision Engine",
        title_executions: "Protected Treasury Execution Pipeline",
        title_ai: "AI Confidential Treasury Assistant",
        title_status: "System & Platform Status",

        msg_prefs_saved: "User preferences & display language updated successfully!"
    },
    ja: {
        nav_landing: "ランディング概要",
        nav_treasury: "財務ポートフォリオ概要",
        nav_vaults: "ボルト保護管理",
        nav_policies: "機密リスクポリシー",
        nav_decisions: "TEE意思決定エンジン",
        nav_executions: "オンチェーン保護実行",
        nav_ai: "AIインテリジェンス",
        nav_status: "システムステータス",
        nav_settings: "設定とセキュリティ",
        
        btn_new_vault: "+ 新規FXRPボルト",
        btn_ai_builder: "🤖 AIポリシービルダー",
        btn_save_prefs: "💾 設定と言語を保存",
        btn_refresh_session: "🔑 Web3セッション署名トークンを更新",
        
        title_settings_page: "プラットフォーム設定とプロファイルセキュリティ",
        subtitle_settings_page: "プラットフォーム設定、通知、Web3ウォレット認証、Flareコントラクト設定の管理",
        card_user_profile: "ユーザープロファイルと表示設定",
        card_contracts_security: "Flare Coston2スマートコントラクトとセキュリティ",
        
        label_display_name: "表示名",
        label_email_addr: "メールアドレス",
        label_display_lang: "表示言語",
        label_default_tz: "デフォルトタイムゾーン",
        
        title_treasury: "財務ポートフォリオ概要",
        subtitle_treasury: "リアルタイム機密FXRPリザーブ追跡、リスク指標、およびFlare TEE証明ステータス",
        card_connected_wallet: "接続済みWeb3ウォレット",
        card_active_vaults: "アクティブな保護ボルト",
        card_active_policies: "有効なリスクポリシー",
        card_total_reserves: "総財務リザーブ",
        status_not_connected: "未接続",
        
        title_vaults: "機密ボルト保護管理",
        title_policies: "機密リスクポリシー設定",
        title_decisions: "Flare TEE意思決定エンジン",
        title_executions: "保護された財務実行パイプライン",
        title_ai: "AI機密財務アシスタント",
        title_status: "システムおよびプラットフォームのステータス",

        msg_prefs_saved: "ユーザー設定と表示言語が正常に更新されました！"
    },
    es: {
        nav_landing: "Visión general",
        nav_treasury: "Resumen del tesoro",
        nav_vaults: "Gestión de bóvedas",
        nav_policies: "Políticas confidenciales",
        nav_decisions: "Motor de decisiones",
        nav_executions: "Ejecuciones protegidas",
        nav_ai: "Inteligencia IA",
        nav_status: "Estado del sistema",
        nav_settings: "Configuración y seguridad",
        
        btn_new_vault: "+ Nueva Bóveda FXRP",
        btn_ai_builder: "🤖 Creador de Políticas IA",
        btn_save_prefs: "💾 Guardar Preferencias e Idioma",
        btn_refresh_session: "🔑 Actualizar Token de Firma Web3",
        
        title_settings_page: "Configuración de Plataforma y Seguridad",
        subtitle_settings_page: "Gestione preferencias de plataforma, notificaciones, autenticación de billetera Web3 y contratos Flare",
        card_user_profile: "Perfil de Usuario y Preferencias de Pantalla",
        card_contracts_security: "Contratos Inteligentes Flare Coston2 y Seguridad",
        
        label_display_name: "Nombre en Pantalla",
        label_email_addr: "Correo Electrónico",
        label_display_lang: "Idioma de Pantalla",
        label_default_tz: "Zona Horaria Predeterminada",
        
        title_treasury: "Resumen del Portafolio del Tesoro",
        subtitle_treasury: "Seguimiento en tiempo real de reservas FXRP, métricas de riesgo y atestación Flare TEE",
        card_connected_wallet: "Billetera Web3 Conectada",
        card_active_vaults: "Bóvedas Protegidas Activas",
        card_active_policies: "Políticas de Riesgo Activas",
        card_total_reserves: "Reservas Totales del Tesoro",
        status_not_connected: "No Conectado",
        
        title_vaults: "Gestión de Bóvedas Confidenciales",
        title_policies: "Políticas de Riesgo Confidenciales",
        title_decisions: "Motor de Decisiones Flare TEE",
        title_executions: "Canal de Ejecución Protegida del Tesoro",
        title_ai: "Asistente Confidencial de IA del Tesoro",
        title_status: "Estado del Sistema y Plataforma",

        msg_prefs_saved: "¡Preferencias de usuario e idioma actualizados con éxito!"
    },
    de: {
        nav_landing: "Überblick",
        nav_treasury: "Tresor-Übersicht",
        nav_vaults: "Tresor-Verwaltung",
        nav_policies: "Vertrauliche Richtlinien",
        nav_decisions: "Entscheidungs-Engine",
        nav_executions: "Geschützte Ausführungen",
        nav_ai: "KI-Intelligenz",
        nav_status: "Systemstatus",
        nav_settings: "Einstellungen & Sicherheit",
        
        btn_new_vault: "+ Neuer FXRP Tresor",
        btn_ai_builder: "🤖 KI-Richtlinien-Builder",
        btn_save_prefs: "💾 Einstellungen & Sprache Speichern",
        btn_refresh_session: "🔑 Web3-Sitzungssignatur-Token Aktualisieren",
        
        title_settings_page: "Plattform-Einstellungen & Profilsicherheit",
        subtitle_settings_page: "Verwalten Sie Plattform-Einstellungen, Benachrichtigungen, Web3-Wallet-Authentifizierung und Flare-Verträge",
        card_user_profile: "Benutzerprofil & Anzeige-Einstellungen",
        card_contracts_security: "Flare Coston2 Smart Contracts & Sicherheit",
        
        label_display_name: "Anzeigename",
        label_email_addr: "E-Mail-Adresse",
        label_display_lang: "Anzeigesprache",
        label_default_tz: "Standard-Zeitzone",
        
        title_treasury: "Treasury-Portfolio-Übersicht",
        subtitle_treasury: "Echtzeit-Tracking von FXRP-Reserven, Risikometriken und Flare TEE-Attestierungsstatus",
        card_connected_wallet: "Verbundenes Web3-Wallet",
        card_active_vaults: "Aktive Geschützte Tresore",
        card_active_policies: "Aktive Risikorichtlinien",
        card_total_reserves: "Gesamte Treasury-Reserven",
        status_not_connected: "Nicht Verbunden",
        
        title_vaults: "Vertrauliche Tresor-Verwaltung",
        title_policies: "Vertrauliche Risikorichtlinien",
        title_decisions: "Flare TEE-Entscheidungs-Engine",
        title_executions: "Geschützte Treasury-Ausführungspipeline",
        title_ai: "Vertraulicher KI-Treasury-Assistent",
        title_status: "System- & Plattformstatus",

        msg_prefs_saved: "Benutzereinstellungen und Anzeigesprache erfolgreich aktualisiert!"
    },
    zh: {
        nav_landing: "首页概览",
        nav_treasury: "财库组合概览",
        nav_vaults: "金库管理",
        nav_policies: "保密风险策略",
        nav_decisions: "TEE决策引擎",
        nav_executions: "链上受保护执行",
        nav_ai: "AI 智能助理",
        nav_status: "系统运行状态",
        nav_settings: "设置与安全",
        
        btn_new_vault: "+ 新建 FXRP 金库",
        btn_ai_builder: "🤖 AI 策略生成器",
        btn_save_prefs: "💾 保存偏好与语言设置",
        btn_refresh_session: "🔑 刷新 Web3 会话签名令牌",
        
        title_settings_page: "平台设置与个人资料安全",
        subtitle_settings_page: "管理平台偏好设置、通知、Web3 钱包身份验证以及 Flare 智能合约配置",
        card_user_profile: "用户个人资料与显示偏好",
        card_contracts_security: "Flare Coston2 智能合约与安全",
        
        label_display_name: "显示名称",
        label_email_addr: "电子邮箱",
        label_display_lang: "显示语言",
        label_default_tz: "默认时区",
        
        title_treasury: "财库资产组合概览",
        subtitle_treasury: "实时保密 FXRP 储备跟踪、风险指标与 Flare TEE 硬件认证状态",
        card_connected_wallet: "已连接 Web3 钱包",
        card_active_vaults: "活跃受保护金库",
        card_active_policies: "生效风险策略",
        card_total_reserves: "财库总储备",
        status_not_connected: "未连接",
        
        title_vaults: "保密金库安全管理",
        title_policies: "保密风险控制策略",
        title_decisions: "Flare TEE 硬件决策引擎",
        title_executions: "受保护财库执行流水线",
        title_ai: "AI 保密财库智能助手",
        title_status: "系统与平台运行状态",

        msg_prefs_saved: "用户偏好与显示语言已成功更新！"
    }
};

export class I18nEngine {
    static currentLang = localStorage.getItem('xrpshield_user_language') || 'en';

    static setLanguage(lang) {
        if (TRANSLATIONS[lang]) {
            this.currentLang = lang;
            localStorage.setItem('xrpshield_user_language', lang);
            this.translatePage();
        }
    }

    static t(key) {
        const dict = TRANSLATIONS[this.currentLang] || TRANSLATIONS['en'];
        return dict[key] || TRANSLATIONS['en'][key] || key;
    }

    static translatePage() {
        const lang = this.currentLang;
        const dict = TRANSLATIONS[lang] || TRANSLATIONS['en'];

        // 1. Translate Sidebar Navigation Links
        const navMap = {
            '#landing': dict.nav_landing,
            '#dashboard': dict.nav_treasury,
            '#vaults': dict.nav_vaults,
            '#policies': dict.nav_policies,
            '#decisions': dict.nav_decisions,
            '#executions': dict.nav_executions,
            '#ai-assistant': dict.nav_ai,
            '#status': dict.nav_status,
            '#settings': dict.nav_settings
        };

        const navLinks = document.querySelectorAll('.nav-list a, .nav-menu a, .sidebar a');
        navLinks.forEach(link => {
            const href = link.getAttribute('href');
            if (href && navMap[href]) {
                const iconSvg = link.querySelector('svg')?.outerHTML || '';
                const span = link.querySelector('span');
                if (span) {
                    span.innerText = navMap[href];
                } else if (iconSvg) {
                    link.innerHTML = `${iconSvg} <span>${navMap[href]}</span>`;
                } else {
                    link.innerText = navMap[href];
                }
            }
        });

        // 2. Translate elements with data-i18n
        document.querySelectorAll('[data-i18n]').forEach(el => {
            const key = el.getAttribute('data-i18n');
            if (key && dict[key]) {
                if (el.tagName === 'INPUT' && el.type === 'button') {
                    el.value = dict[key];
                } else {
                    el.innerText = dict[key];
                }
            }
        });

        // 3. Dynamic Text Node Translation Mapping
        const textPairs = [
            ["Platform Settings & Profile Security", dict.title_settings_page],
            ["User Profile & Display Preferences", dict.card_user_profile],
            ["Flare Coston2 Smart Contracts & Security", dict.card_contracts_security],
            ["Display Name", dict.label_display_name],
            ["Email Address", dict.label_email_addr],
            ["Display Language", dict.label_display_lang],
            ["Default Timezone", dict.label_default_tz],
            ["Treasury Portfolio Overview", dict.title_treasury],
            ["Confidential Vault Management", dict.title_vaults],
            ["Confidential Risk Policies", dict.title_policies],
            ["Flare TEE Decision Engine", dict.title_decisions],
            ["Protected Treasury Execution Pipeline", dict.title_executions],
            ["AI Confidential Treasury Assistant", dict.title_ai],
            ["System & Platform Status", dict.title_status],
            ["+ New FXRP Vault", dict.btn_new_vault],
            ["🤖 AI Policy Builder", dict.btn_ai_builder],
            ["💾 Save Preferences & Language", dict.btn_save_prefs],
            ["🔑 Refresh Real Web3 Session Signature Token", dict.btn_refresh_session]
        ];

        textPairs.forEach(([enText, translatedText]) => {
            if (!translatedText || lang === 'en') return;

            // Search for headings, labels, buttons with text matching enText
            document.querySelectorAll('h1, h2, h3, h4, label, button, .card-title, .card-subtext').forEach(el => {
                if (el.innerText.trim() === enText) {
                    el.innerText = translatedText;
                }
            });
        });
    }
}
