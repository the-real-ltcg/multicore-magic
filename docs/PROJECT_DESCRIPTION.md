Gives Minecraft's chunk mesh building its own **dedicated, tunable thread pool** — so chunk loading doesn't compete with world generation and other background tasks for CPU cores.

## The problem

Vanilla builds chunk render meshes on a thread pool it **shares** with world generation, structure searches, and other misc background work. The pool is sized automatically to `CPU cores − 1`, but every one of those threads is also up for grabs by worldgen — so chunk building is constantly competing with everything else for the same cores, especially noticeable when flying or exploring new terrain quickly.

## What this mod does

Multicore Magic redirects chunk mesh building to a **separate, dedicated pool** that nothing else touches, so it no longer contends with worldgen or IO. The pool is fully configurable and can be resized **live, in-game, with no restart required**.

**A note on scope:** this mod does not — and cannot — make the entire game "multicore." Minecraft's core tick loop is intentionally single-threaded and can't be safely parallelized wholesale. This mod targets one specific, real bottleneck (chunk mesh building) the same way mods like Lithium or Starlight target specific subsystems, rather than promising generic multithreading of the whole engine.

## Features

- Dedicated chunk-builder thread pool, fully decoupled from vanilla's shared background executor
- Live-resizable — change the thread count mid-game and see the effect immediately
- Mod Menu + Cloth Config screen with tooltips
- In-game status command for active/queued task counts
- Sensible default, tunable up to 2x your core count for experimentation

## Commands

- `/multicoremagic status` — shows enabled state, thread count, active/queued tasks
- `/multicoremagic threads <n>` — resizes the pool live, no restart needed
- `/multicoremagic on` — enables the dedicated pool
- `/multicoremagic off` — disables it, falling back to vanilla's shared pool

`on` / `off` take effect on the next world join or resource reload (F3+A); `threads` applies immediately.

The default thread count matches vanilla's own formula (CPU cores − 1). The slider goes up to cores x 2 for experimentation, but pushing it too high alongside vanilla's own worldgen pool can cause oversubscription (both pools competing for the same cores) under heavy load like flying through new terrain — dial it back if you notice stutter after raising it.

## Requirements

Two Fabric builds are published, one per supported Minecraft version:

- `multicore-magic-fabric-26.2-*.jar` — Minecraft 26.2, Fabric Loader 0.18.4+, Fabric API, Java 25
- `multicore-magic-fabric-26.1-*.jar` — Minecraft 26.1.2, Fabric Loader 0.18.4+, Fabric API, Java 25

## NeoForge support

**Works on NeoForge 26.1.2. Does NOT work on NeoForge 26.2 yet.** This mod ships metadata to run on NeoForge via [Launchpad](https://github.com/Sinytra/Launchpad), but Launchpad itself currently only has builds for Minecraft 26.1.2 — there is no 26.2 release upstream yet. So:

- On NeoForge **26.1.2**: install the `fabric-26.1` jar above with Launchpad and it works today.
- On NeoForge **26.2**: not possible right now regardless of what you install, since Launchpad hasn't released a 26.2 build. This is not a bug in this mod; it's waiting on Launchpad.

To run this on NeoForge 26.1.2, you'll need:

- [Launchpad](https://github.com/Sinytra/Launchpad) (loads the `fabric-26.1` jar directly, no separate build)
- [Forgified Fabric API](https://github.com/Sinytra/ForgifiedFabricAPI) in place of regular Fabric API

The Mod Menu config screen is not available on NeoForge (Mod Menu is Fabric-only), but all `/multicoremagic` commands work the same. Check [Launchpad's releases](https://github.com/Sinytra/Launchpad/releases) for current status before attempting the 26.2 jar on NeoForge.

## License

MIT — source on [GitHub](https://github.com/the-real-ltcg/multicore-magic).
