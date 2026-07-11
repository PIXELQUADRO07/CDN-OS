# CDN OS - Security-First Android Distribution

CDN OS is a custom Android distribution focused on hardening, privacy, and offensive security auditing (pentesting).

## Project Overview
The goal of CDN OS is to provide a robust system that implements high-level security measures at the kernel, framework, and user interface levels.

## Current Roadmap Status

### 🔐 Phase 1: Kernel & System Level (Low Level) - IN PROGRESS
- [ ] Kernel Hardening (KSPP: CONFIG_SLAB_FREELIST_HARDENED, etc.)
- [ ] CFI (Control-Flow Integrity) & Shadow Call Stack
- [ ] SELinux strict policies
- [ ] Kernel Lockdown mode

### 🛡️ Phase 2: Framework & System Services - IN PROGRESS
- [x] Firewall UI (Settings integration)
- [ ] Network Security Config (TLS 1.3 enforcement)
- [ ] Permissions Hardening
- [ ] Verified Boot (AVB) hardening

### 🧰 Phase 3: Offensive Tools (Auditing) - PLANNED
- [ ] Integrated tool suite (nmap, tcpdump, metasploit, etc.)
- [ ] Advanced WiFi interface (monitor mode)
- [ ] USB Gadget Mode (HID emulation)

### 📱 Phase 4: UX/UI & Settings - IN PROGRESS
- [x] Custom Settings Dashboard
- [x] Setup Wizard (Security-first)
- [ ] Audit Log UI

## Development
This project is built on top of AOSP.

### Recent Changes
- Implemented Firewall UI in `packages/apps/Settings`.
- Expanded Settings dashboard with custom CDN OS sections.
